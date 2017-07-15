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

public interface FileOperationListener {
    /**
     * @param file File
     * @return true: continue with current file, false: continue with next file
     */
    boolean fileExists(VirtualFile file);

    void startProcessingFile(VirtualFile file, long currentFileNumber);

    void finishedProcessingFile(VirtualFile file);

    void afterStreamBufferProcessed(long fileSize, long bytesProcessed, long bytesProcessedTotal);

    boolean interrupt();
}
