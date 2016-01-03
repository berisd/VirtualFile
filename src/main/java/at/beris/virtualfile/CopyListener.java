/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

public interface CopyListener {
    void startCopyFile(String fileName, long currentFileNumber);

    void afterBlockCopied(long fileSize, long bytesCopiedBlock, long bytesCopiedTotal);

    boolean interrupt();

    void fileExists(IFile file);
}
