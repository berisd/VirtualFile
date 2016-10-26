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

class StreamBufferOperationData<T> {
    InputStream sourceStream;
    T targetStream;
    FileOperationListener listener;
    byte[] buffer;
    int bytesRead;
    long fileSize;
    long bytesWrittenBlock;
    long BytesWrittenTotal;


    public StreamBufferOperationData(InputStream sourceStream, T targetStream, long fileSize, FileOperationListener listener) {
        this.sourceStream = sourceStream;
        this.targetStream = targetStream;
        this.fileSize = fileSize;
        this.listener = listener;
        this.buffer = new byte[COPY_BUFFER_SIZE];
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

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public void setBytesRead(int bytesRead) {
        this.bytesRead = bytesRead;
    }

    public long getFileSize() {
        return fileSize;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public int getBytesRead() {
        return bytesRead;
    }

    public long getBytesWrittenBlock() {
        return bytesWrittenBlock;
    }

    public void setBytesWrittenBlock(long bytesWrittenBlock) {
        this.bytesWrittenBlock = bytesWrittenBlock;
    }

    public long getBytesWrittenTotal() {
        return BytesWrittenTotal;
    }

    public void setBytesWrittenTotal(long bytesWrittenTotal) {
        BytesWrittenTotal = bytesWrittenTotal;
    }
}
