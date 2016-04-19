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
    void startCopyFile(String fileName, long currentFileNumber);

    void afterBlockCopied(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal);

    boolean interrupt();

    void fileExists(File file);
}
