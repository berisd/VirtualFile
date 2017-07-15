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
import at.beris.virtualfile.VirtualFile;
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

    protected UrlFileContext fileContext;
    protected FileOperationProvider fileOperationProvider;
    protected Integer filesProcessed = 0;

    protected RF fileOperationResult;
    protected List<RB> streamBufferOperationResultList;

    public AbstractFileOperation(UrlFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super();
        this.fileContext = fileContext;
        this.fileOperationProvider = fileOperationProvider;
        this.streamBufferOperationResultList = new ArrayList<>();
    }

    public RF execute(UrlFile source, UrlFile target, FileOperationListener listener) {
        if (!source.exists())
            throw new VirtualFileException(Message.FILE_NOT_FOUND(source.getPath()));
        return null;
    }

    protected void processFilesRecursively(UrlFile source, UrlFile target, FileOperationListener listener) {
        executeIteration(source, target, listener);
        if (source.isDirectory()) {
            for (VirtualFile sourceChildFile : source.list()) {
                source = (UrlFile)sourceChildFile;
                URL targetUrl = target.getUrl();
                URL targetChildUrl;
                try {
                    targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
                }
                UrlFile parentTarget = target;
                target = fileContext.resolveFile(targetChildUrl);
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

    private void executeIteration(UrlFile source, UrlFile target, FileOperationListener listener) {
        if (listener != null)
            listener.startProcessingFile(source, filesProcessed + 1);
        executeFileOperation(source, target, listener);
        if (listener != null)
            listener.finishedProcessingFile(source);
        filesProcessed++;
    }

    abstract protected void executeFileOperation(UrlFile source, UrlFile target, FileOperationListener listener);

}
