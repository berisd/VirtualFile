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
import at.beris.virtualfile.cache.DisposableObject;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.CopyListener;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.AclEntry;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public interface VirtualFile extends DisposableObject {
    void add(VirtualFile file);

    //TODO use Set instead of Varargs
    void addAttributes(FileAttribute... attributes);

    Byte[] checksum();

    void copy(VirtualFile targetFile);

    void copy(VirtualFile targetFile, CopyListener listener);

    /**
     * Creates an empty file
     *
     * @throws IOException IOException
     */
    void create();

    void delete();

    void delete(VirtualFile file);

    Boolean exists();

    List<VirtualFile> extract(VirtualFile target);

    /**
     * Find files recursively matching a filter
     *
     * @param filter A filter
     * @return A list of files
     */
    List<VirtualFile> find(Filter filter);

    List<AclEntry> getAcl();

    //TODO create a move method that combines copy and delete
    Set<FileAttribute> getAttributes();

    FileTime getCreationTime();

    GroupPrincipal getGroup();

    InputStream getInputStream();

    FileTime getLastAccessTime();

    FileTime getLastModifiedTime();

    URL getLinkTarget();

    FileModel getModel();

    String getName();

    OutputStream getOutputStream();

    UserPrincipal getOwner();

    VirtualFile getParent();

    String getPath();

    VirtualFile getRoot();

    /**
     * Returns the size in bytes for a file and the number of contained items for a directory
     *
     * @return File size
     */
    long getSize();

    URL getUrl();

    /**
     * VirtualFile is an archive
     */
    boolean isArchive();

    /**
     * VirtualFile is archived within an archive
     */
    boolean isArchived();

    boolean isContainer();

    boolean isDirectory();

    boolean isRoot();

    boolean isSymbolicLink();

    /**
     * List contained files non-recursively
     *
     * @return A list of files
     */
    List<VirtualFile> list();

    /**
     * List contained files non-recursively filtered by a filter
     *
     * @return A list of files
     */
    List<VirtualFile> list(Filter filter);

    /**
     * Updates the model with information from the physical file
     */
    void refresh();

    //TODO use Set instead of Varargs
    void removeAttributes(FileAttribute... attributes);

    void setAcl(List<AclEntry> acl);

    //TODO use Set instead of Varargs
    void setAttributes(FileAttribute... attributes);

    void setCreationTime(FileTime time);

    void setGroup(GroupPrincipal group);

    void setLastAccessTime(FileTime time);

    void setLastModifiedTime(FileTime time);

    void setModel(FileModel model);

    void setOwner(UserPrincipal owner);

    void setUrl(URL url);

    void dispose();
}
