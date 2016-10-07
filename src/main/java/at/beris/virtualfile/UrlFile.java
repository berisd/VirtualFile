/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

public class UrlFile implements VirtualFile, Comparable<UrlFile> {

    private static Logger logger = LoggerFactory.getLogger(UrlFile.class);

    private VirtualFile parent;
    private FileModel model;
    private URL url;
    private FileOperationProvider fileOperationProvider;
    private FileContext context;

    public UrlFile(VirtualFile parent, URL url, FileContext context) {
        this.parent = parent;
        this.url = url;
        this.context = context;
        this.fileOperationProvider = context.getFileOperationProvider(url.toString());
    }

    @Override
    public URL getUrl() {
        logger.debug("Get URL for {}", this);
        logger.debug("Returns: {}", url);
        return url;
    }

    @Override
    public FileModel getModel() throws IOException {
        logger.debug("Get model for {}", this);
        checkModel();
        logger.debug("Returns: ", model);
        return model;
    }

    @Override
    public String getName() throws IOException {
        logger.debug("Get name for {}", this);
        checkModel();
        String name = FileUtils.getName(model.getUrl().getPath());
        logger.debug("Returns: {}", name);
        return name;
    }

    @Override
    public FileTime getCreationTime() throws IOException {
        logger.debug("Get creationTime for {}", this);
        checkModel();
        FileTime creationTime = model.getCreationTime();
        logger.debug("Returns: {}", creationTime);
        return creationTime;
    }

    @Override
    public FileTime getLastModifiedTime() throws IOException {
        logger.debug("Get lastModifiedTime for {}", this);
        checkModel();
        FileTime lastModifiedTime = model.getLastModifiedTime();
        logger.debug("Returns: {}", lastModifiedTime);
        return lastModifiedTime;
    }

    @Override
    public URL getLinkTarget() throws IOException {
        logger.debug("Get linkTarget for {}", this);
        checkModel();
        URL linkTarget = model.getLinkTarget();
        logger.debug("Returns: {}", linkTarget);
        return linkTarget;
    }

    @Override
    public FileTime getLastAccessTime() throws IOException {
        logger.debug("Get lastAccessTime for {}", this);
        checkModel();
        FileTime lastAccessTime = model.getLastAccessTime();
        logger.debug("Returns: {}", lastAccessTime);
        return lastAccessTime;
    }

    @Override
    public long getSize() throws IOException {
        logger.debug("Get size for {}", this);
        checkModel();
        long size = model.getSize();
        logger.debug("Returns: {}", size);
        return size;
    }

    @Override
    public String getPath() throws IOException {
        logger.debug("Get path for {}", this);
        checkModel();
        String path = model.getUrl().getPath();
        logger.debug("Returns: {}", path);
        return path;
    }

    @Override
    public void delete() throws IOException {
        logger.info("Delete {}", this);
        checkModel();
        fileOperationProvider.delete(model);
    }

    @Override
    public void delete(VirtualFile file) throws IOException {
        logger.info("Delete {} from {}", file, this);
        checkModel();
        throw new NotImplementedException();
    }

    @Override
    public void dispose() {
        logger.debug("Dispose {}", this);
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
        logger.info("Calculate checksum for {}", this);
        checkModel();
        Byte[] checksum = fileOperationProvider.checksum(model);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.setLength(0);
        stringBuilder.append("Returns: ");
        for (byte b : checksum)
            stringBuilder.append(String.format("%02x", b));
        logger.info(stringBuilder.toString());
        return checksum;
    }

    @Override
    public boolean isDirectory() throws IOException {
        logger.debug("Check isDirectory for {}", this);
        checkModel();
        Boolean isDirectory = model.isDirectory();
        logger.debug("Returns: {}", isDirectory);
        return isDirectory;
    }

    @Override
    public boolean isSymbolicLink() throws IOException {
        logger.debug("Check isSymbolicLink for {}", this);
        checkModel();
        boolean isSymbolicLink = model.isSymbolicLink();
        logger.debug("Returns: {}", isSymbolicLink);
        return isSymbolicLink;
    }

