/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.util.FileUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

public class UrlFile implements File, Comparable<UrlFile> {

    private File parent;
    private FileModel model;
    private URL url;
    private FileOperationProvider fileOperationProvider;
    private FileContext context;

    public UrlFile(File parent, URL url, FileContext context) {
        this.parent = parent;
        this.url = url;
        this.context = context;
        this.fileOperationProvider = context.getFileOperationProvider(url.toString());
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public FileModel getModel() throws IOException {
        checkModel();
        return model;
    }

    @Override
    public String getName() throws IOException {
        checkModel();
        return FileUtils.getName(model.getUrl().getPath());
    }

    @Override
    public FileTime getCreationTime() throws IOException {
        checkModel();
        return model.getCreationTime();
    }

    @Override
    public FileTime getLastModifiedTime() throws IOException {
        checkModel();
        return model.getLastModifiedTime();
    }

    @Override
    public FileTime getLastAccessTime() throws IOException {
        checkModel();
        return model.getLastAccessTime();
    }

    @Override
    public long getSize() throws IOException {
        checkModel();
        return model.getSize();
    }

    @Override
    public String getPath() throws IOException {
        checkModel();
        return model.getUrl().getPath();
    }

    @Override
    public void delete() throws IOException {
        checkModel();
        fileOperationProvider.delete(model);
    }

    @Override
    public void delete(File file) throws IOException {
        checkModel();
        throw new NotImplementedException();
    }

    @Override
    public void dispose() {
        if (model != null) {
            model.clear();
            model = null;
        }
        parent = null;
        url = null;
        context = null;
        fileOperationProvider = null;
    }

    @Override
    public Byte[] checksum() throws IOException {
        checkModel();
        return fileOperationProvider.checksum(model);
    }

    @Override
    public boolean isDirectory() throws IOException {
        checkModel();
        return model.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() throws IOException {
        checkModel();
        return model.isSymbolicLink();
    }

    @Override
    public boolean isContainer() throws IOException {
        checkModel();
        return isArchive() || isDirectory();
    }

    @Override
    public File getParent() {
        return parent;
    }

    @Override
    public File getRoot() throws IOException {
        checkModel();
        File root = this;
        while (root.getParent() != null)
            root = root.getParent();
        return root;
    }

    @Override
    public boolean isRoot() throws IOException {
        checkModel();
        return this.toString().equals(getRoot() != null ? getRoot().toString() : "");
    }

    @Override
    public Boolean exists() throws IOException {
        checkModel();
        return fileOperationProvider.exists(model);
    }

    @Override
    public List<File> extract(File target) throws IOException {
        checkModel();
        return fileOperationProvider.extract(model, target);
    }

    @Override
    public void create() throws IOException {
        checkModel();
        fileOperationProvider.create(model);
        updateModel();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        checkModel();
        return fileOperationProvider.getInputStream(model);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        checkModel();
        return fileOperationProvider.getOutputStream(model);
    }

    @Override
    public List<AclEntry> getAcl() throws IOException {
        checkModel();
        return model.getAcl();
    }

    @Override
    public void setAcl(List<AclEntry> acl) throws IOException {
        checkModel();
        model.setAcl(acl);
        fileOperationProvider.setAcl(model);
        updateModel();
    }

    @Override
    public UserPrincipal getOwner() throws IOException {
        checkModel();
        return model.getOwner();
    }

    @Override
    public void setOwner(UserPrincipal owner) throws IOException {
        checkModel();
        model.setOwner(owner);
        fileOperationProvider.setOwner(model);
        updateModel();
    }

    @Override
    public void setUrl(URL url) throws IOException {
        this.url = url;
    }

    @Override
    public GroupPrincipal getGroup() throws IOException {
        checkModel();
        return model.getGroup();
    }

    @Override
    public void setGroup(GroupPrincipal group) throws IOException {
        checkModel();
        model.setGroup(group);
        fileOperationProvider.setGroup(model);
        updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) throws IOException {
        checkModel();
        model.setLastAccessTime(time);
        fileOperationProvider.setLastAccessTime(model);
        updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) throws IOException {
        checkModel();
        model.setLastModifiedTime(time);
        fileOperationProvider.setLastModifiedTime(model);
        updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) throws IOException {
        checkModel();
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        fileOperationProvider.setAttributes(model);
        updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) throws IOException {
        checkModel();
        model.setCreationTime(time);
        fileOperationProvider.setCreationTime(model);
        updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() throws IOException {
        checkModel();
        return model.getAttributes();
    }

    @Override
    public void add(File file) throws IOException {
        checkModel();
        fileOperationProvider.add(model, file);
    }

    @Override
    public void addAttributes(FileAttribute... attributes) throws IOException {
        checkModel();
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.addAttribute(attribute);

        fileOperationProvider.addAttributes(model);
        updateModel();
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) throws IOException {
        checkModel();
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.removeAttribute(attribute);

        fileOperationProvider.removeAttributes(model);
        updateModel();
    }

    @Override
    public List<File> find(Filter filter) throws IOException {
        checkModel();
        Filter directoriesFilter = new IsDirectoryFilter().equalTo(true);
        Filter withDirectoriesFilter = ((Filter) filter.clone()).or(new IsDirectoryFilter().equalTo(true));

        List<File> fileList = fileOperationProvider.list(model, withDirectoriesFilter);

        Map<Filter, List<File>> partitionedFileList = FileUtils.groupFileListByFilters(fileList, Arrays.asList(filter, directoriesFilter));

        fileList.clear();
        fileList = partitionedFileList.get(filter);
        List<File> directoryList = partitionedFileList.get(directoriesFilter);

        for (File directory : directoryList) {
            fileList.addAll(directory.find(filter));
        }
        directoryList.clear();

        return fileList;
    }

    @Override
    public List<File> list() throws IOException {
        checkModel();
        return fileOperationProvider.list(model, null);
    }

    @Override
    public List<File> list(Filter filter) throws IOException {
        checkModel();
        return fileOperationProvider.list(model, filter);
    }

    @Override
    public boolean isArchive() throws IOException {
        checkModel();
        return model.isArchive();
    }

    @Override
    public boolean isArchived() throws IOException {
        checkModel();
        return model.isArchived();
    }

    @Override
    public void copy(File targetFile) throws IOException {
        checkModel();
        fileOperationProvider.copy(this, targetFile, null);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) throws IOException {
        checkModel();
        fileOperationProvider.copy(this, targetFile, listener);
    }

    @Override
    public void refresh() throws IOException {
        checkModel();
        updateModel();
    }

    @Override
    public String toString() {
        return this.getClass().getInterfaces()[0].getSimpleName() + ((model != null && model.getUrl() != null) ? " " + maskedUrlString(model.getUrl()) :
                "@" + Integer.toHexString(System.identityHashCode(this)));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UrlFile otherFile = (UrlFile) o;

        return compareTo(otherFile) == 0;
    }

    @Override
    public int hashCode() {
        return model.getUrl().toString().hashCode();
    }

    @Override
    public int compareTo(UrlFile o) {
        return model.getUrl().toString().compareTo(o.getUrl().toString());
    }

    @Override
    public void setModel(FileModel model) throws IOException {
        this.model = model;
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
    }

    void updateModel() throws IOException {
        fileOperationProvider.updateModel(model);
    }

    private void checkModel() throws IOException {
        if (model == null)
            createModel();
        if (!model.getUrl().toString().equals(url.toString())) {
            context.replaceFileUrl(url, model.getUrl());
        }
    }

    private void createModel() throws IOException {
        model = new FileModel();
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
        updateModel();
    }
}
