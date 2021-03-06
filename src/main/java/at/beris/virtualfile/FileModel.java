/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.util.FileUtils;

import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FileModel {
    private URL url;
    private URL linkTarget;     // contains link target if file is a symbolic link
    private FileTime lastModifiedTime;
    private FileTime lastAccessTime;
    private FileTime creationTime;
    private long size;
    private FileModel parent;
    private Set<FileAttribute> attributes;
    private boolean isSymbolicLink;
    private UserPrincipal owner;
    private GroupPrincipal group;
    private List<AclEntry> acl;
    private boolean isDirectory;
    private boolean fileExists;

    public FileModel() {
        attributes = new HashSet<>();
        acl = new ArrayList<>();
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public FileTime getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(FileTime lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public FileTime getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(FileTime lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public FileTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(FileTime creationTime) {
        this.creationTime = creationTime;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public FileModel getParent() {
        return parent;
    }

    public void setParent(FileModel parent) {
        this.parent = parent;
    }

    public Set<FileAttribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<FileAttribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(FileAttribute attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(FileAttribute attribute) {
        attributes.remove(attribute);
    }

    public void setDirectory(boolean isDirectory) {
        this.isDirectory = isDirectory;
    }

    public Boolean isDirectory() {
        return (fileExists && isDirectory) || (!fileExists && url.getPath().endsWith("/"));
    }

    public Boolean isArchive() {
        String[] pathParts = url.toString().split("/");
        return FileUtils.isArchive(pathParts[pathParts.length - 1]);
    }

    public boolean isFileExists() {
        return fileExists;
    }

    public void setFileExists(boolean fileExists) {
        this.fileExists = fileExists;
    }

    public boolean isSymbolicLink() {
        return isSymbolicLink;
    }

    public void setSymbolicLink(boolean symbolicLink) {
        isSymbolicLink = symbolicLink;
    }

    public UserPrincipal getOwner() {
        return owner;
    }

    public void setOwner(UserPrincipal owner) {
        this.owner = owner;
    }

    public GroupPrincipal getGroup() {
        return group;
    }

    public void setGroup(GroupPrincipal group) {
        this.group = group;
    }

    public List<AclEntry> getAcl() {
        return acl;
    }

    public void setAcl(List<AclEntry> acl) {
        this.acl = acl;
    }

    public URL getLinkTarget() {
        return linkTarget;
    }

    public void setLinkTarget(URL linkTarget) {
        this.linkTarget = linkTarget;
    }

    public void clear() {
        acl.clear();
        acl = null;
        attributes.clear();
        attributes = null;
        lastModifiedTime = null;
        lastAccessTime = null;
        creationTime = null;
        parent = null;
        owner = null;
        group = null;
        url = null;
        linkTarget = null;
    }
}
