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

public class SetAclOperation extends AbstractFileOperation<Void, Void> {

    public SetAclOperation(FileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public Void execute(File source, File target, Listener listener, Void... params) throws IOException {
        fileOperationProvider.setAcl(source.getModel());
        return null;
    }
}
