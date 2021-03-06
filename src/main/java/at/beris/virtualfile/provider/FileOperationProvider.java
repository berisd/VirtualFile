/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.FileOperation;
import at.beris.virtualfile.provider.operation.FileOperationListener;
import at.beris.virtualfile.util.DisposableObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public interface FileOperationProvider<C> extends DisposableObject {

    C getClient();

    Byte[] checksum(FileModel model);

    /**
     * Creates a new  physical representation
     *
     * @param model FileModel
     */
    void create(FileModel model);

    void delete(FileModel model);

    Boolean exists(FileModel model);

    InputStream getInputStream(FileModel model);

    OutputStream getOutputStream(FileModel model);

    /**
     * List files in this file
     *
     * @param model  FileModel
     * @param filter Filter
     * @return List of files
     */
    List<UrlFile> list(FileModel model, Filter filter);

    /**
     * Updates the Model with information from the physical file.
     *
     * @param model FileModel
     */
    void updateModel(FileModel model);

    void setAcl(FileModel model);

    void setAttributes(FileModel model);

    void setCreationTime(FileModel model);

    void setGroup(FileModel model);

    void setLastAccessTime(FileModel model);

    void setLastModifiedTime(FileModel model);

    void setOwner(FileModel model);

    List<UrlFile> extract(FileModel model, UrlFile target);

    boolean isOperationSupported(FileOperation fileOperation);

    Set<FileOperation> supportedOperations();

    void add(FileModel model, UrlFile file);

    void addAttributes(FileModel model);

    void removeAttributes(FileModel model);

    void rename(FileModel model, String newName);

    void move(FileModel model, UrlFile targetFile);

    Integer copy(UrlFile sourceFile, UrlFile targetFile, FileOperationListener listener);

    Boolean compare(UrlFile sourceFile, UrlFile targetFile, FileOperationListener listener);

    void dispose();

    boolean isReadable(FileModel model);

    boolean isWritable(FileModel model);

    boolean isExecutable(FileModel model);

    boolean isHidden(FileModel model);
}
