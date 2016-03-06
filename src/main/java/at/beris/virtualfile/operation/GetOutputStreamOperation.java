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

import java.io.OutputStream;

public class GetOutputStreamOperation extends AbstractFileOperation<OutputStream, Void> {

    public GetOutputStreamOperation(FileContext fileContext, FileOperationProvider fileOperationProvider) {
        super(fileContext, fileOperationProvider);
    }

    @Override
    public OutputStream execute(File source, File target, Listener listener, Void... params) {
        return fileOperationProvider.getOutputStream(source.getModel());
    }
}
