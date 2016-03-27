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

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

public class UrlFile implements File, Comparable<UrlFile> {

    private File parent;
    private FileModel model;
    private URL url;
    private Map<FileOperationEnum, FileOperation> fileOperationMap;

    public UrlFile(File parent, URL url, FileContext context) {
        this.parent = parent;
        this.url = url;
        this.fileOperationMap = context.getFileOperationMap(url);
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public FileModel getModel() {
        checkModel();
        return model;
    }

    @Override
    public String getName() {
        checkModel();
        String path = model.getPath();
        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));
        String name = path.substring(path.lastIndexOf('/') + 1);
        return name;
    }

    @Override
    public FileTime getCreationTime() {
        checkModel();
        return model.getCreationTime();
    }

    @Override
    public FileTime getLastModifiedTime() {
        checkModel();
        return model.getLastModifiedTime();
    }

    @Override
    public FileTime getLastAccessTime() {
        checkModel();
        return model.getLastAccessTime();
    }

    @Override
    public long getSize() {
        checkModel();
        return model.getSize();
    }

    @Override
    public String getPath() {
        checkModel();
        return model.getPath();
    }

    @Override
    public void delete() {
        checkModel();
        executeOperation(FileOperationEnum.DELETE, null, null, (Void) null);
    }

    @Override
    public void delete(File file) {
        checkModel();
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
        checkModel();
        return executeOperation(FileOperationEnum.CHECKSUM, null, null, (Void) null);
    }

    @Override
    public boolean isDirectory() {
        checkModel();
        return model.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() {
        checkModel();
        return model.isSymbolicLink();
    }

    @Override
    public boolean isContainer() {
        checkModel();
        return isArchive() || isDirectory();
    }

    @Override
    public File getParent() {
        return parent;
    }

    @Override
    public File getRoot() {
        checkModel();
        File root = this;
        while (root.getParent() != null)
            root = root.getParent();
        return root;
    }

    @Override
    public boolean isRoot() {
        checkModel();
        return this.toString().equals(getRoot() != null ? getRoot().toString() : "");
    }

    @Override
    public Boolean exists() {
        checkModel();
        return executeOperation(FileOperationEnum.EXISTS, null, null, (Void) null);
    }

    @Override
    public List<File> extract(File target) {
        checkModel();
        return executeOperation(FileOperationEnum.EXTRACT, target, null, (Void) null);
    }

    @Override
    public void create() {
        checkModel();
        executeOperation(FileOperationEnum.CREATE, null, null, (Void) null);
        updateModel();
    }

    @Override
    public InputStream getInputStream() {
        checkModel();
        return executeOperation(FileOperationEnum.GET_INPUT_STREAM, null, null, (Void) null);
    }

    @Override
    public OutputStream getOutputStream() {
        checkModel();
        return executeOperation(FileOperationEnum.GET_OUTPUT_STREAM, null, null, (Void) null);
    }

    @Override
    public List<AclEntry> getAcl() {
        checkModel();
        return model.getAcl();
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        checkModel();
        model.setAcl(acl);
        executeOperation(FileOperationEnum.SET_ACL, null, null, (Void) null);
        updateModel();
    }

    @Override
    public UserPrincipal getOwner() {
        checkModel();
        return model.getOwner();
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        checkModel();
        model.setOwner(owner);
        executeOperation(FileOperationEnum.SET_OWNER, null, null, (Void) null);
        updateModel();
    }

    @Override
    public GroupPrincipal getGroup() {
        checkModel();
        return model.getGroup();
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        checkModel();
        model.setGroup(group);
        executeOperation(FileOperationEnum.SET_GROUP, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        checkModel();
        model.setLastAccessTime(time);
        executeOperation(FileOperationEnum.SET_LAST_ACCESS_TIME, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        checkModel();
        model.setLastModifiedTime(time);
        executeOperation(FileOperationEnum.SET_LAST_MODIFIED_TIME, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) {
        checkModel();
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        executeOperation(FileOperationEnum.SET_ATTRIBUTES, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) {
        checkModel();
        model.setCreationTime(time);
        executeOperation(FileOperationEnum.SET_CREATION_TIME, null, null, (Void) null);
        updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        checkModel();
        return model.getAttributes();
    }

    @Override
    public void add(File file) {
        checkModel();
        throw new NotImplementedException();
    }

    @Override
    public void addAttributes(FileAttribute... attributes) {
        checkModel();
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.addAttribute(attribute);

        executeOperation(FileOperationEnum.ADD_ATTRIBUTES, null, null, (Void) null);
        updateModel();
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) {
        checkModel();
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.removeAttribute(attribute);

        executeOperation(FileOperationEnum.REMOVE_ATTRIBUTES, null, null, (Void) null);
        updateModel();
    }

    @Override
    public List<File> find(Filter filter) {
        checkModel();
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
        checkModel();
        return executeOperation(FileOperationEnum.LIST, null, null, (Filter) null);
    }

    @Override
    public List<File> list(Filter filter) {
        checkModel();
        return executeOperation(FileOperationEnum.LIST, null, null, filter);
    }

    @Override
    public boolean isArchive() {
        checkModel();
        return model.isArchive();
    }

    @Override
    public boolean isArchived() {
        checkModel();
        return model.isArchived();
    }

    @Override
    public void copy(File targetFile) {
        checkModel();
        executeOperation(FileOperationEnum.COPY, targetFile, null, (Void) null);
    }

    @Override
    public void copy(File targetFile, CopyListener listener) {
        checkModel();
        executeOperation(FileOperationEnum.COPY, targetFile, listener, (Void) null);
    }

    @Override
    public void refresh() {
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

    private <T, P> T executeOperation(FileOperationEnum fileOperationEnum, File targetFile, Listener listener, P... params) {
        FileOperation<T, P> fileOperation = fileOperationMap.get(fileOperationEnum);
        if (fileOperation == null)
            throw new OperationNotSupportedException();

        return fileOperation.execute(this, targetFile, listener, params);
    }

    void updateModel() {
        executeOperation(FileOperationEnum.UPDATE_MODEL, null, null, (Void) null);
    }

    private void checkModel() {
        if (model == null)
            createModel();
    }

    private void createModel() {
        model = new FileModel();
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
        updateModel();
    }
}
