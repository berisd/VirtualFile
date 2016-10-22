/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.provider.operation.CopyOperation;
import at.beris.virtualfile.provider.operation.CustomFileOperation;
import at.beris.virtualfile.provider.operation.FileOperation;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public abstract class AbstractFileOperationProvider<CLIENT> implements FileOperationProvider {
    protected FileContext fileContext;
    protected CLIENT client;
    protected Set<FileOperation> supportedOperations;
    protected Map<FileOperation, CustomFileOperation> customFileOperationMap;

    protected static final Set<FileOperation> BASIC_FILE_OPERATIONS = createBasicOperations();

    public AbstractFileOperationProvider(FileContext fileContext, CLIENT client) {
        super();
        this.fileContext = fileContext;
        this.client = client;
        this.supportedOperations = BASIC_FILE_OPERATIONS;
        this.customFileOperationMap = new HashMap<>();
        customFileOperationMap.put(FileOperation.COPY, new CopyOperation(fileContext, this));
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
    public void copy(VirtualFile sourceFile, VirtualFile targetFile, CopyListener listener) {
        ((CustomFileOperation<Void, Void>) customFileOperationMap.get(FileOperation.COPY)).execute(sourceFile, targetFile, listener, (Void) null);
    }

    @Override
    public void dispose() {
        fileContext = null;
        client = null;
        supportedOperations.clear();
        supportedOperations = null;
        customFileOperationMap.clear();
        customFileOperationMap = null;
    }

    private static Set<FileOperation> createBasicOperations() {
        Set<FileOperation> operations = new HashSet<>();
        operations.add(FileOperation.ADD_ATTRIBUTES);
        operations.add(FileOperation.CHECKSUM);
        operations.add(FileOperation.COPY);
        operations.add(FileOperation.CREATE);
        operations.add(FileOperation.DELETE);
        operations.add(FileOperation.EXISTS);
        operations.add(FileOperation.GET_INPUT_STREAM);
        operations.add(FileOperation.GET_OUTPUT_STREAM);
        operations.add(FileOperation.LIST);
        operations.add(FileOperation.REMOVE_ATTRIBUTES);
        operations.add(FileOperation.SET_ACL);
        operations.add(FileOperation.SET_ATTRIBUTES);
        operations.add(FileOperation.SET_CREATION_TIME);
        operations.add(FileOperation.SET_GROUP);
        operations.add(FileOperation.SET_LAST_ACCESS_TIME);
        operations.add(FileOperation.SET_LAST_MODIFIED_TIME);
        operations.add(FileOperation.SET_OWNER);
        operations.add(FileOperation.UPDATE_MODEL);
        return operations;
    }
}
