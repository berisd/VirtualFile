/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import at.beris.virtualfile.File;

public interface CopyListener extends Listener {
    void startFile(File file, long currentFileNumber);

    void finishedFile(File file);

    void afterBlockCopied(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal);

    boolean interrupt();

    /**
     * @param file
     * @return true: continue with current file, false: continue with next file
     */
    boolean fileExists(File file);
}
