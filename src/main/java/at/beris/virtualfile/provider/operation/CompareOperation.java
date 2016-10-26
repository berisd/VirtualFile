/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
import at.beris.virtualfile.provider.FileOperationProvider;

public class CompareOperation extends AbstractFileOperation<CompareResult> {

    public CompareOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public CompareResult execute(VirtualFile source, VirtualFile target, FileOperationListener listener) {
        super.execute(source, target, listener);
        return null;
    }
}
