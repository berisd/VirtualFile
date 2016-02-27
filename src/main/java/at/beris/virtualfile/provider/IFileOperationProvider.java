/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.filter.IFilter;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface IFileOperationProvider {
    /**
     * Convenience method calling copy
     *
     * @param parent
     * @param child
     */
    void add(IFile parent, IFile child);

    byte[] checksum(IClient client, FileModel model);

    /**
     * Creates a new  physical representation
     *
     * @param model
     * @return
     */
    void create(IClient client, FileModel model);

    void delete(IClient client, FileModel model);

    boolean exists(IClient client, FileModel model);

    Set<IAttribute> getAttributes(IClient client, FileModel model);

    InputStream getInputStream(IClient client, FileModel model) throws IOException;

    OutputStream getOutputStream(IClient client, FileModel model) throws IOException;

    /**
     * List files in this file
     *
     * @param client
     * @param model
     * @param filter
     * @return
     */
    List<IFile> list(IClient client, FileModel model, Optional<IFilter> filter);

    /**
     * Updates the Model with information from the physical file.
     *
     * @param client
     * @param model
     */
    void updateModel(IClient client, FileModel model);

    void setAcl(IClient client, FileModel model);

    void setAttributes(IClient client, FileModel model);

    void setCreationTime(IClient client, FileModel model);

    void setGroup(IClient client, FileModel model);

    void setLastAccessTime(IClient client, FileModel model);

    void setLastModifiedTime(IClient client, FileModel model);

    void setOwner(IClient client, FileModel model);
}
