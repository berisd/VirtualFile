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

public class CopyOperation extends AbstractFileOperation<Long, CopyListener> {

    public CopyOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public Long execute(VirtualFile source, VirtualFile target, CopyListener listener) {
        super.execute(source, target, listener);
        if (source.isDirectory() && !target.isDirectory())
            throw new OperationNotSupportedException("Can't copy directory to a file!");
        if (!source.isDirectory() && target.isDirectory())
            target = fileContext.newFile(UrlUtils.newUrl(target.getUrl(), source.getName()));
        CopyFileIterationLogic iterationLogic = new CopyFileIterationLogic(source, target, listener);
        iterateRecursively(iterationLogic);
        return iterationLogic.getFilesProcessed();
    }

    private class CopyFileIterationLogic extends FileIterationLogic<CopyListener> {

        private boolean createFile;

        public CopyFileIterationLogic(VirtualFile source, VirtualFile target, CopyListener listener) {
            super(source, target, listener);
        }

        @Override
        public void before() {
            createFile = true;
            if (target.exists()) {
                if (listener != null)
                    createFile = listener.fileExists(target);
            }
            if (source.isDirectory() && createFile && !target.exists()) {
                target.create();
            }
        }

        @Override
        public void execute() {
            if (createFile) {
                if (listener != null)
                    listener.startFile(source, filesProcessed + 1);
                copyFile(source, target, listener);
                if (listener != null)
                    listener.finishedFile(source);
            }
            filesProcessed++;
        }
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
