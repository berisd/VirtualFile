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
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.filter.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public abstract class AbstractFileOperationProvider implements FileOperationProvider {
    protected FileContext fileContext;
    protected Client client;

    public AbstractFileOperationProvider(FileContext fileContext, Client client) {
        super();
        this.fileContext = fileContext;
        this.client = client;
    }

    @Override
    public abstract Byte[] checksum(FileModel model) throws IOException;

    @Override
    public abstract void create(FileModel model) throws IOException;

    @Override
    public abstract void delete(FileModel model) throws IOException;

    @Override
    public abstract Boolean exists(FileModel model) throws IOException;

    @Override
    public abstract InputStream getInputStream(FileModel model) throws IOException;

    @Override
    public abstract OutputStream getOutputStream(FileModel model) throws IOException;

    @Override
    public abstract List<File> list(FileModel model, Filter filter) throws IOException;

    @Override
    public abstract void updateModel(FileModel model) throws IOException;

    @Override
    public abstract void setAcl(FileModel model) throws IOException;

    @Override
    public abstract void setAttributes(FileModel model) throws IOException;

    @Override
    public abstract void setCreationTime(FileModel model) throws IOException;

    @Override
    public abstract void setGroup(FileModel model) throws IOException;

    @Override
    public abstract void setLastAccessTime(FileModel model) throws IOException;

    @Override
    public abstract void setLastModifiedTime(FileModel model) throws IOException;

    @Override
    public abstract void setOwner(FileModel model) throws IOException;
}
