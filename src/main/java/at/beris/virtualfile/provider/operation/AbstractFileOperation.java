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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class AbstractFileOperation<T, R, RB> {

    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    protected VirtualFileContext fileContext;
    protected FileOperationProvider fileOperationProvider;

    protected R fileOperationResult;
    protected List<RB> streamBufferOperationResultList;

    public AbstractFileOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super();
        this.fileContext = fileContext;
        this.fileOperationProvider = fileOperationProvider;
        this.streamBufferOperationResultList = new ArrayList<>();
    }

    public R execute(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        if (!source.exists())
            throw new VirtualFileException(Message.NO_SUCH_FILE(source.getPath()));
        return null;
    }

    protected void iterateFilesRecursively(FileIterationLogic iterationLogic) {
        iterationLogic.executeIteration();
        if (iterationLogic.getSource().isDirectory()) {
            for (VirtualFile sourceChildFile : iterationLogic.getSource().list()) {
                iterationLogic.setSource(sourceChildFile);
                URL targetUrl = iterationLogic.getTarget().getUrl();
                URL targetChildUrl;
                try {
                    targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
                }
                VirtualFile parentTarget = iterationLogic.getTarget();
                iterationLogic.setTarget(fileContext.newFile(targetChildUrl));
                iterateFilesRecursively(iterationLogic);
                iterationLogic.setTarget(parentTarget);
            }
        }
        iterationLogic.getTarget().refresh();
    }

    protected void processStreams(StreamBufferOperationData<T, RB> data, Consumer<StreamBufferOperationData<T, RB>> consumer) throws IOException {
        byte[] buffer = new byte[COPY_BUFFER_SIZE];
        data.setSourceBuffer(buffer);
        int bytesRead;

        while ((bytesRead = data.getSourceStream().read(buffer)) > 0) {
            data.setSourceBytesRead(bytesRead);
            consumer.accept(data);
            data.setBytesProcessedBuffer(data.getSourceBytesRead());
            data.setBytesProcessedTotal(data.getBytesProcessedTotal() + data.getBytesProcessedBuffer());
            streamBufferOperationResultList.add(data.getResult());

            if (data.getListener() != null) {
                data.getListener().afterStreamBufferProcessed(data.getFileSize(), data.getBytesProcessedBuffer(), data.getBytesProcessedTotal());
                if (data.getListener().interrupt())
                    break;
            }
        }
    }

}
