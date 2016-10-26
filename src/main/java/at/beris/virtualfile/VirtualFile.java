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
import at.beris.virtualfile.provider.operation.CompareResult;
import at.beris.virtualfile.provider.operation.FileOperationListener;

import java.io.File;
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

    VirtualArchive asArchive();

    File asFile();

    //TODO use Set instead of Varargs
    void addAttributes(FileAttribute... attributes);

    Byte[] checksum();

    Integer copy(VirtualFile targetFile);

    Integer copy(VirtualFile targetFile, FileOperationListener listener);

    CompareResult compare(VirtualFile targetFile);

    CompareResult compare(VirtualFile targetFile, FileOperationListener listener);

    void compress();

    void decompress();

    /**
     * Creates an empty file
     */
    void create();

    void delete();

    Boolean exists();

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

    ContentType getContentType();

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
     * Check if file is an archive
     *
     * @return True if file is an archive
     */
    boolean isArchive();

    boolean isDirectory();

    boolean isSymbolicLink();

    boolean isReadable();

    boolean isWritable();

    boolean isExecutable();

    boolean isHidden();

    /**
     * List contained files non-recursively
     *
     * @return A list of files
     */
    List<VirtualFile> list();

    /**
     * List contained files non-recursively filtered by a filter
     *
     * @param filter Filter
     * @return List of files
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
