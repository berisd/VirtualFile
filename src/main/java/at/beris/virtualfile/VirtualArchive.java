/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import java.util.List;

/**
 * Representation of an Archive
 */
public interface VirtualArchive {

    /**
     * Get archive file
     *
     * @return
     */
    VirtualFile getVirtualFile();

    /**
     * Add a file to the archive
     *
     * @param path Insertion path for then file
     * @param file file
     */
    void add(String path, VirtualFile file);

    /**
     * Create a directory
     *
     * @param path Path where directory will be created
     * @param name Directory name
     */
    void createDirectory(String path, String name);

    /**
     * Remove an archiveEntry from the archive
     *
     * @param archiveEntry ArchiveEntry
     */
    void remove(VirtualArchiveEntry archiveEntry);

    /**
     * * Remove an archiveEntry from the archive
     *
     * @param path Path
     * @param name Name
     */
    void remove(String path, String name);

    /**
     * Extract archive contents to directory
     *
     * @param target Target Directory
     * @return
     */
    List<VirtualFile> extract(VirtualFile target);

    /**
     * List all archive entries
     *
     * @return List of archive entries
     */
    List<VirtualArchiveEntry> list();

    /**
     * List archive entries inside the path
     *
     * @param path Path
     * @return
     */
    List<VirtualArchiveEntry> list(String path);

}
