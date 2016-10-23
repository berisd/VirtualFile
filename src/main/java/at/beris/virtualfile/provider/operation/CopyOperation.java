/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;

public class CopyOperation extends AbstractFileOperation<Void, Void> {
    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    private long filesProcessed;

    public CopyOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public Void execute(VirtualFile source, VirtualFile target, Listener listener, Void... params) {
        filesProcessed = 0L;
        if (!source.exists())
            throw new VirtualFileException(Message.NO_SUCH_FILE(source.getPath()));
        if (source.isDirectory() && !target.isDirectory())
            throw new OperationNotSupportedException("Can't copy directory to a file!");
        if (!source.isDirectory() && target.isDirectory())

            target = fileContext.newFile(UrlUtils.newUrl(target.getUrl(), source.getName()));
        copyRecursive(source, target, (CopyListener) listener);
        return null;
    }

    private void copyRecursive(VirtualFile source, VirtualFile target, CopyListener listener) {
        boolean createFile = true;
        if (target.exists()) {
            if (listener != null)
                createFile = listener.fileExists(target);
        }

        if (source.isDirectory()) {
            if (createFile) {
                if (!target.exists())
                    target.create();

                for (VirtualFile sourceChildFile : source.list()) {
                    URL targetUrl = target.getUrl();
                    URL targetChildUrl = null;
                    try {
                        targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));
                    } catch (MalformedURLException e) {
                        throw new VirtualFileException(e);
                    }
                    VirtualFile targetChildFile = fileContext.newFile(targetChildUrl);
                    copyRecursive(sourceChildFile, targetChildFile, listener);
                }
            }
        } else {
            if (createFile) {
                if (listener != null)
                    listener.startFile(source, filesProcessed + 1);
                copyFile(source, target, listener);
                if (listener != null)
                    listener.finishedFile(source);
            }
            filesProcessed++;
        }
        target.refresh();
    }

    private void copyFile(VirtualFile source, VirtualFile target, CopyListener listener) {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];

        long bytesWrittenTotal = 0;
        long bytesWrittenBlock = 0;
        int length;
        try (InputStream inputStream = source.getInputStream(); OutputStream outputStream = target.getOutputStream()) {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                bytesWrittenBlock = length;
                bytesWrittenTotal += bytesWrittenBlock;
                if (listener != null)
                    listener.afterBlockCopied(source.getSize(), bytesWrittenBlock, bytesWrittenTotal);
                if (listener != null && listener.interrupt())
                    break;
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }
}
