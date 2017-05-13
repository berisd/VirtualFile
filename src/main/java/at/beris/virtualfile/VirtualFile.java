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
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.filter.IsDirectoryFilter;
import at.beris.virtualfile.provider.FileOperationProvider;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import at.beris.virtualfile.util.FileUtils;
import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
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

public class VirtualFile implements Comparable<VirtualFile> {

    private static Logger logger = LoggerFactory.getLogger(VirtualFile.class);

    protected FileModel model;
    protected URL url;
    protected FileOperationProvider fileOperationProvider;
    protected UrlFileContext context;

    public VirtualFile(URL url, UrlFileContext context) {
        this.url = url;
        this.context = context;
        this.fileOperationProvider = context.getFileOperationProvider(url.toString());
    }

    public URL getUrl() {
        logger.debug("Get URL for {}", this);
        logger.debug("Returns: {}", url);
        return url;
    }

    public FileModel getModel() {
        logger.debug("Get model for {}", this);
        checkModel();
        logger.debug("Returns: ", model);
        return model;
    }

    public String getName() {
        logger.debug("Get name for {}", this);
        checkModel();
        String name = FileUtils.getName(model.getUrl().getPath());
        logger.debug("Returns: {}", name);
        return name;
    }

    public FileTime getCreationTime() {
        logger.debug("Get creationTime for {}", this);
        checkModel();
        FileTime creationTime = model.getCreationTime();
        logger.debug("Returns: {}", creationTime);
        return creationTime;
    }

    public FileTime getLastModifiedTime() {
        logger.debug("Get lastModifiedTime for {}", this);
        checkModel();
        FileTime lastModifiedTime = model.getLastModifiedTime();
        logger.debug("Returns: {}", lastModifiedTime);
        return lastModifiedTime;
    }

    public URL getLinkTarget() {
        logger.debug("Get linkTarget for {}", this);
        checkModel();
        URL linkTarget = model.getLinkTarget();
        logger.debug("Returns: {}", linkTarget);
        return linkTarget;
    }

    public FileTime getLastAccessTime() {
        logger.debug("Get lastAccessTime for {}", this);
        checkModel();
        FileTime lastAccessTime = model.getLastAccessTime();
        logger.debug("Returns: {}", lastAccessTime);
        return lastAccessTime;
    }

    /**
     * Returns the size in bytes for a file and the number of contained items for a directory.
     *
     * @return File size.
     */
    public long getSize() {
        logger.debug("Get size for {}", this);
        checkModel();
        long size = model.getSize();
        logger.debug("Returns: {}", size);
        return size;
    }

    public String getPath() {
        logger.debug("Get path for {}", this);
        checkModel();
        String path = model.getUrl().getPath();
        logger.debug("Returns: {}", path);
        return path;
    }

    /**
     * Deletes the file.
     */
    public void delete() {
        logger.info("Delete {}", this);
        checkModel();
        fileOperationProvider.delete(model);
    }

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

    public boolean isDirectory() {
        logger.debug("Check isDirectory for {}", this);
        checkModel();
        Boolean isDirectory = model.isDirectory();
        logger.debug("Returns: {}", isDirectory);
        return isDirectory;
    }

    public boolean isSymbolicLink() {
        logger.debug("Check isSymbolicLink for {}", this);
        checkModel();
        boolean isSymbolicLink = model.isSymbolicLink();
        logger.debug("Returns: {}", isSymbolicLink);
        return isSymbolicLink;
    }

    public boolean isReadable() {
        logger.debug("Check isReadable for {}", this);
        checkModel();
        boolean isReadable = fileOperationProvider.isReadable(model);
        logger.debug("Returns: {}", isReadable);
        return isReadable;
    }

    public boolean isWritable() {
        logger.debug("Check isWritable for {}", this);
        checkModel();
        boolean isWritable = fileOperationProvider.isWritable(model);
        logger.debug("Returns: {}", isWritable);
        return isWritable;
    }

    public boolean isExecutable() {
        logger.debug("Check isExecutable for {}", this);
        checkModel();
        boolean isExecutable = fileOperationProvider.isExecutable(model);
        logger.debug("Returns: {}", isExecutable);
        return isExecutable;
    }

