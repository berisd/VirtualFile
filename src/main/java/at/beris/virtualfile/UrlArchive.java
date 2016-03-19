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
import at.beris.virtualfile.operation.FileOperationEnum;

import java.net.URL;
import java.util.List;

public class UrlArchive extends UrlFile implements Archive {

    public UrlArchive(File parent, URL url, FileModel model, FileContext context) {
        super(parent, url, model, context);
    }

    @Override
    public List<File> extract(File target) {
        return executeOperation(FileOperationEnum.EXTRACT, target, null, (Void) null);
    }

    @Override
    public void add(File file) {
        throw new NotImplementedException();
    }

    @Override
    public void delete(File file) {
        throw new NotImplementedException();
    }
}
