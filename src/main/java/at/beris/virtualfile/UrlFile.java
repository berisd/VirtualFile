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
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import at.beris.virtualfile.operation.Listener;
import at.beris.virtualfile.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private final static Logger LOGGER = LoggerFactory.getLogger(UrlFile.class);

    protected File parent;
    protected FileModel model;
    protected FileContext context;
    protected StringBuilder stringBuilder;

    private Site site;
    private Map<FileOperationEnum, FileOperation> fileOperationMap;

    public UrlFile(File parent, URL url, FileModel model, FileContext context) {
        this.parent = parent;
        this.model = model;
        this.model.setUrl(url);
        this.context = context;
        this.site = context.getSite(url);
        this.fileOperationMap = context.getFileOperationMap(url);
        this.stringBuilder = new StringBuilder();
    }

    @Override
    public URL getUrl() {
        LOGGER.debug("Get URL for " + this);
        URL url = model.getUrl();
        LOGGER.debug("Returns: " + url);
        return url;
    }

    @Override
    public FileModel getModel() {
        LOGGER.debug("Get model for " + this);
        LOGGER.debug("Returns: " + model);
        return model;
    }

    @Override
    public Site getSite() {
        LOGGER.debug("Get site for " + this);
        LOGGER.debug("Returns: " + site);
        return site;
    }

    @Override
    public String getName() {
        LOGGER.debug("Get name for " + this);
        String path = model.getPath();

        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));

        String name = path.substring(path.lastIndexOf('/') + 1);
        LOGGER.debug("Returns: " + name);

        return name;
    }

    @Override
    public FileTime getCreationTime() {
        LOGGER.debug("Get creationTime for " + this);
        FileTime creationTime = model.getCreationTime();
        LOGGER.debug("Returns: " + creationTime);
        return creationTime;
    }

    @Override
    public FileTime getLastModifiedTime() {
        LOGGER.debug("Get lastModifiedTime for " + this);
        FileTime lastModifiedTime = model.getLastModifiedTime();
        LOGGER.debug("Returns: " + lastModifiedTime);
        return lastModifiedTime;
    }

    @Override
    public FileTime getLastAccessTime() {
        LOGGER.debug("Get lastAccessTime for " + this);
        FileTime lastAccessTime = model.getLastAccessTime();
        LOGGER.debug("Returns: " + lastAccessTime);
        return lastAccessTime;
    }

    @Override
    public long getSize() {
        LOGGER.debug("Get size for " + this);
        LOGGER.debug("Returns: " + model.getSize());
        return model.getSize();
    }

    @Override
    public String getPath() {
        LOGGER.debug("Get path for " + this);
        LOGGER.debug("Returns: " + model.getPath());
        return model.getPath();
    }

    @Override
    public void delete() {
        logOperation("Delete " + this);
        executeOperation(FileOperationEnum.DELETE, null, null, (Void) null);
    }

    @Override
    public void dispose() {
        LOGGER.debug("Dispose " + this);
        model.clear();
        model = null;
        parent = null;
        fileOperationMap = null;
    }

    @Override
    public Byte[] checksum() {
        logOperation("Calculate checksum for " + this);

        Byte[] checksum = executeOperation(FileOperationEnum.CHECKSUM, null, null, (Void) null);

        stringBuilder.setLength(0);
        stringBuilder.append("Returns: ");
        for (byte b : checksum)
            stringBuilder.append(String.format("%02x", b));
        LOGGER.info(stringBuilder.toString());
        return checksum;
    }

    @Override
    public boolean isDirectory() {
        LOGGER.debug("Check isDirectory for " + this);
        LOGGER.debug("Returns: " + model.isDirectory());
        return model.isDirectory();
    }

    @Override
    public boolean isSymbolicLink() {
        LOGGER.debug("Check isSymbolicLink for " + this);
        LOGGER.debug("Returns: " + model.isSymbolicLink());
        return model.isSymbolicLink();
    }

    @Override
    public boolean isContainer() {
        LOGGER.debug("Check isContainer for " + this);
        boolean isContainer = this instanceof FileContainer;
        LOGGER.debug("Returns: " + isContainer);
        return isContainer;
    }

    @Override
    public File getParent() {
        LOGGER.debug("Get parent for " + this);
        LOGGER.debug("Returns: " + parent);
        return parent;
    }

    @Override
    public File getRoot() {
        LOGGER.debug("Get root for " + this);
        File root = this;

        while (root.getParent() != null)
            root = root.getParent();

        LOGGER.debug("Returns: " + root);
        return root;
    }

    @Override
    public boolean isRoot() {
        LOGGER.debug("Check isRoot for " + this);
        boolean isRoot = this.toString().equals(getRoot() != null ? getRoot().toString() : "");
        LOGGER.debug("Returns: " + isRoot);
        return isRoot;
    }

    @Override
    public Boolean exists() {
        logOperation("Check exists for " + this);
        Boolean exists = executeOperation(FileOperationEnum.EXISTS, null, null, (Void) null);
        logOperation("Returns: " + exists);
        return exists;
    }

    @Override
    public void create() {
        logOperation("Create " + this);
        executeOperation(FileOperationEnum.CREATE, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public InputStream getInputStream() {
        LOGGER.debug("Get Inputstream for " + this);
        return executeOperation(FileOperationEnum.GET_INPUT_STREAM, null, null, (Void) null);
    }

    @Override
    public OutputStream getOutputStream() {
        LOGGER.debug("Get Outputstream for " + this);
        return executeOperation(FileOperationEnum.GET_OUTPUT_STREAM, null, null, (Void) null);
    }

    @Override
    public List<AclEntry> getAcl() {
        LOGGER.debug("Get ACL for " + this);
        List<AclEntry> acl = model.getAcl();
        LOGGER.debug("Returns: " + acl);
        return acl;
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        logOperation("Set ACL to " + acl + " for " + this);
        model.setAcl(acl);
        executeOperation(FileOperationEnum.SET_ACL, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public UserPrincipal getOwner() {
        LOGGER.debug("Get owner for " + this);
        LOGGER.debug("Returns: " + model.getOwner());
        return model.getOwner();
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        logOperation("Set owner to " + owner + " for " + this);
        model.setOwner(owner);
        executeOperation(FileOperationEnum.SET_OWNER, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public GroupPrincipal getGroup() {
        LOGGER.debug("Get group for " + this);
        LOGGER.debug("Returns: " + model.getGroup());
        return model.getGroup();
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        logOperation("Set group to " + group + " for " + this);
        model.setGroup(group);
        executeOperation(FileOperationEnum.SET_GROUP, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        logOperation("Set lastAccessTime to " + time + " for " + this);
        model.setLastAccessTime(time);
        executeOperation(FileOperationEnum.SET_LAST_ACCESS_TIME, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        logOperation("Set lastModifiedTime to " + time + " for " + this);
        model.setLastModifiedTime(time);
        executeOperation(FileOperationEnum.SET_LAST_MODIFIED_TIME, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) {
        logOperation("Set attributes for " + this);
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        executeOperation(FileOperationEnum.SET_ATTRIBUTES, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) {
        logOperation("Set creationTime to " + time + " for " + this);
        model.setCreationTime(time);
        executeOperation(FileOperationEnum.SET_CREATION_TIME, null, null, (Void) null);
        _updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        LOGGER.debug("Get attributes for " + this);
        LOGGER.debug("Returns: " + model.getAttributes());
        return model.getAttributes();
    }

    @Override
    public void addAttributes(FileAttribute... attributes) {
        logOperation("Add attributes " + getAttributesString(attributes) + " to " + this);

        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.addAttribute(attribute);

        executeOperation(FileOperationEnum.ADD_ATTRIBUTES, null, null, (Void) null);

        _updateModel();
    }

    @Override
    public Archive asArchive() {
        return (Archive) this;
    }

    @Override
    public Directory asDirectory() {
        return (Directory) this;
    }

    @Override
    public void removeAttributes(FileAttribute... attributes) {
        logOperation("Remove attributes " + getAttributesString(attributes) + " from " + this);
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.removeAttribute(attribute);

        executeOperation(FileOperationEnum.REMOVE_ATTRIBUTES, null, null, (Void) null);

        _updateModel();
    }

    @Override
    public List<File> find(Filter filter) {
        logOperation("Find children for " + this + " with filter " + filter);
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

        logOperation("Returns: " + fileList.size() + " entries");

        return fileList;
    }

    @Override
    public List<File> list() {
        logOperation("List children for " + this);
        List<File> fileList = executeOperation(FileOperationEnum.LIST, null, null, (Filter) null);
        logOperation("Returns: " + fileList.size() + " entries");
        return fileList;
    }

    @Override
    public List<File> list(Filter filter) {
        logOperation("List children for " + this + " with filter " + filter);
        List<File> fileList = executeOperation(FileOperationEnum.LIST, null, null, filter);
        logOperation("Returns: " + fileList.size() + " entries");
        return fileList;
    }

    @Override
    public boolean isArchive() {
        LOGGER.debug("Check isArchive for " + this);
        boolean isArchive = this instanceof Archive;
        LOGGER.debug("Returns: " + isArchive);
        return isArchive;
    }

    @Override
    public boolean isArchived() {
        LOGGER.debug("Check isArchived for " + this);
        return model.isArchived();
    }

    @Override
    public void copy(File targetFile) {
        logOperation("Copy " + this + " to " + targetFile);
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
        logOperation("Copy " + this + " to " + targetFile + " with Listener");
        executeOperation(FileOperationEnum.COPY, targetFile, listener, (Void) null);
    }

    @Override
    public void refresh() {
        logOperation("Refresh " + this);
        _updateModel();
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

    void _updateModel() {
        LOGGER.debug("UpdateModel for " + this);
        executeOperation(FileOperationEnum.UPDATE_MODEL, null, null, (Void) null);
    }

    private String getAttributesString(FileAttribute[] attributes) {
        String attributesString = "";
        if (attributes.length < 1)
            attributesString = "<none>";
        else {
            for (FileAttribute attribute : attributes)
                attributesString = (attributesString != "" ? ", " : "") + attribute.toString();
        }
        return attributesString;
    }

    protected boolean isInternalCall() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        StackTraceElement calleeStackTraceElement = stackTrace[4];
        Package thisPackage = this.getClass().getPackage();

        String calleeClassName = calleeStackTraceElement.getClassName();
        if (!calleeClassName.startsWith(thisPackage.getName()))
            return false;

        if (calleeClassName.startsWith(thisPackage.getName()) && calleeClassName.endsWith("Test"))
            return false;

        return true;
    }

    protected void logOperation(String message) {
        if (isInternalCall()) {
            LOGGER.debug(message);
        } else
            LOGGER.info(message);
    }
}
