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
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.exception.VirtualFileException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Optional;

public class CopyOperation {
    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    private long filesProcessed;

    public CopyOperation(IFile sourceFile, IFile targetFile, CopyListener listener) {
        filesProcessed = 0L;
        try {
            copyRecursive((File) sourceFile, (File) targetFile, listener);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private void copyRecursive(File sourceFile, File targetFile, CopyListener listener) throws IOException {
        if (targetFile.exists()) {
            listener.fileExists(targetFile);
        }

        if (sourceFile.isDirectory()) {
            if (!targetFile.exists())
                targetFile.create();

            for (IFile sourceChildFile : sourceFile.getFileOperationProvider().list(sourceFile.getClient(), sourceFile.getModel(), null)) {
                URL targetUrl = targetFile.getUrl();
                URL targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));

                File targetChildFile = (File) FileManager.newFile(targetFile, targetChildUrl);
                copyRecursive((File) sourceChildFile, targetChildFile, listener);
            }
        } else {
            listener.startCopyFile(sourceFile.getPath(), filesProcessed + 1);
            copyFile(sourceFile, targetFile, listener);
            filesProcessed++;
        }
        targetFile.refresh();
    }

    private void copyFile(File sourceFile, File targetFile, CopyListener listener) throws IOException {
        InputStream inputStream = sourceFile.getInputStream();
        OutputStream outputStream = targetFile.getOutputStream();
        byte[] buffer = new byte[COPY_BUFFER_SIZE];

        long bytesWrittenTotal = 0;
        long bytesWrittenBlock = 0;
        int length;
        try {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
                bytesWrittenBlock = length;
                bytesWrittenTotal += bytesWrittenBlock;
                listener.afterBlockCopied(sourceFile.getSize(), bytesWrittenBlock, bytesWrittenTotal);
                if (listener.interrupt())
                    break;
            }
        } finally {
            inputStream.close();
            outputStream.close();
        }
    }
}
