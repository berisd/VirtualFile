/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.operation.CopyListener;

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

public interface File {
    void add(File file);

    void addAttributes(FileAttribute... attributes);

    Byte[] checksum();

    void copy(File targetFile);

    void copy(File targetFile, CopyListener listener);

    /**
     * Creates an empty file
     *
     * @return true if the named file does not exist and was successfully created; false if the named file already exists
     * @throws IOException
     */
    void create();

    void delete();

    void delete(File file);

    Boolean exists();

    List<File> extract(File target);

    /**
     * Find files recursively matching a filter
     *
     * @param filter A filter
     * @return A list of files
     */
    List<File> find(Filter filter);

    List<AclEntry> getAcl();

    //TODO create a move method that combines copy and delete
    Set<FileAttribute> getAttributes();

    FileTime getCreationTime();

    GroupPrincipal getGroup();

    InputStream getInputStream();

    FileTime getLastAccessTime();

    FileTime getLastModifiedTime();

    FileModel getModel();

    String getName();

    OutputStream getOutputStream();

    UserPrincipal getOwner();

    File getParent();

    String getPath();

    File getRoot();

    /**
     * Returns the size in bytes for a file and the number of contained items for a directory
     *
     * @return
     */
    long getSize();

    URL getUrl();

    /**
     * File is an archive
     */
    boolean isArchive();

    /**
     * File is archived within an archive
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
    List<File> list();

    /**
     * List contained files non-recursively filtered by a filter
     *
     * @return A list of files
     */
    List<File> list(Filter filter);

    /**
     * Updates the model with information from the physical file
     */
    void refresh();

    void removeAttributes(FileAttribute... attributes);

    void setAcl(List<AclEntry> acl);

    void setAttributes(FileAttribute... attributes);

    void setCreationTime(FileTime time);

    void setGroup(GroupPrincipal group);

    void setLastAccessTime(FileTime time);

    void setLastModifiedTime(FileTime time);

    void setOwner(UserPrincipal owner);

    /**
     * Free the file with all resources
     */
    void dispose();
}
