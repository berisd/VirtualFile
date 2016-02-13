/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.operation.CopyListener;
import at.beris.virtualfile.provider.IFileOperationProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface IFile {
    URL getUrl();

    FileModel getModel();

    IClient getClient();

    IFileOperationProvider getFileOperationProvider();

    String getName();

    Date getLastModified();

    long getSize();

    void setSize(long size);

    boolean isDirectory();

    void setParent(IFile parent);

    IFile getParent();

    IFile getRoot();

    boolean isRoot();

    //TODO create a move method that combines copy and delete

    //TODO delete method. a file is added with copy
    void add(IFile file);

    boolean exists();

    Set<Attribute> getAttributes();

    List<IFile> list();

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

    int compareTo(IFile file);

    /**
     * Creates an empty file
     *
     * @return true if the named file does not exist and was successfully created; false if the named file already exists
     * @throws IOException
     */
    void create();

    InputStream getInputStream() throws IOException;

    OutputStream getOutputStream() throws IOException;
}
