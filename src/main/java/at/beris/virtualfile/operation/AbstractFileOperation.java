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
import at.beris.virtualfile.provider.FileOperationProvider;

import java.io.IOException;

public abstract class AbstractFileOperation<T, P> implements FileOperation<T, P> {
    protected FileContext fileContext;
    protected FileOperationProvider fileOperationProvider;

    public AbstractFileOperation(FileContext fileContext, FileOperationProvider fileOperationProvider) {
        super();
        this.fileContext = fileContext;
        this.fileOperationProvider = fileOperationProvider;
    }

    @Override
    public abstract T execute(File source, File target, Listener listener, P... params) throws IOException;
}
