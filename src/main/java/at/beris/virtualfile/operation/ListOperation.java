/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.operation;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.io.IOException;
import java.util.List;

public class ListOperation extends AbstractFileOperation<List<File>, Filter> {

    public ListOperation(FileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public List<File> execute(File source, File target, Listener listener, Filter... params) throws IOException {
        if (params.length == 1)
            return fileOperationProvider.list(source.getModel(), params[0]);
        else
            return fileOperationProvider.list(source.getModel(), null);
    }
}
