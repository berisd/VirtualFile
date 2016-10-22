/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.provider.FileOperationProvider;

public abstract class AbstractFileOperation<T, P> implements CustomFileOperation<T, P> {
    protected FileContext fileContext;
    protected FileOperationProvider fileOperationProvider;

    public AbstractFileOperation(FileContext fileContext, FileOperationProvider fileOperationProvider) {
        super();
        this.fileContext = fileContext;
        this.fileOperationProvider = fileOperationProvider;
    }

    @Override
    public abstract T execute(VirtualFile source, VirtualFile target, Listener listener, P... params);
}
