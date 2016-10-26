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
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.util.UrlUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;

public class CopyOperation extends AbstractFileOperation<Integer> {

    public CopyOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public Integer execute(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        super.execute(source, target, listener);
        if (source.isDirectory() && !target.isDirectory())
            throw new OperationNotSupportedException("Can't copy directory to a file!");
        if (!source.isDirectory() && target.isDirectory())
            target = fileContext.newFile(UrlUtils.newUrl(target.getUrl(), source.getName()));
        CopyFileIterationLogic iterationLogic = new CopyFileIterationLogic(source, target, listener);
        iterateFilesRecursively(iterationLogic);
        return iterationLogic.getFilesProcessed();
    }

    private class CopyFileIterationLogic extends FileIterationLogic<FileOperationListener> {

        public CopyFileIterationLogic(VirtualFile source, VirtualFile target, FileOperationListener listener) {
            super(source, target, listener);
        }

        @Override
        public void executeOperation() {
            copyFile(source, target, listener);
        }

        private void copyFile(VirtualFile source, VirtualFile target, FileOperationListener listener) {
            try (InputStream inputStream = source.getInputStream(); OutputStream outputStream = target.getOutputStream()) {
                processStreams(new StreamBufferOperationData<>(inputStream, outputStream, source.getSize(), listener), new CopyStreamBufferOperation());
            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }
    }

    private class CopyStreamBufferOperation implements Consumer<StreamBufferOperationData<OutputStream>> {

        @Override
        public void accept(StreamBufferOperationData<OutputStream> data) {
            try {
                data.getTargetStream().write(data.getBuffer(), 0, data.getBytesRead());
                data.setBytesWrittenBlock(data.getBytesRead());
                data.setBytesWrittenTotal(data.getBytesWrittenTotal() + data.getBytesWrittenBlock());
            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }
    }
}
