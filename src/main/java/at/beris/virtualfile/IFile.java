/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.filter.IFilter;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.provider.IFileOperationProvider;

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

public interface IFile {
    URL getUrl();

    FileModel getModel();

    IClient getClient();

    IFileOperationProvider getFileOperationProvider();

    /**
     * Updates the model with information from the physical file
     */
    void refresh();

    String getName();

    FileTime getCreationTime();

    FileTime getLastModifiedTime();

    FileTime getLastAccessTime();

    long getSize();

    boolean isDirectory();

    boolean isSymbolicLink();

    boolean isContainer();

    IFile getParent();

    IFile getRoot();

    boolean isRoot();

    //TODO create a move method that combines copy and delete

    boolean exists();

    Set<IAttribute> getAttributes();

    void setAttributes(IAttribute... attributes);

    void addAttributes(IAttribute... attributes);

    void removeAttributes(IAttribute... attributes);

    /**
     * Find files recursively matching a filter
     *
     * @param filter A filter
     * @return A list of files
     */
    List<IFile> find(IFilter filter);

    /**
     * List contained files non-recursively
     *
     * @return A list of files
     */
    List<IFile> list();

    /**
     * List contained files non-recursively filtered by a filter
     *
     * @return A list of files
     */
    List<IFile> list(IFilter filter);

    String getPath();

    void delete();

    byte[] checksum();

    /**
     * File is an archive
     */
    boolean isArchive();

    /**
     * File is archived within an archive
     */
    boolean isArchived();

    void copy(IFile targetFile, CopyListener listener);

    /**
     * Creates an empty file
     *
     * @return true if the named file does not exist and was successfully created; false if the named file already exists
     * @throws IOException
     */
    void create();

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;

    List<AclEntry> getAcl();

    void setAcl(List<AclEntry> acl);

    UserPrincipal getOwner();

    void setOwner(UserPrincipal owner);

    GroupPrincipal getGroup();

    void setGroup(GroupPrincipal group);
}