    public boolean isHidden() {
        logger.debug("Check isHidden for {}", this);
        checkModel();
        boolean isHidden = fileOperationProvider.isHidden(model);
        logger.debug("Returns: {}", isHidden);
        return isHidden;
    }

    public VirtualFile getParent() {
        logger.debug("Get parent for {}", this);
        VirtualFile parent = context.getParentFile(this);
        logger.debug("Returns: {}", parent);
        return parent;
    }

    public VirtualFile getRoot() {
        logger.debug("Get root for {}", this);
        checkModel();
        VirtualFile root = this;
        while (root.getParent() != null)
            root = root.getParent();
        logger.debug("Returns: {}", root);
        return root;
    }

    /**
     * Tests wether the file or directory exists.
     * @return true if the file exists; false otherwise
     */
    public Boolean exists() {
        logger.info("Check exists for {}", this);
        checkModel();
        Boolean exists = fileOperationProvider.exists(model);
        logger.info("Returns: {}", exists);
        return exists;
    }

    /**
     * Creates an empty file.
     */
    public void create() {
        logger.info("Create {}", this);
        checkModel();
        fileOperationProvider.create(model);
        updateModel();
    }

    public InputStream getInputStream() {
        logger.debug("Get Inputstream for {}", this);
        checkModel();
        return fileOperationProvider.getInputStream(model);
    }

    public OutputStream getOutputStream() {
        logger.debug("Get Outputstream for {}", this);
        checkModel();
        return fileOperationProvider.getOutputStream(model);
    }

    public List<AclEntry> getAcl() {
        logger.debug("Get ACL for {}", this);
        checkModel();
        List<AclEntry> acl = model.getAcl();
        logger.debug("Returns: {}", acl);
        return acl;
    }

    public void setAcl(List<AclEntry> acl) {
        logger.info("Set ACL to {} for {}", acl, this);
        checkModel();
        model.setAcl(acl);
        fileOperationProvider.setAcl(model);
        updateModel();
    }

    public UserPrincipal getOwner() {
        logger.debug("Get owner for {}", this);
        checkModel();
        UserPrincipal owner = model.getOwner();
        logger.debug("Returns: {}", owner);
        return owner;
    }

    public void setOwner(UserPrincipal owner) {
        logger.info("Set owner to {} for {}", owner, this);
        checkModel();
        model.setOwner(owner);
        fileOperationProvider.setOwner(model);
        updateModel();
    }

    public void setUrl(URL url) {
        logger.info("Set url to {} for {}", url, this);
        this.url = url;
    }

    public GroupPrincipal getGroup() {
        logger.debug("Get group for {}", this);
        checkModel();
        GroupPrincipal group = model.getGroup();
        logger.debug("Returns: {}" + group);
        return group;
    }

    public void setGroup(GroupPrincipal group) {
        logger.info("Set group to {} for {}", group, this);
        checkModel();
        model.setGroup(group);
        fileOperationProvider.setGroup(model);
        updateModel();
    }

    public void setLastAccessTime(FileTime time) {
        logger.info("Set lastAccessTime to {} for {}", time, this);
        checkModel();
        model.setLastAccessTime(time);
        fileOperationProvider.setLastAccessTime(model);
        updateModel();
    }

    public void setLastModifiedTime(FileTime time) {
        logger.info("Set lastModifiedTime to {} for {}", time, this);
        checkModel();
        model.setLastModifiedTime(time);
        fileOperationProvider.setLastModifiedTime(model);
        updateModel();
    }

    public void setAttributes(FileAttribute... attributes) {
        logger.info("Set attributes for {}", this);
        checkModel();
        model.setAttributes(new HashSet<>(Arrays.asList(attributes)));
        fileOperationProvider.setAttributes(model);
        updateModel();
    }

    public void setCreationTime(FileTime time) {
        logger.info("Set creationTime to {} for {}", time, this);
        checkModel();
        model.setCreationTime(time);
        fileOperationProvider.setCreationTime(model);
        updateModel();
    }

    //TODO create a move method that combines copy and delete.

