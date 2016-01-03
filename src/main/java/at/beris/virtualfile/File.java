/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.FileUtils;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.operation.CopyOperation;
import at.beris.virtualfile.provider.IFileOperationProvider;
import at.beris.virtualfile.FileModel;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

public class File implements IFile {
    private final static Logger LOGGER = org.apache.log4j.Logger.getLogger(File.class);

    private IFile parent;
    private FileModel model;
    private IFileOperationProvider fileOperationProvider;
    private IClient client;

    public File(URL url, FileModel model, IFileOperationProvider fileOperationProvider, IClient client) {
        this(null, url, model, fileOperationProvider, client);
    }

    public File(IFile parent, URL url, FileModel model, IFileOperationProvider fileOperationProvider) {
        this(parent, url, model, fileOperationProvider, null);
    }

    public File(IFile parent, URL url, FileModel model, IFileOperationProvider fileOperationProvider, IClient client) {
        this.parent = parent;
        this.model = model;
        this.model.setUrl(url);
        this.fileOperationProvider = fileOperationProvider;
        this.client = client;
    }

    @Override
    public URL getUrl() {
        return model.getUrl();
    }

    @Override
    public FileModel getModel() {
        return model;
    }

    @Override
    public IClient getClient() {
        return client;
    }

    @Override
    public IFileOperationProvider getFileOperationProvider() {
        return fileOperationProvider;
    }

    @Override
    public String getName() {
        String path = model.getPath();

        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));

        return path.substring(path.lastIndexOf('/') + 1);
    }

    @Override
    public Date getLastModified() {
        return getModel().getLastModified();
    }

    @Override
    public long getSize() {
        return model.getSize();
    }

    @Override
    public void setSize(long size) {
        model.setSize(size);
    }

    @Override
    public String getPath() {
        return model.getPath();
    }

    @Override
    public void delete() {
        fileOperationProvider.delete(client, model);
    }

    @Override
    public byte[] checksum() throws IOException {
        return fileOperationProvider.checksum(client, model);
    }

    @Override
    public boolean isDirectory() {
        return model.isDirectory();
    }

    @Override
    public IFile getParent() {
        return parent;
    }

    @Override
    public IFile getRoot() {
        IFile file = this;

        while (file.getParent() != null)
            file = file.getParent();

        return file;
    }

    @Override
    public boolean isRoot() {
        return this.toString().equals(getRoot() != null ? getRoot().toString() : "");
    }

    @Override
    public void add(IFile file) {
        fileOperationProvider.add(this, file);
    }

    @Override
    public boolean exists() {
        return fileOperationProvider.exists(this.getClient(), this.getModel());
    }

    @Override
    public void create() throws IOException {
        fileOperationProvider.create(this.getClient(), this.getModel());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return fileOperationProvider.getInputStream(client, model);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return fileOperationProvider.getOutputStream(client, model);
    }

    @Override
    public Set<Attribute> getAttributes() {
        return model.getAttributes();
    }

    @Override
    public List<IFile> list() throws IOException {
        return fileOperationProvider.list(client, model);
    }

    @Override
    public void setParent(IFile parent) {
        this.parent = parent;
    }

    @Override
    public boolean isArchive() {
        List<String> archiveExtensions = FileUtils.getArchiveExtensions();

        for (String extension : archiveExtensions) {
            if (getName().toUpperCase().endsWith("." + extension) && !isDirectory())
                return true;
        }
        return false;
    }

    @Override
    public boolean isArchived() {
        IFile parent = getParent();
        while (parent != null) {
            if (parent.isArchived())
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public void copy(IFile targetFile, CopyListener listener) throws IOException {
        LOGGER.info("Copy " + model.getUrl().toString() + " to " + targetFile.getUrl().toString());
        new CopyOperation(this, targetFile, listener);
    }

    /**
     * Updates the model with information from the physical file
     */
    public void updateModel() {
        getFileOperationProvider().updateModel(client, model);
    }

    @Override
    public String toString() {
        return model.getUrl().toString();
    }

    @Override
    public int compareTo(IFile file) {
        return file.getModel().getUrl().toString().compareTo(file.getUrl().toString());
    }
}
