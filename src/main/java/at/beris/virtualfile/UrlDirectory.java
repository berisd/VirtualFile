/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;

import java.net.URL;
import java.util.Map;

public class UrlDirectory extends UrlFileContainer implements Directory {
    public UrlDirectory(File parent, URL url, FileModel model, Map<FileOperationEnum, FileOperation> fileOperationMap, Site site) {
        super(parent, url, model, fileOperationMap, site);
    }
}
