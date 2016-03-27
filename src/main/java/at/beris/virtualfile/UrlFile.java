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
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.operation.Listener;
import at.beris.virtualfile.util.FileUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;

import static at.beris.virtualfile.util.FileUtils.maskedUrlString;

public class UrlFile implements File, Comparable<UrlFile> {

    protected File parent;
    protected FileModel model;
    protected FileContext context;

    private Map<FileOperationEnum, FileOperation> fileOperationMap;

    public UrlFile(File parent, URL url, FileModel model, FileContext context) {
        this.parent = parent;
        this.model = model;
        this.model.setUrl(url);
        this.context = context;
        this.fileOperationMap = context.getFileOperationMap(url);
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
    public String getName() {
        String path = model.getPath();
        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));
        String name = path.substring(path.lastIndexOf('/') + 1);
        return name;
    }

    @Override
    public FileTime getCreationTime() {
        return model.getCreationTime();
    }

    @Override
    public FileTime getLastModifiedTime() {
        return model.getLastModifiedTime();
    }

    @Override
    public FileTime getLastAccessTime() {
        return model.getLastAccessTime();
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
        executeOperation(FileOperationEnum.DELETE, null, null, (Void) null);
    }

    @Override
    public void delete(File file) {
        throw new NotImplementedException();
    }

    @Override
    public void dispose() {
        model.clear();
        model = null;
        parent = null;
        fileOperationMap = null;
    }

    @Override
    public Byte[] checksum() {
        return executeOperation(FileOperationEnum.CHECKSUM, null, null, (Void) null);
    }

    @Override
    public boolean isDirectory() {
        return model.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() {
        return model.isSymbolicLink();
    }

    @Override
    public boolean isContainer() {
        return isArchive() || isDirectory();
    }

    @Override
    public File getParent() {
        return parent;
    }

    @Override
    public File getRoot() {
        File root = this;
        while (root.getParent() != null)
            root = root.getParent();
        return root;
    }

    @Override
    public boolean isRoot() {
        return this.toString().equals(getRoot() != null ? getRoot().toString() : "");
    }

    @Override
    public Boolean exists() {
        return executeOperation(FileOperationEnum.EXISTS, null, null, (Void) null);
    }

    @Override
    public List<File> extract(File target) {
        return executeOperation(FileOperationEnum.EXTRACT, target, null, (Void) null);
    }

    @Override
    public void create() {
        executeOperation(FileOperationEnum.CREATE, null, null, (Void) null);
        updateModel();
    }

    @Override
    public InputStream getInputStream() {
        return executeOperation(FileOperationEnum.GET_INPUT_STREAM, null, null, (Void) null);
    }

    @Override
    public OutputStream getOutputStream() {
        return executeOperation(FileOperationEnum.GET_OUTPUT_STREAM, null, null, (Void) null);
    }

    @Override
    public List<AclEntry> getAcl() {
        return model.getAcl();
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        model.setAcl(acl);
        executeOperation(FileOperationEnum.SET_ACL, null, null, (Void) null);
        updateModel();
    }

    @Override
    public UserPrincipal getOwner() {
        return model.getOwner();
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        model.setOwner(owner);
        executeOperation(FileOperationEnum.SET_OWNER, null, null, (Void) null);
        updateModel();
    }

    @Override
    public GroupPrincipal getGroup() {
        return model.getGroup();
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        model.setGroup(group);
        executeOperation(FileOperationEnum.SET_GROUP, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        model.setLastAccessTime(time);
        executeOperation(FileOperationEnum.SET_LAST_ACCESS_TIME, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        model.setLastModifiedTime(time);
        executeOperation(FileOperationEnum.SET_LAST_MODIFIED_TIME, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) {
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        executeOperation(FileOperationEnum.SET_ATTRIBUTES, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) {
        model.setCreationTime(time);
        executeOperation(FileOperationEnum.SET_CREATION_TIME, null, null, (Void) null);
        updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        return model.getAttributes();
    }

    @Override
    public void add(File file) {
        throw new NotImplementedException();
    }

    @Override
    public void addAttributes(FileAttribute... attributes) {
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.addAttribute(attribute);

        executeOperation(FileOperationEnum.ADD_ATTRIBUTES, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) {
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.removeAttribute(attribute);

        executeOperation(FileOperationEnum.REMOVE_ATTRIBUTES, null, null, (Void) null);
        updateModel();
    }

    @Override
    public List<File> find(Filter filter) {
        Filter directoriesFilter = new IsDirectoryFilter().equalTo(true);
        Filter withDirectoriesFilter = ((Filter) filter.clone()).or(new IsDirectoryFilter().equalTo(true));

        List<File> fileList = executeOperation(FileOperationEnum.LIST, null, null, withDirectoriesFilter);

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
    public List<File> list() {
        return executeOperation(FileOperationEnum.LIST, null, null, (Filter) null);
    }

    @Override
    public List<File> list(Filter filter) {
        return executeOperation(FileOperationEnum.LIST, null, null, filter);
    }

    @Override
    public boolean isArchive() {
        return model.isArchive();
    }

    @Override
    public boolean isArchived() {
        return model.isArchived();
    }

    @Override
    public void copy(File targetFile) {
        executeOperation(FileOperationEnum.COPY, targetFile, null, (Void) null);
    }

    protected <T, P> T executeOperation(FileOperationEnum fileOperationEnum, File targetFile, Listener listener, P... params) {
        FileOperation<T, P> fileOperation = fileOperationMap.get(fileOperationEnum);
        if (fileOperation == null)
            throw new OperationNotSupportedException();

        return fileOperation.execute(this, targetFile, listener, params);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) {
        executeOperation(FileOperationEnum.COPY, targetFile, listener, (Void) null);
    }

    @Override
    public void refresh() {
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

    void updateModel() {
        executeOperation(FileOperationEnum.UPDATE_MODEL, null, null, (Void) null);
    }
}