    @Override
    public boolean isContainer() throws IOException {
        logger.debug("Check isContainer for {}", this);
        checkModel();
        boolean isContainer = isArchive() || isDirectory();
        logger.debug("Returns: {}", isContainer);
        return isContainer;
    }

    @Override
    public VirtualFile getParent() {
        logger.debug("Get parent for {}", this);
        logger.debug("Returns: {}", parent);
        return parent;
    }

    @Override
    public VirtualFile getRoot() throws IOException {
        logger.debug("Get root for {}", this);
        checkModel();
        VirtualFile root = this;
        while (root.getParent() != null)
            root = root.getParent();
        logger.debug("Returns: {}", root);
        return root;
    }

    @Override
    public boolean isRoot() throws IOException {
        logger.debug("Check isRoot for {}", this);
        checkModel();
        boolean isRoot = this.toString().equals(getRoot() != null ? getRoot().toString() : "");
        logger.debug("Returns: {}", isRoot);
        return isRoot;
    }

    @Override
    public Boolean exists() throws IOException {
        logger.info("Check exists for {}", this);
        checkModel();
        Boolean exists = fileOperationProvider.exists(model);
        logger.info("Returns: {}", exists);
        return exists;
    }

    @Override
    public List<VirtualFile> extract(VirtualFile target) throws IOException {
        logger.info("Extract {} to {}", this, target);
        checkModel();
        return fileOperationProvider.extract(model, target);
    }

    @Override
    public void create() throws IOException {
        logger.info("Create {}", this);
        checkModel();
        fileOperationProvider.create(model);
        updateModel();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        logger.debug("Get Inputstream for {}", this);
        checkModel();
        return fileOperationProvider.getInputStream(model);
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        logger.debug("Get Outputstream for {}", this);
        checkModel();
        return fileOperationProvider.getOutputStream(model);
    }

    @Override
    public List<AclEntry> getAcl() throws IOException {
        logger.debug("Get ACL for {}", this);
        checkModel();
        List<AclEntry> acl = model.getAcl();
        logger.debug("Returns: {}", acl);
        return acl;
    }

    @Override
    public void setAcl(List<AclEntry> acl) throws IOException {
        logger.info("Set ACL to {} for {}", acl, this);
        checkModel();
        model.setAcl(acl);
        fileOperationProvider.setAcl(model);
        updateModel();
    }

    @Override
    public UserPrincipal getOwner() throws IOException {
        logger.debug("Get owner for {}", this);
        checkModel();
        UserPrincipal owner = model.getOwner();
        logger.debug("Returns: {}", owner);
        return owner;
    }

    @Override
    public void setOwner(UserPrincipal owner) throws IOException {
        logger.info("Set owner to {} for {}", owner, this);
        checkModel();
        model.setOwner(owner);
        fileOperationProvider.setOwner(model);
        updateModel();
    }

    @Override
    public void setUrl(URL url) throws IOException {
        logger.info("Set url to {} for {}", url, this);
        this.url = url;
    }

    @Override
    public GroupPrincipal getGroup() throws IOException {
        logger.debug("Get group for {}", this);
        checkModel();
        GroupPrincipal group = model.getGroup();
        logger.debug("Returns: {}" + group);
        return group;
    }

    @Override
    public void setGroup(GroupPrincipal group) throws IOException {
        logger.info("Set group to {} for {}", group, this);
        checkModel();
        model.setGroup(group);
        fileOperationProvider.setGroup(model);
        updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) throws IOException {
        logger.info("Set lastAccessTime to {} for {}", time, this);
        checkModel();
        model.setLastAccessTime(time);
        fileOperationProvider.setLastAccessTime(model);
        updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) throws IOException {
        logger.info("Set lastModifiedTime to {} for {}", time, this);
        checkModel();
        model.setLastModifiedTime(time);
        fileOperationProvider.setLastModifiedTime(model);
        updateModel();
    }

