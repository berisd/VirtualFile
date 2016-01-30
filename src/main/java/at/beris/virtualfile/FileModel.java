/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import java.net.URL;
import java.util.Date;
import java.util.EnumSet;
import java.util.Set;

public class FileModel {
    private URL url;
    private Date lastModifiedTime;
    private Date lastAccessTime;
    private Date creationTime;
    private long size;
    private FileModel parent;
    private Set<Attribute> attributes;

    public FileModel() {
        attributes = EnumSet.noneOf(Attribute.class);
    }

    public URL getUrl() {
        return url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public Date getLastModifiedTime() {
        return lastModifiedTime;
    }

    public void setLastModifiedTime(Date lastModifiedTime) {
        this.lastModifiedTime = lastModifiedTime;
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
    }

    public void setLastAccessTime(Date lastAccessTime) {
        this.lastAccessTime = lastAccessTime;
    }

    public Date getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(Date creationTime) {
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

    public Set<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public Boolean isDirectory() {
        return url.getPath().endsWith("/");
    }

    public String getPath() {
        return url.getPath();
    }
}
