/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.filter.Filter;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileOperationProvider {
    Byte[] checksum(FileModel model);

    /**
     * Creates a new  physical representation
     *
     * @param model
     * @return
     */
    void create(FileModel model);

    void delete(FileModel model);

    Boolean exists(FileModel model);

    InputStream getInputStream(FileModel model);

    OutputStream getOutputStream(FileModel model);

    /**
     * List files in this file
     *
     * @param model
     * @param filter
     * @return
     */
    List<File> list(FileModel model, Filter filter);

    /**
     * Updates the Model with information from the physical file.
     *
     * @param model
     */
    void updateModel(FileModel model);

    void setAcl(FileModel model);

    void setAttributes(FileModel model);

    void setCreationTime(FileModel model);

    void setGroup(FileModel model);

    void setLastAccessTime(FileModel model);

    void setLastModifiedTime(FileModel model);

    void setOwner(FileModel model);
}
