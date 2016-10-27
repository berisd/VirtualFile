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

public class CopyOperation extends AbstractFileOperation<OutputStream, Integer, Boolean> {

    public CopyOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
        fileOperationResult = 0;
    }

    @Override
    public Integer execute(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        super.execute(source, target, listener);
        if (source.isDirectory() && !target.isDirectory())
            throw new OperationNotSupportedException("Can't copy directory to a file!");
        if (!source.isDirectory() && target.isDirectory())
            target = fileContext.newFile(UrlUtils.newUrl(target.getUrl(), source.getName()));
        iterateFilesRecursively(new CopyFileIterationLogic(source, target, listener));
        return fileOperationResult;
    }

    private class CopyFileIterationLogic extends FileIterationLogic<FileOperationListener> {

        public CopyFileIterationLogic(VirtualFile source, VirtualFile target, FileOperationListener listener) {
            super(source, target, listener);
        }

        @Override
        public void executeOperation() {
            boolean createFile = true;
            if (target.exists()) {
                if (listener != null)
                    createFile = listener.fileExists(target);
            }

            if (createFile) {
                if (source.isDirectory()) {
                    if (!target.exists()) {
                        target.create();
                    }
                } else {
                    copyFile(source, target, listener);
                }
            }
            calculateFileOperationResult();
        }

        private void copyFile(VirtualFile source, VirtualFile target, FileOperationListener listener) {
            try (InputStream sourceStream = source.getInputStream(); OutputStream targetStream = target.getOutputStream()) {
                processStreams(new StreamBufferOperationData<>(sourceStream, targetStream, source.getSize(), listener), new CopyStreamBufferOperation());
            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }

        private void calculateFileOperationResult() {
            Boolean isFileCopySuccessful = true;
            for (Boolean streamBufferOperationResult : streamBufferOperationResultList) {
                isFileCopySuccessful &= streamBufferOperationResult;
            }

            if (isFileCopySuccessful)
                fileOperationResult++;
        }
    }

    private class CopyStreamBufferOperation implements Consumer<StreamBufferOperationData<OutputStream, Boolean>> {

        @Override
        public void accept(StreamBufferOperationData<OutputStream, Boolean> data) {
            try {
                data.setResult(true);
                data.getTargetStream().write(data.getSourceBuffer(), 0, data.getSourceBytesRead());
            } catch (IOException e) {
                data.setResult(false);
                throw new VirtualFileException(e);
            }
        }
    }
}
