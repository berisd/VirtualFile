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
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.filter.Filter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

public interface FileOperationProvider {
    /**
     * Convenience method calling copy
     *
     * @param parent
     * @param child
     */
    void add(File parent, File child);

    byte[] checksum(Client client, FileModel model);

    /**
     * Creates a new  physical representation
     *
     * @param model
     * @return
     */
    void create(Client client, FileModel model);

    void delete(Client client, FileModel model);

    boolean exists(Client client, FileModel model);

    InputStream getInputStream(Client client, FileModel model) throws IOException;

    OutputStream getOutputStream(Client client, FileModel model) throws IOException;

    /**
     * List files in this file
     *
     * @param client
     * @param model
     * @param filter
     * @return
     */
    List<File> list(Client client, FileModel model, Filter filter);

    /**
     * Updates the Model with information from the physical file.
     *
     * @param client
     * @param model
     */
    void updateModel(Client client, FileModel model);

    void setAcl(Client client, FileModel model);

    void setAttributes(Client client, FileModel model);

    void setCreationTime(Client client, FileModel model);

    void setGroup(Client client, FileModel model);

    void setLastAccessTime(Client client, FileModel model);

    void setLastModifiedTime(Client client, FileModel model);

    void setOwner(Client client, FileModel model);
}
