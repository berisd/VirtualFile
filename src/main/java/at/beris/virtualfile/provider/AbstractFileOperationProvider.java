/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public abstract class AbstractFileOperationProvider<C> implements FileOperationProvider {

    protected static final Set<FileOperation> BASIC_FILE_OPERATIONS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(FileOperation.values())));

    protected VirtualFileContext fileContext;
    protected C client;
    protected Set<FileOperation> supportedOperations;

    public AbstractFileOperationProvider(VirtualFileContext fileContext, C client) {
        super();
        this.fileContext = fileContext;
        this.client = client;
        this.supportedOperations = BASIC_FILE_OPERATIONS;
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
    public abstract List<VirtualFile> list(FileModel model, Filter filter);

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

    @Override
    public List<VirtualFile> extract(FileModel model, VirtualFile target) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void add(FileModel model, VirtualFile file) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void addAttributes(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void removeAttributes(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public Set<FileOperation> supportedOperations() {
        return supportedOperations;
    }

    @Override
    public boolean isOperationSupported(FileOperation fileOperation) {
        return supportedOperations.contains(fileOperation);
    }

    @Override
    public Integer copy(VirtualFile sourceFile, VirtualFile targetFile, CopyListener listener) {
        return new CopyOperation(fileContext, this).execute(sourceFile, targetFile, listener);
    }

    @Override
    public CompareResult compare(VirtualFile sourceFile, VirtualFile targetFile, CompareListener listener) {
        return new CompareOperation(fileContext, this).execute(sourceFile, targetFile, listener);
    }

    @Override
    public void dispose() {
        fileContext = null;
        client = null;
        supportedOperations.clear();
        supportedOperations = null;
    }

}
