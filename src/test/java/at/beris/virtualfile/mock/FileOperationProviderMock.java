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
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.FileOperation;
import at.beris.virtualfile.provider.operation.FileOperationListener;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public class FileOperationProviderMock implements FileOperationProvider {
    private boolean exists;

    @Override
    public Object getClient() {
        return null;
    }

    @Override
    public Byte[] checksum(FileModel model) {
        return new Byte[0];
    }

    @Override
    public void create(FileModel model) {
        exists = true;
    }

    @Override
    public void delete(FileModel model) {
        exists = false;
    }

    @Override
    public Boolean exists(FileModel model) {
        return exists;
    }

    @Override
    public InputStream getInputStream(FileModel model) {
        return null;
    }

    @Override
    public OutputStream getOutputStream(FileModel model) {
        return null;
    }

    @Override
    public List<UrlFile> list(FileModel model, Filter filter) {
        return null;
    }

    @Override
    public void updateModel(FileModel model) {

    }

    @Override
    public void setAcl(FileModel model) {

    }

    @Override
    public void setAttributes(FileModel model) {

    }

    @Override
    public void setCreationTime(FileModel model) {

    }

    @Override
    public void setGroup(FileModel model) {

    }

    @Override
    public void setLastAccessTime(FileModel model) {

    }

    @Override
    public void setLastModifiedTime(FileModel model) {

    }

    @Override
    public void setOwner(FileModel model) {

    }

    @Override
    public List<UrlFile> extract(FileModel model, UrlFile target) {
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
    public void add(FileModel model, UrlFile file) {

    }

    @Override
    public void addAttributes(FileModel model) {

    }

    @Override
    public void removeAttributes(FileModel model) {

    }

    @Override
    public void rename(FileModel model, String newName) {

    }

    @Override
    public void move(FileModel model, UrlFile targetFile) {
        throw new OperationNotSupportedException();
    }

    @Override
    public Integer copy(UrlFile sourceFile, UrlFile targetFile, FileOperationListener listener) {
        return 0;
    }

    @Override
    public Boolean compare(UrlFile sourceFile, UrlFile targetFile, FileOperationListener listener) {
        return null;
    }

    @Override
    public void dispose() {

    }

    @Override
    public boolean isReadable(FileModel model) {
        return false;
    }

    @Override
    public boolean isWritable(FileModel model) {
        return false;
    }

    @Override
    public boolean isExecutable(FileModel model) {
        return false;
    }

    @Override
    public boolean isHidden(FileModel model) {
        return false;
    }
}
