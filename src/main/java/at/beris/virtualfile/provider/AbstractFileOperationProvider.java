/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.filter.Filter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class AbstractFileOperationProvider implements FileOperationProvider {
    protected FileContext fileContext;

    public AbstractFileOperationProvider(FileContext fileContext) {
        super();
        this.fileContext = fileContext;
    }

    @Override
    public abstract Byte[] checksum(FileModel model);

    @Override
    public abstract void create(FileModel model);

    @Override
    public abstract void delete(FileModel model);

    @Override
    public abstract Boolean exists(FileModel model);

    @Override
    public abstract InputStream getInputStream(FileModel model);

    @Override
    public abstract OutputStream getOutputStream(FileModel model);

    @Override
    public abstract List<File> list(FileModel model, Filter filter);

    @Override
    public abstract void updateModel(FileModel model);

    @Override
    public abstract void setAcl(FileModel model);

    @Override
    public abstract void setAttributes(FileModel model);

    @Override
    public abstract void setCreationTime(FileModel model);

    @Override
    public abstract void setGroup(FileModel model);

    @Override
    public abstract void setLastAccessTime(FileModel model);

    @Override
    public abstract void setLastModifiedTime(FileModel model);

    @Override
    public abstract void setOwner(FileModel model);
}
