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

public class Archive {

    private static Logger logger = LoggerFactory.getLogger(Archive.class);

    private VirtualFile file;

    private VirtualFileContext context;

    private ArchiveOperationProvider archiveOperationProvider;

    public Archive(VirtualFile file, VirtualFileContext context) {
        this.file = file;
        this.context = context;
        //TODO ArchiveOperationProvider muss vom context kommen
        this.archiveOperationProvider = new ArchiveOperationProvider();
    }

    public VirtualFile getFile() {
        return file;
    }

    public void add(VirtualFile file) {
        logger.info("Add {} to {}", file, this);
        throw new NotImplementedException();
    }

    public void delete(VirtualFile file) {
        throw new NotImplementedException();
    }

    public List<VirtualFile> extract(VirtualFile target) {
        logger.info("Extract {} to {}", this, target);
        return archiveOperationProvider.extract(this, target);
    }

    public List<CustomArchiveEntry> list() {
        return archiveOperationProvider.list(this);
    }

    public List<CustomArchiveEntry> list(String path) {
        return null;
    }

    public VirtualFileContext getContext() {
        return context;
    }
}
