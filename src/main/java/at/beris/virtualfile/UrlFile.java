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
import at.beris.virtualfile.content.charset.CharsetDetector;
import at.beris.virtualfile.content.charset.CharsetMatch;
import at.beris.virtualfile.content.detect.Detector;
import at.beris.virtualfile.content.metadata.Metadata;
import at.beris.virtualfile.content.mime.MediaType;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import at.beris.virtualfile.util.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;

import static at.beris.virtualfile.util.UrlUtils.maskedUrlString;

/**
 * Internal class representing a virtual file
 */
class UrlFile implements VirtualFile, Comparable<UrlFile> {

    private static Logger logger = LoggerFactory.getLogger(UrlFile.class);

    protected FileModel model;
    protected URL url;
    protected FileOperationProvider fileOperationProvider;
    protected UrlFileContext context;

    public UrlFile(URL url, UrlFileContext context) {
        this.url = url;
        this.context = context;
        this.fileOperationProvider = context.getFileOperationProvider(url);
    }

    @Override
    public URL getUrl() {
        logger.debug("Get URL for {}", this);
        logger.debug("Returns: {}", url);
        return url;
    }

    @Override
    public FileModel getModel() {
        logger.debug("Get model for {}", this);
        checkModel();
        logger.debug("Returns: ", model);
        return model;
    }

    @Override
    public String getName() {
        logger.debug("Get name for {}", this);
        checkModel();
        String name = FileUtils.getName(model.getUrl().getPath());
        logger.debug("Returns: {}", name);
        return name;
    }

    @Override
    public FileTime getCreationTime() {
        logger.debug("Get creationTime for {}", this);
        checkModel();
        FileTime creationTime = model.getCreationTime();
        logger.debug("Returns: {}", creationTime);
        return creationTime;
    }

    @Override
    public FileTime getLastModifiedTime() {
        logger.debug("Get lastModifiedTime for {}", this);
        checkModel();
        FileTime lastModifiedTime = model.getLastModifiedTime();
        logger.debug("Returns: {}", lastModifiedTime);
        return lastModifiedTime;
    }

    @Override
    public URL getLinkTarget() {
        logger.debug("Get linkTarget for {}", this);
        checkModel();
        URL linkTarget = model.getLinkTarget();
        logger.debug("Returns: {}", linkTarget);
        return linkTarget;
    }

    @Override
    public FileTime getLastAccessTime() {
        logger.debug("Get lastAccessTime for {}", this);
        checkModel();
        FileTime lastAccessTime = model.getLastAccessTime();
        logger.debug("Returns: {}", lastAccessTime);
        return lastAccessTime;
    }

    @Override
    public long getSize() {
        logger.debug("Get size for {}", this);
        checkModel();
        long size = model.getSize();
        logger.debug("Returns: {}", size);
        return size;
    }

    @Override
    public String getPath() {
        logger.debug("Get path for {}", this);
        checkModel();
        String path = model.getUrl().getPath();
        logger.debug("Returns: {}", path);
        return path;
    }

    @Override
    public void delete() {
        logger.info("Delete {}", this);
        checkModel();
        fileOperationProvider.delete(model);
    }

    @Override
    public void dispose() {
        logger.debug("Dispose {}", this);
        if (model != null) {
            model.clear();
            model = null;
        }
        url = null;
        context = null;
        fileOperationProvider = null;
    }

