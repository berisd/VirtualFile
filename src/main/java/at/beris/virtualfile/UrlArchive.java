/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.operation.FileOperation;
import at.beris.virtualfile.operation.FileOperationEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.List;
import java.util.Map;

public class UrlArchive extends UrlFileContainer implements Archive {
    private final static Logger LOGGER = LoggerFactory.getLogger(UrlArchive.class);

    public UrlArchive(File parent, URL url, FileModel model, Map<FileOperationEnum, FileOperation> fileOperationMap) {
        super(parent, url, model, fileOperationMap);
    }

    @Override
    public List<File> extract(File target) {
        LOGGER.info("Extract " + this + " to " + target);
        List<File> fileList = executeOperation(FileOperationEnum.EXTRACT, target, null, (Void) null);
        LOGGER.info("Returns: " + fileList.size() + " entries");
        return fileList;
    }
}
