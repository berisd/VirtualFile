/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.operation;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class CopyOperation implements FileOperation {
    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    private long filesProcessed;

    @Override
    public void execute(File source) {
        throw new NotImplementedException("Call with parameters source, target and listener");
    }

    @Override
    public void execute(File source, Listener listener) {
        throw new NotImplementedException("Call with parameters source, target and listener");
    }

    @Override
    public void execute(File source, File target) {
        execute(source, target, null);
    }

    @Override
    public void execute(File source, File target, Listener listener) {
        filesProcessed = 0L;
        try {
            if (source.isDirectory() && !target.isDirectory())
                throw new OperationNotSupportedException("Can't copy directory to a file!");
            if (!source.isDirectory() && target.isDirectory())
                target = FileManager.newFile(FileUtils.newUrl(target.getUrl(), source.getName()));
            copyRecursive((UrlFile) source, (UrlFile) target, (CopyListener) listener);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private void copyRecursive(UrlFile source, UrlFile target, CopyListener listener) throws IOException {
        if (target.exists()) {
            if (listener != null)
                listener.fileExists(target);
        }

        if (source.isDirectory()) {
            if (!target.exists())
                target.create();

            for (File sourceChildFile : source.getFileOperationProvider().list(source.getClient(), source.getModel(), null)) {
                URL targetUrl = target.getUrl();
                URL targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));

                UrlFile targetChildFile = (UrlFile) FileManager.newFile(target, targetChildUrl);
                copyRecursive((UrlFile) sourceChildFile, targetChildFile, listener);
            }
        } else {
            if (listener != null)
                listener.startCopyFile(source.getPath(), filesProcessed + 1);
            copyFile(source, target, listener);
            filesProcessed++;
        }
        target.refresh();
    }

    private void copyFile(UrlFile source, UrlFile target, CopyListener listener) throws IOException {
        InputStream inputStream = source.getInputStream();
        OutputStream outputStream = target.getOutputStream();
        byte[] buffer = new byte[COPY_BUFFER_SIZE];

        long bytesWrittenTotal = 0;
        long bytesWrittenBlock = 0;
        int length;
        try {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                bytesWrittenBlock = length;
                bytesWrittenTotal += bytesWrittenBlock;
                if (listener != null)
                    listener.afterBlockCopied(source.getSize(), bytesWrittenBlock, bytesWrittenTotal);
                if (listener != null && listener.interrupt())
                    break;
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }
}
