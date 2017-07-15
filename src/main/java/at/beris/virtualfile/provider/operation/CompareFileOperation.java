/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.UrlFileContext;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

public class CompareFileOperation extends AbstractFileOperation<Boolean, Boolean> {

    public CompareFileOperation(UrlFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
        fileOperationResult = true;
    }

    @Override
    public Boolean execute(UrlFile source, UrlFile target, FileOperationListener listener) {
        super.execute(source, target, listener);
        processFilesRecursively(source, target, listener);
        return fileOperationResult;
    }

    @Override
    protected void executeFileOperation(UrlFile source, UrlFile target, FileOperationListener listener) {
        if (source.isDirectory()) {
            if (target.isDirectory())
                fileOperationResult = source.getName().equals(target.getName());
            else
                fileOperationResult = false;
        } else {
            if (target.isDirectory())
                fileOperationResult = false;
            else {
                compareFile(source, target, listener);
                fileOperationResult &= calculateFileOperationResult();
            }
        }
    }

    private void compareFile(UrlFile source, UrlFile target, FileOperationListener listener) {
        CompareStreamBufferOperation streamBufferOperation = new CompareStreamBufferOperation();
        try (InputStream sourceStream = source.getInputStream(); InputStream targetStream = target.getInputStream()) {
            processStreams(sourceStream, targetStream, source.getSize(), listener, streamBufferOperation);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private boolean calculateFileOperationResult() {
        boolean result = true;
        for (Boolean streamBufferOperationResult : streamBufferOperationResultList) {
            result &= streamBufferOperationResult;
        }
        return result;
    }

    private class CompareStreamBufferOperation extends StreamBufferOperation<Boolean, InputStream, InputStream> {

        @Override
        Boolean process(InputStream sourceStream, InputStream targetStream, byte[] sourceBuffer, int sourceBytesRead) {
            try {
                targetStream.read(targetBuffer);
                return Arrays.equals(sourceBuffer, targetBuffer);

            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }
    }
}
