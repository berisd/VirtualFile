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
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFileOperation<RF, RB> {

    public final static int STREAM_BUFFER_SIZE = 1024 * 16;

    protected VirtualFileContext fileContext;
    protected FileOperationProvider fileOperationProvider;
    protected Integer filesProcessed = 0;

    protected RF fileOperationResult;
    protected List<RB> streamBufferOperationResultList;

    public AbstractFileOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super();
        this.fileContext = fileContext;
        this.fileOperationProvider = fileOperationProvider;
        this.streamBufferOperationResultList = new ArrayList<>();
    }

    public RF execute(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        if (!source.exists())
            throw new VirtualFileException(Message.NO_SUCH_FILE(source.getPath()));
        return null;
    }

    protected void processFilesRecursively(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        executeIteration(source, target, listener);
        if (source.isDirectory()) {
            for (VirtualFile sourceChildFile : source.list()) {
                source = sourceChildFile;
                URL targetUrl = target.getUrl();
                URL targetChildUrl;
                try {
                    targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
                }
                VirtualFile parentTarget = target;
                target = fileContext.newFile(targetChildUrl);
                processFilesRecursively(source, target, listener);
                target = parentTarget;
            }
        }
        target.refresh();
    }

    protected <SS extends InputStream, TS> void processStreams(SS sourceStream, TS targetStream, long sourceFileSize, FileOperationListener listener, StreamBufferOperation<RB, SS, TS> streamBufferOperation) throws IOException {

        byte[] sourceBuffer = new byte[STREAM_BUFFER_SIZE];
        int sourceBytesRead;
        long bytesProcessedTotal = 0;

        while ((sourceBytesRead = sourceStream.read(sourceBuffer)) > 0) {
            RB result = streamBufferOperation.process(sourceStream, targetStream, sourceBuffer, sourceBytesRead);
            bytesProcessedTotal += sourceBytesRead;
            streamBufferOperationResultList.add(result);

            if (listener != null) {
                listener.afterStreamBufferProcessed(sourceFileSize, sourceBytesRead, bytesProcessedTotal);
                if (listener.interrupt())
                    break;
            }
        }
    }

    private void executeIteration(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        if (listener != null)
            listener.startProcessingFile(source, filesProcessed + 1);
        executeFileOperation(source, target, listener);
        if (listener != null)
            listener.finishedProcessingFile(source);
        filesProcessed++;
    }

    abstract protected void executeFileOperation(VirtualFile source, VirtualFile target, FileOperationListener listener);

}
