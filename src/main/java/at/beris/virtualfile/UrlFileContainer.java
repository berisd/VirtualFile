/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;


import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;

import java.net.URL;
import java.util.Map;

public class UrlFileContainer extends UrlFile implements FileContainer {
    public UrlFileContainer(File parent, URL url, FileModel model, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationMap);
    }

    @Override
    public void add(File file) {
        //TODO Rework this
//        getFileOperationProvider().add(this, file);
    }

    @Override
    public void delete(File file) {
        throw new NotImplementedException();
    }
}
