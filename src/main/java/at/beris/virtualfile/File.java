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

public interface File {
    void add(File file) throws IOException;

    void addAttributes(FileAttribute... attributes) throws IOException;

    Byte[] checksum() throws IOException;

    void copy(File targetFile) throws IOException;

    void copy(File targetFile, CopyListener listener) throws IOException;

    /**
     * Creates an empty file
     *
     * @return true if the named file does not exist and was successfully created; false if the named file already exists
     * @throws IOException
     */
    void create() throws IOException;

    void delete() throws IOException;

    void delete(File file) throws IOException;

    Boolean exists() throws IOException;

    List<File> extract(File target) throws IOException;

    /**
     * Find files recursively matching a filter
     *
     * @param filter A filter
     * @return A list of files
     */
    List<File> find(Filter filter) throws IOException;

    List<AclEntry> getAcl() throws IOException;

    //TODO create a move method that combines copy and delete
    Set<FileAttribute> getAttributes() throws IOException;

    FileTime getCreationTime() throws IOException;

    GroupPrincipal getGroup() throws IOException;

    InputStream getInputStream() throws IOException;

    FileTime getLastAccessTime() throws IOException;

    FileTime getLastModifiedTime() throws IOException;

    FileModel getModel() throws IOException;

    String getName() throws IOException;

    OutputStream getOutputStream() throws IOException;

    UserPrincipal getOwner() throws IOException;

    File getParent() throws IOException;

    String getPath() throws IOException;

    File getRoot() throws IOException;

    /**
     * Returns the size in bytes for a file and the number of contained items for a directory
     *
     * @return
     */
    long getSize() throws IOException;

    URL getUrl() throws IOException;

    /**
     * File is an archive
     */
    boolean isArchive() throws IOException;

    /**
     * File is archived within an archive
     */
    boolean isArchived() throws IOException;

    boolean isContainer() throws IOException;

    boolean isDirectory() throws IOException;

    boolean isRoot() throws IOException;

    boolean isSymbolicLink() throws IOException;

    /**
     * List contained files non-recursively
     *
     * @return A list of files
     */
    List<File> list() throws IOException;

    /**
     * List contained files non-recursively filtered by a filter
     *
     * @return A list of files
     */
    List<File> list(Filter filter) throws IOException;

    /**
     * Updates the model with information from the physical file
     */
    void refresh() throws IOException;

    void removeAttributes(FileAttribute... attributes) throws IOException;

    void setAcl(List<AclEntry> acl) throws IOException;

    void setAttributes(FileAttribute... attributes) throws IOException;

    void setCreationTime(FileTime time) throws IOException;

    void setGroup(GroupPrincipal group) throws IOException;

    void setLastAccessTime(FileTime time) throws IOException;

    void setLastModifiedTime(FileTime time) throws IOException;

    void setModel(FileModel model) throws IOException;

    void setOwner(UserPrincipal owner) throws IOException;

    /**
     * Free the file with all resources
     */
    void dispose() throws IOException;
}