    public Set<FileAttribute> getAttributes() {
        logger.debug("Get attributes for {}", this);
        checkModel();
        Set<FileAttribute> attributes = model.getAttributes();
        logger.debug("Returns: {}", FileUtils.getAttributesString(attributes.toArray(new FileAttribute[0])));
        return attributes;
    }

    public ContentType getContentType() {
        try (InputStream inputStream = new BufferedInputStream(getInputStream())) {
            DefaultDetector detector = context.getContentDetector();
            MediaType mediaType = detector.detect(inputStream, new Metadata());
            return new ContentType(mediaType);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    public VirtualArchive asArchive() {
        return new VirtualArchive(this, context);
    }

    public File asFile() {
        try {
            return new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new VirtualFileException(e);
        }
    }

    //TODO Provide also a method with a Set instead of Varargs
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

    //TODO provide method with set instead of Varargs

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

    public void rename(String newName) {
        logger.info("Rename {} to {}", this, newName);
        checkModel();
        fileOperationProvider.rename(model, newName);
    }

    /**
     * Finds files recursively matching a filter.
     *
     * @param filter A filter
     * @return A list of files.
     */
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

    /**
     * Lists contained files non-recursively.
     *
     * @return A list of files.
     */
    public List<VirtualFile> list() {
        logger.info("List children for {}", this);
        checkModel();
        List<VirtualFile> fileList = fileOperationProvider.list(model, null);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    /**
     * Lists contained files non-recursively filtered by a filter.
     *
     * @param filter The Filter
     * @return List of files.
     */
    public List<VirtualFile> list(Filter filter) {
        logger.info("List children for {} with filter {}", this, filter);
        checkModel();
        List<VirtualFile> fileList = fileOperationProvider.list(model, filter);
        logger.info("Returns: {} entries", fileList.size());
        return fileList;
    }

    public void move(VirtualFile target) {
        logger.info("Move {} to {}", this, target);
        checkModel();
        fileOperationProvider.move(model, target);
    }

    /**
     * Check whether file is an archive.
     *
     * @return True if file is an archive; false otherwise.
     */
    public boolean isArchive() {
        logger.debug("Check isArchive for {}", this);
        checkModel();
        Boolean isArchive = model.isArchive();
        logger.debug("Returns: {}", isArchive);
        return isArchive;
    }

    public Integer copy(VirtualFile targetFile) {
        logger.info("Copy {} to {}", this, targetFile);
        checkModel();
        Integer filesCopied = fileOperationProvider.copy(this, targetFile, null);
        logger.debug("Returns: {}", filesCopied);
        return filesCopied;
    }

    public Integer copy(VirtualFile targetFile, FileOperationListener listener) {
        logger.info("Copy {} to {} with FileOperationListener", this, targetFile);
        checkModel();
        Integer filesCopied = fileOperationProvider.copy(this, targetFile, listener);
        logger.debug("Returns: {}", filesCopied);
        return filesCopied;
    }

    public Boolean compare(VirtualFile targetFile) {
        logger.info("Compare {} with {}", this, targetFile);
        checkModel();
        Boolean result = fileOperationProvider.compare(this, targetFile, null);
        logger.debug("Returns: equal={}", result);
        return result;
    }

    public Boolean compare(VirtualFile targetFile, FileOperationListener listener) {
        logger.info("Compare {} with {} with FileOperationListener", this, targetFile);
        checkModel();
        Boolean result = fileOperationProvider.compare(this, targetFile, listener);
        logger.debug("Returns: equal={}", result);
        return result;
    }

    public void compress() {
        throw new NotImplementedException();
    }

    public void decompress() {
        throw new NotImplementedException();
    }

    /**
     * Updates the model with information from the physical file
     */
    public void refresh() {
        logger.info("Refresh {}", this);
        checkModel();
        updateModel();
    }

    public String toString() {
        return String.format("%s@%s [%s]", this.getClass().getSimpleName(),
                Integer.toHexString(System.identityHashCode(this)), maskedUrlString(url));
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VirtualFile otherFile = (VirtualFile) o;

        return compareTo(otherFile) == 0;
    }

    public int hashCode() {
        return url.toString().hashCode();
    }

    public int compareTo(VirtualFile o) {
        return url.toString().compareTo(url.toString());
    }

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
