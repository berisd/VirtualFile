/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.filter.BasicFilter;
import at.beris.virtualfile.filter.CollectionFilter;
import at.beris.virtualfile.filter.DefaultFilter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.operation.CopyOperation;
import at.beris.virtualfile.provider.IFileOperationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.*;

import static at.beris.virtualfile.FileUtils.maskedUrlString;

public class File implements IFile, Comparable<File> {
    private final static Logger LOGGER = LoggerFactory.getLogger(File.class);

    private IFile parent;
    private FileModel model;
    private Map<FileType, IFileOperationProvider> fileOperationProviderMap;
    private IClient client;

    public File(URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        this(null, url, model, fileOperationProviderMap, client);
    }

    public File(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap) {
        this(parent, url, model, fileOperationProviderMap, null);
    }

    public File(IFile parent, URL url, FileModel model, Map<FileType, IFileOperationProvider> fileOperationProviderMap, IClient client) {
        this.parent = parent;
        this.model = model;
        this.model.setUrl(url);
        this.fileOperationProviderMap = fileOperationProviderMap;
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
        if (isArchive())
            return fileOperationProviderMap.get(FileType.ARCHIVE);
        else if (isArchived())
            return fileOperationProviderMap.get(FileType.ARCHIVED);
        else
            return fileOperationProviderMap.get(FileType.DEFAULT);
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
        return getModel().getLastModifiedTime();
    }

    @Override
    public long getSize() {
        return model.getSize();
    }

    @Override
    public String getPath() {
        return model.getPath();
    }

    @Override
    public void delete() {
        LOGGER.info("Delete " + maskedUrlString(this));
        getFileOperationProvider().delete(client, model);
    }

    @Override
    public byte[] checksum() {
        return getFileOperationProvider().checksum(client, model);
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
    public boolean exists() {
        return getFileOperationProvider().exists(this.getClient(), this.getModel());
    }

    @Override
    public void create() {
        LOGGER.info("Create " + maskedUrlString(this));
        getFileOperationProvider().create(this.getClient(), this.getModel());
        updateModel();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return getFileOperationProvider().getInputStream(client, model);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        return getFileOperationProvider().getOutputStream(client, model);
    }

    @Override
    public Set<Attribute> getAttributes() {
        getFileOperationProvider().updateModel(client, model);
        return model.getAttributes();
    }

    @Override
    public void addAttributes(Attribute... attributes) {
        if (attributes.length < 1)
            return;

        for (Attribute attribute : attributes)
            getModel().addAttribute(attribute);
    }

    @Override
    public void removeAttributes(Attribute... attributes) {
        if (attributes.length < 1)
            return;

        for (Attribute attribute : attributes)
            getModel().removeAttribute(attribute);
    }

    @Override
    public List<IFile> list() {
        return getFileOperationProvider().list(client, model);
    }

    @Override
    public List<IFile> list(BasicFilter filter) {
        return filterList(getFileOperationProvider().list(client, model), filter);
    }

    @Override
    public List<IFile> list(DefaultFilter filter) {
        return filterList(getFileOperationProvider().list(client, model), filter);
    }

    @Override
    public List<IFile> list(CollectionFilter filter) {
        return filterList(getFileOperationProvider().list(client, model), filter);
    }

    @Override
    public boolean isArchive() {
        if (!isDirectory() && FileUtils.isArchive(getName()))
            return true;
        return false;
    }

    @Override
    public boolean isArchived() {
        IFile parent = getParent();
        while (parent != null) {
            if (parent.isArchive())
                return true;
            if (parent.isArchived())
                return true;
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    public void copy(IFile targetFile, CopyListener listener) {
        LOGGER.info("Copy " + maskedUrlString(this) + " to " + maskedUrlString(targetFile));
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File otherFile = (File) o;

        return compareTo(otherFile) == 0;
    }

    @Override
    public int hashCode() {
        return model.getUrl().toString().hashCode();
    }

    @Override
    public int compareTo(File o) {
        return model.getUrl().toString().compareTo(o.getUrl().toString());
    }

    private List<IFile> filterList(List<IFile> fileList, BasicFilter filter) {
        List<IFile> filteredList = new ArrayList<>();
        for (IFile file : fileList) {
            if (filter.filter(file)) {
                filteredList.add(file);
            }
        }
        return filteredList;
    }
}
