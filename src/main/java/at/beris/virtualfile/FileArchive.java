/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.provider.ArchiveOperationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

class FileArchive implements VirtualArchive {

    private static Logger logger = LoggerFactory.getLogger(FileArchive.class);

    private VirtualFile file;

    private VirtualFileContext context;

    private ArchiveOperationProvider archiveOperationProvider;

    public FileArchive(VirtualFile file, VirtualFileContext context) {
        this.file = file;
        this.context = context;
        this.archiveOperationProvider = context.getArchiveOperationProvider();
    }

    @Override
    public VirtualFile getVirtualFile() {
        return file;
    }

    @Override
    public void add(String path, VirtualFile file) {
        logger.info("Add {} to path {}", file, path);
        throw new NotImplementedException();
    }

    @Override
    public void createDirectory(String path, String name) {
        logger.info("Create directory {} at path {}", name, path);
        throw new NotImplementedException();
    }

    @Override
    public void remove(VirtualArchiveEntry archiveEntry) {
        throw new NotImplementedException();
    }

    @Override
    public void remove(String path, String name) {
        throw new NotImplementedException();
    }

    @Override
    public List<VirtualFile> extract(VirtualFile target) {
        logger.info("Extract {} to {}", this, target);
        return archiveOperationProvider.extract(this, target);
    }

    @Override
    public List<VirtualArchiveEntry> list() {
        return archiveOperationProvider.list(this);
    }

    @Override
    public List<VirtualArchiveEntry> list(String path) {
        return null;
    }

}
