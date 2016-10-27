/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import java.io.InputStream;

import static at.beris.virtualfile.provider.operation.AbstractFileOperation.COPY_BUFFER_SIZE;

class StreamBufferOperationData<T, R> {
    private InputStream sourceStream;
    private T targetStream;
    private FileOperationListener listener;
    private byte[] sourceBuffer;
    private byte[] targetBuffer;
    private int sourceBytesRead;
    private long fileSize;
    private long bytesProcessedBuffer;
    private long bytesProcessedTotal;
    private R result;


    public StreamBufferOperationData(InputStream sourceStream, T targetStream, long fileSize, FileOperationListener listener) {
        this.sourceStream = sourceStream;
        this.targetStream = targetStream;
        this.fileSize = fileSize;
        this.listener = listener;
        this.sourceBuffer = new byte[COPY_BUFFER_SIZE];
        this.targetBuffer = new byte[COPY_BUFFER_SIZE];
    }

    public InputStream getSourceStream() {
        return sourceStream;
    }

    public T getTargetStream() {
        return targetStream;
    }

    public FileOperationListener getListener() {
        return listener;
    }

    public void setSourceBuffer(byte[] sourceBuffer) {
        this.sourceBuffer = sourceBuffer;
    }

    public void setSourceBytesRead(int sourceBytesRead) {
        this.sourceBytesRead = sourceBytesRead;
    }

    public long getFileSize() {
        return fileSize;
    }

    public byte[] getSourceBuffer() {
        return sourceBuffer;
    }

    public int getSourceBytesRead() {
        return sourceBytesRead;
    }

    public long getBytesProcessedBuffer() {
        return bytesProcessedBuffer;
    }

    public void setBytesProcessedBuffer(long bytesProcessedBuffer) {
        this.bytesProcessedBuffer = bytesProcessedBuffer;
    }

    public long getBytesProcessedTotal() {
        return bytesProcessedTotal;
    }

    public void setBytesProcessedTotal(long bytesProcessedTotal) {
        this.bytesProcessedTotal = bytesProcessedTotal;
    }

    public byte[] getTargetBuffer() {
        return targetBuffer;
    }

    public R getResult() {
        return result;
    }

    public void setResult(R result) {
        this.result = result;
    }
}