    @Override
    public Byte[] checksum() {
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
    public boolean isDirectory() {
        logger.debug("Check isDirectory for {}", this);
        checkModel();
        Boolean isDirectory = model.isDirectory();
        logger.debug("Returns: {}", isDirectory);
        return isDirectory;
    }

    @Override
    public boolean isSymbolicLink() {
        logger.debug("Check isSymbolicLink for {}", this);
        checkModel();
        boolean isSymbolicLink = model.isSymbolicLink();
        logger.debug("Returns: {}", isSymbolicLink);
        return isSymbolicLink;
    }

    @Override
    public boolean isReadable() {
        logger.debug("Check isReadable for {}", this);
        checkModel();
        boolean isReadable = fileOperationProvider.isReadable(model);
        logger.debug("Returns: {}", isReadable);
        return isReadable;
    }

    @Override
    public boolean isWritable() {
        logger.debug("Check isWritable for {}", this);
        checkModel();
        boolean isWritable = fileOperationProvider.isWritable(model);
        logger.debug("Returns: {}", isWritable);
        return isWritable;
    }

    @Override
    public boolean isExecutable() {
        logger.debug("Check isExecutable for {}", this);
        checkModel();
        boolean isExecutable = fileOperationProvider.isExecutable(model);
        logger.debug("Returns: {}", isExecutable);
        return isExecutable;
    }

    @Override
    public boolean isHidden() {
        logger.debug("Check isHidden for {}", this);
        checkModel();
        boolean isHidden = fileOperationProvider.isHidden(model);
        logger.debug("Returns: {}", isHidden);
        return isHidden;
    }

    @Override
    public VirtualFile getParent() {
        logger.debug("Get parent for {}", this);
        VirtualFile parent = context.getParentFile(this);
        logger.debug("Returns: {}", parent);
        return parent;
    }

    @Override
    public VirtualFile getRoot() {
        logger.debug("Get root for {}", this);
        checkModel();
        VirtualFile root = this;
        while (root.getParent() != null)
            root = root.getParent();
        logger.debug("Returns: {}", root);
        return root;
    }

    @Override
    public Boolean exists() {
        logger.info("Check exists for {}", this);
        checkModel();
        Boolean exists = fileOperationProvider.exists(model);
        logger.info("Returns: {}", exists);
        return exists;
    }

    @Override
    public void create() {
        logger.info("Create {}", this);
        checkModel();
        fileOperationProvider.create(model);
        updateModel();
    }

    @Override
    public InputStream getInputStream() {
        logger.debug("Get Inputstream for {}", this);
        checkModel();
        return fileOperationProvider.getInputStream(model);
    }

    @Override
    public OutputStream getOutputStream() {
        logger.debug("Get Outputstream for {}", this);
        checkModel();
        return fileOperationProvider.getOutputStream(model);
    }

    @Override
    public List<AclEntry> getAcl() {
        logger.debug("Get ACL for {}", this);
        checkModel();
        List<AclEntry> acl = model.getAcl();
        logger.debug("Returns: {}", acl);
        return acl;
    }

    @Override
    public void setAcl(List<AclEntry> acl) {
        logger.info("Set ACL to {} for {}", acl, this);
        checkModel();
        model.setAcl(acl);
        fileOperationProvider.setAcl(model);
        updateModel();
    }

    @Override
    public UserPrincipal getOwner() {
        logger.debug("Get owner for {}", this);
        checkModel();
        UserPrincipal owner = model.getOwner();
        logger.debug("Returns: {}", owner);
        return owner;
    }

    @Override
    public void setOwner(UserPrincipal owner) {
        logger.info("Set owner to {} for {}", owner, this);
        checkModel();
        model.setOwner(owner);
        fileOperationProvider.setOwner(model);
        updateModel();
    }

    @Override
    public void setUrl(URL url) {
        logger.info("Set url to {} for {}", url, this);
        this.url = url;
    }

    @Override
    public GroupPrincipal getGroup() {
        logger.debug("Get group for {}", this);
        checkModel();
        GroupPrincipal group = model.getGroup();
        logger.debug("Returns: {}" + group);
        return group;
    }

    @Override
    public void setGroup(GroupPrincipal group) {
        logger.info("Set group to {} for {}", group, this);
        checkModel();
        model.setGroup(group);
        fileOperationProvider.setGroup(model);
        updateModel();
    }

    @Override
    public void setLastAccessTime(FileTime time) {
        logger.info("Set lastAccessTime to {} for {}", time, this);
        checkModel();
        model.setLastAccessTime(time);
        fileOperationProvider.setLastAccessTime(model);
        updateModel();
    }

    @Override
    public void setLastModifiedTime(FileTime time) {
        logger.info("Set lastModifiedTime to {} for {}", time, this);
        checkModel();
        model.setLastModifiedTime(time);
        fileOperationProvider.setLastModifiedTime(model);
        updateModel();
    }

    @Override
    //TODO use Set instead of Varargs
    public void setAttributes(FileAttribute... attributes) {
        logger.info("Set attributes for {}", this);
        checkModel();
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        fileOperationProvider.setAttributes(model);
        updateModel();
    }

    @Override
    public void setCreationTime(FileTime time) {
        logger.info("Set creationTime to {} for {}", time, this);
        checkModel();
        model.setCreationTime(time);
        fileOperationProvider.setCreationTime(model);
        updateModel();
    }

    @Override
    public Set<FileAttribute> getAttributes() {
        logger.debug("Get attributes for {}", this);
        checkModel();
        Set<FileAttribute> attributes = model.getAttributes();
        logger.debug("Returns: {}", FileUtils.getAttributesString(attributes.toArray(new FileAttribute[0])));
        return attributes;
    }

    @Override
    public ContentType getContentType() {
        try (InputStream inputStream = new BufferedInputStream(getInputStream())) {
            Detector detector = context.getContentDetector();
            MediaType mediaType = detector.detect(inputStream, new Metadata());
            return new ContentType(mediaType);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public String getContentEncoding() {
        try (InputStream inputStream = new BufferedInputStream(getInputStream())) {
            CharsetDetector detector = context.getCharsetDetector();
            detector.setText(inputStream);
            CharsetMatch match = detector.detect();
            return match.getName();

        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public VirtualArchive asArchive() {
        return new FileArchive(this, context);
    }

    @Override
    public File asFile() {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void addAttributes(FileAttribute... attributes) {
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
    //TODO use Set instead of Varargs
    public void removeAttributes(FileAttribute... attributes) {
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
    public void rename(String newName) {
        logger.info("Rename {} to {}", this, newName);
        checkModel();
        fileOperationProvider.rename(model, newName);
    }

    @Override
    public List<VirtualFile> find(Filter filter) {
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
    public List<VirtualFile> list() {
        logger.info("List children for {}", this);
        checkModel();
        List<VirtualFile> fileList = fileOperationProvider.list(model, null);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public List<VirtualFile> list(Filter filter) {
        logger.info("List children for {} with filter {}", this, filter);
        checkModel();
        List<VirtualFile> fileList = fileOperationProvider.list(model, filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    @Override
    public void move(VirtualFile target) {
        logger.info("Move {} to {}", this, target);
        checkModel();
        fileOperationProvider.move(model, target);
    }

    @Override
    public boolean isArchive() {
        logger.debug("Check isArchive for {}", this);
        checkModel();
        Boolean isArchive = model.isArchive();
        logger.debug("Returns: {}", isArchive);
        return isArchive;
    }

    @Override
    public Integer copy(VirtualFile targetFile) {
        logger.info("Copy {} to {}", this, targetFile);
        checkModel();
        Integer filesCopied = fileOperationProvider.copy(this, targetFile, null);
        logger.debug("Returns: {}", filesCopied);
        return filesCopied;
    }

    @Override
    public Integer copy(VirtualFile targetFile, FileOperationListener listener) {
        logger.info("Copy {} to {} with FileOperationListener", this, targetFile);
        checkModel();
        Integer filesCopied = fileOperationProvider.copy(this, targetFile, listener);
        logger.debug("Returns: {}", filesCopied);
        return filesCopied;
    }

    @Override
    public Boolean compare(VirtualFile targetFile) {
        logger.info("Compare {} with {}", this, targetFile);
        checkModel();
        Boolean result = fileOperationProvider.compare(this, targetFile, null);
        logger.debug("Returns: equal={}", result);
        return result;
    }

    @Override
    public Boolean compare(VirtualFile targetFile, FileOperationListener listener) {
        logger.info("Compare {} with {} with FileOperationListener", this, targetFile);
        checkModel();
        Boolean result = fileOperationProvider.compare(this, targetFile, listener);
        logger.debug("Returns: equal={}", result);
        return result;
    }

    @Override
    public void compress() {
        throw new NotImplementedException();
    }

    @Override
    public void decompress() {
        throw new NotImplementedException();
    }

    @Override
    public void refresh() {
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
        return url.toString().hashCode();
    }

    @Override
    public int compareTo(UrlFile o) {
        return url.toString().compareTo(url.toString());
    }

    @Override
    public void setModel(FileModel model) {
        logger.debug("Set model to {} for {}", model, this);
        this.model = model;
        VirtualFile parent = context.getParentFile(this);
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
    }

    void updateModel() {
        logger.debug("Update model for {}", this);
        fileOperationProvider.updateModel(model);
    }

    protected void checkModel() {
        logger.debug("Check model for {}", this);
        if (model == null)
            createModel();
        if (model.getUrl() != null && !url.toString().equals(model.getUrl().toString())) {
            context.replaceFileUrl(url, model.getUrl());
        }
    }

    private void createModel() {
        logger.debug("Create model for {}", this);
        model = context.createFileModel();
        VirtualFile parent = context.getParentFile(this);
        if (parent != null)
            model.setParent(parent.getModel());
        model.setUrl(url);
        updateModel();
    }
}
