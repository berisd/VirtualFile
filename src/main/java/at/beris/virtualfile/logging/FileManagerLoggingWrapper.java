/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.logging;

import at.beris.virtualfile.File;
import org.slf4j.LoggerFactory;

public class FileManagerLoggingWrapper extends FileLoggingWrapper {

    public FileManagerLoggingWrapper(File file) {
        super(file);
        setLogger(LoggerFactory.getLogger(FileManagerLoggingWrapper.class));
    }
}