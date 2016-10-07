/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.CopyListener;
import at.beris.virtualfile.provider.operation.FileOperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Set;

public interface FileOperationProvider {
    Byte[] checksum(FileModel model) throws IOException;

    /**
     * Creates a new  physical representation
     *
     * @param model
     * @return
     */
    void create(FileModel model) throws IOException;

    void delete(FileModel model) throws IOException;

    Boolean exists(FileModel model) throws IOException;

    InputStream getInputStream(FileModel model) throws IOException;

    OutputStream getOutputStream(FileModel model) throws IOException;

    /**
     * List files in this file
     *
     * @param model
     * @param filter
     * @return
     */
    List<VirtualFile> list(FileModel model, Filter filter) throws IOException;

    /**
     * Updates the Model with information from the physical file.
     *
     * @param model
     */
    void updateModel(FileModel model) throws IOException;

    void setAcl(FileModel model) throws IOException;

    void setAttributes(FileModel model) throws IOException;

    void setCreationTime(FileModel model) throws IOException;

    void setGroup(FileModel model) throws IOException;

    void setLastAccessTime(FileModel model) throws IOException;

    void setLastModifiedTime(FileModel model) throws IOException;

    void setOwner(FileModel model) throws IOException;

    List<VirtualFile> extract(FileModel model, VirtualFile target) throws IOException;

    boolean isOperationSupported(FileOperation fileOperation);

    Set<FileOperation> supportedOperations();

    void add(FileModel model, VirtualFile file) throws IOException;

    void addAttributes(FileModel model) throws IOException;

    void removeAttributes(FileModel model) throws IOException;

    void copy(VirtualFile sourceFile, VirtualFile targetFile, CopyListener listener) throws IOException;

    void dispose();
}
