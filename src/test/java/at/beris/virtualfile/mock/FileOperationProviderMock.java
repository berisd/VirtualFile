/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.mock;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.provider.operation.FileOperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public class FileOperationProviderMock implements FileOperationProvider {
    private boolean exists;

    @Override
    public Byte[] checksum(FileModel model) throws IOException {
        return new Byte[0];
    }

    @Override
    public void create(FileModel model) throws IOException {
        exists = true;
    }

    @Override
    public void delete(FileModel model) throws IOException {
        exists = false;
    }

    @Override
    public Boolean exists(FileModel model) throws IOException {
        return exists;
    }

    @Override
    public InputStream getInputStream(FileModel model) throws IOException {
        return null;
    }

    @Override
    public OutputStream getOutputStream(FileModel model) throws IOException {
        return null;
    }

    @Override
    public List<VirtualFile> list(FileModel model, Filter filter) throws IOException {
        return null;
    }

    @Override
    public void updateModel(FileModel model) throws IOException {

    }

    @Override
    public void setAcl(FileModel model) throws IOException {

    }

    @Override
    public void setAttributes(FileModel model) throws IOException {

    }

    @Override
    public void setCreationTime(FileModel model) throws IOException {

    }

    @Override
    public void setGroup(FileModel model) throws IOException {

    }

    @Override
    public void setLastAccessTime(FileModel model) throws IOException {

    }

    @Override
    public void setLastModifiedTime(FileModel model) throws IOException {

    }

    @Override
    public void setOwner(FileModel model) throws IOException {

    }

    @Override
    public List<VirtualFile> extract(FileModel model, VirtualFile target) throws IOException {
        return null;
    }

    @Override
    public boolean isOperationSupported(FileOperation fileOperation) {
        return false;
    }

    @Override
    public Set<FileOperation> supportedOperations() {
        return null;
    }

    @Override
    public void add(FileModel model, VirtualFile file) throws IOException {

    }

    @Override
    public void addAttributes(FileModel model) throws IOException {

    }

    @Override
    public void removeAttributes(FileModel model) throws IOException {

    }

    @Override
    public void copy(VirtualFile sourceFile, VirtualFile targetFile, CopyListener listener) throws IOException {

    }

    @Override
    public void dispose() {

    }
}
