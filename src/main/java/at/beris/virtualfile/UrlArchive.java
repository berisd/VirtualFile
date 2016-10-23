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
import at.beris.virtualfile.provider.LocalArchivedFileOperationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;

class UrlArchive extends UrlFile implements VirtualArchive {

    private static Logger logger = LoggerFactory.getLogger(UrlArchive.class);

    public UrlArchive(URL url, UrlFileContext context) {
        super(url, context);
        this.fileOperationProvider = new LocalArchivedFileOperationProvider(context, null);
    }

    @Override
    public void add(VirtualFile file) {
        logger.info("Add {} to {}", file, this);
        checkModel();
        fileOperationProvider.add(model, file);
    }

    @Override
    public void delete(VirtualFile file) {
        throw new NotImplementedException();
    }

    @Override
    public List<VirtualFile> extract(VirtualFile target) {
        logger.info("Extract {} to {}", this, target);
        checkModel();
        return fileOperationProvider.extract(model, target);
    }
}
