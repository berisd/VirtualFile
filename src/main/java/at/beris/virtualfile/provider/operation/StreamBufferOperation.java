/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import static at.beris.virtualfile.provider.operation.AbstractFileOperation.STREAM_BUFFER_SIZE;

public abstract class StreamBufferOperation<RS, SS, TS> {
    protected byte[] targetBuffer = new byte[STREAM_BUFFER_SIZE];

    abstract RS process(SS sourceStream, TS targetStream, byte[] sourceBuffer, int sourceBytesRead);
}
