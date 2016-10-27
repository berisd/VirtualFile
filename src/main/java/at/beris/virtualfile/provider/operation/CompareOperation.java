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
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.function.Consumer;

public class CompareOperation extends AbstractFileOperation<InputStream, Boolean, Boolean> {

    public CompareOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
        fileOperationResult = false;
    }

    @Override
    public Boolean execute(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        super.execute(source, target, listener);
        CompareFileIterationLogic iterationLogic = new CompareFileIterationLogic(source, target, listener);
        iterateFilesRecursively(iterationLogic);
        return fileOperationResult;
    }

    private class CompareFileIterationLogic extends FileIterationLogic<FileOperationListener> {

        public CompareFileIterationLogic(VirtualFile source, VirtualFile target, FileOperationListener listener) {
            super(source, target, listener);
        }

        @Override
        public void executeOperation() {
            compareFile(source, target, listener);
            calculateFileOperationResult();
        }

        private void compareFile(VirtualFile source, VirtualFile target, FileOperationListener listener) {
            try (InputStream sourceStream = source.getInputStream(); InputStream targetStream = target.getInputStream()) {
                processStreams(new StreamBufferOperationData<>(sourceStream, targetStream, source.getSize(), listener), new CompareStreamBufferOperation());
            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }

        private void calculateFileOperationResult() {
            fileOperationResult = true;
            for (Boolean streamBufferOperationResult : streamBufferOperationResultList) {
                fileOperationResult &= streamBufferOperationResult;
            }
        }
    }

    private class CompareStreamBufferOperation implements Consumer<StreamBufferOperationData<InputStream, Boolean>> {

        @Override
        public void accept(StreamBufferOperationData<InputStream, Boolean> data) {
            try {
                data.getTargetStream().read(data.getTargetBuffer());
                data.setResult(Arrays.equals(data.getSourceBuffer(), data.getTargetBuffer()));
            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }
    }
}