    @Override
    public void setAttributes(FileAttribute... attributes) throws IOException {
        logger.info("Set attributes for {}", this);
        checkModel();
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        fileOperationProvider.setAttributes(model);
        updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) throws IOException {
        logger.info("Set creationTime to {} for {}", time, this);
        checkModel();
        model.setCreationTime(time);
        fileOperationProvider.setCreationTime(model);
        updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() throws IOException {
        logger.debug("Get attributes for {}", this);
        checkModel();
        Set<FileAttribute> attributes = model.getAttributes();
        logger.debug("Returns: {}", FileUtils.getAttributesString(attributes.toArray(new FileAttribute[0])));
        return attributes;
    }

    @Override
    public void add(VirtualFile file) throws IOException {
        logger.info("Add {} to {}", file, this);
        checkModel();
        fileOperationProvider.add(model, file);
    }

    @Override
    public void addAttributes(FileAttribute... attributes) throws IOException {
        logger.info("Add attributes {} to {}", FileUtils.getAttributesString(attributes), this);
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
        logger.info("Remove attributes {} from {}", FileUtils.getAttributesString(attributes), this);
        checkModel();
        if (attributes.length < 1)
            return;

        for (FileAttribute attribute : attributes)
            model.removeAttribute(attribute);

        fileOperationProvider.removeAttributes(model);
        updateModel();
    }

    @Override
    public List<VirtualFile> find(Filter filter) throws IOException {
        logger.info("Find children for {} with filter {}", this, filter);
        checkModel();
        Filter directoriesFilter = new IsDirectoryFilter().equalTo(true);
        Filter withDirectoriesFilter = ((Filter) filter.clone()).or(new IsDirectoryFilter().equalTo(true));

        List<VirtualFile> fileList = fileOperationProvider.list(model, withDirectoriesFilter);

        Map<Filter, List<VirtualFile>> partitionedFileList = FileUtils.groupFileListByFilters(fileList, Arrays.asList(filter, directoriesFilter));

        fileList.clear();
        fileList = partitionedFileList.get(filter);
        List<VirtualFile> directoryList = partitionedFileList.get(directoriesFilter);

        for (VirtualFile directory : directoryList) {
            fileList.addAll(directory.find(filter));
        }
        directoryList.clear();
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<VirtualFile> list() throws IOException {
        logger.info("List children for {}", this);
        checkModel();
        List<VirtualFile> fileList = fileOperationProvider.list(model, null);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<VirtualFile> list(Filter filter) throws IOException {
        logger.info("List children for {} with filter {}", this, filter);
        checkModel();
        List<VirtualFile> fileList = fileOperationProvider.list(model, filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public boolean isArchive() throws IOException {
        logger.debug("Check isArchive for {}", this);
        checkModel();
        Boolean isArchive = model.isArchive();
        logger.debug("Returns: {}", isArchive);
        return isArchive;
    }

    @Override
    public boolean isArchived() throws IOException {
        logger.debug("Check isArchived for {}", this);
        checkModel();
        boolean isArchived = model.isArchived();
        logger.debug("Returns: {}", isArchived);
        return isArchived;
    }

    @Override
    public void copy(VirtualFile targetFile) throws IOException {
        logger.info("Copy {} to {}", this, targetFile);
        checkModel();
        fileOperationProvider.copy(this, targetFile, null);
    }

    @Override
    public void copy(VirtualFile targetFile, CopyListener listener) throws IOException {
        logger.info("Copy {} to {} with Listener", this, targetFile);
        checkModel();
        fileOperationProvider.copy(this, targetFile, listener);
    }

    @Override
    public void refresh() throws IOException {
        logger.info("Refresh {}", this);
        checkModel();
        updateModel();
    }

    @Override
    public String toString() {
        return String.format("%s@%s [%s]", this.getClass().getSimpleName(),
                Integer.toHexString(System.identityHashCode(this)), maskedUrlString(url));
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
        logger.debug("Set model to {} for {}", model, this);
        this.model = model;
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
    }

    void updateModel() throws IOException {
        logger.debug("Update model for {}", this);
        fileOperationProvider.updateModel(model);
    }

    private void checkModel() throws IOException {
        logger.debug("Check model for {}", this);
        if (model == null)
            createModel();
        if (!model.getUrl().toString().equals(url.toString())) {
            context.replaceFileUrl(url, model.getUrl());
        }
    }

    private void createModel() throws IOException {
        logger.debug("Create model for {}", this);
        model = new FileModel();
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
        updateModel();
    }
}
