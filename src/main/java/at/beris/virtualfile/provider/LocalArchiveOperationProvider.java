/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.exception.VirtualFileException;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.lang3.NotImplementedException;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocalArchiveOperationProvider extends LocalFileOperationProvider {
    @Override
    public IFile create(IClient client, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public void add(IFile parent, IFile child) {
        throw new NotImplementedException("");
    }

    @Override
    public List<IFile> list(IClient client, FileModel model) {
        List<IFile> fileList = new ArrayList<>();
        ArchiveInputStream ais = null;
        InputStream fis = null;

        URL rootUrl = model.getUrl();

        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            fis = new BufferedInputStream(new FileInputStream(new File(model.getPath())));
            ais = factory.createArchiveInputStream(fis);
            ArchiveEntry ae;

            while ((ae = ais.getNextEntry()) != null) {
                String archiveEntryPath = ae.getName();
                String[] pathParts = archiveEntryPath.split("/");
                String path = StringUtils.join(pathParts, "/", 0, pathParts.length - 1);

                String parentUrlString = rootUrl.toString() + "/" + path + (path != "" ? "/" : "");
                String urlString = parentUrlString + pathParts[pathParts.length - 1]
                        + (archiveEntryPath.endsWith("/") ? "/" : "");

                IFile file = FileManager.newFile(path.equals("") ? rootUrl : new URL(parentUrlString), new URL(urlString));
                fileList.add(file);
            }
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        } catch (ArchiveException e) {
            throw new VirtualFileException(e);
        } finally {
            try {
                if (ais != null)
                    ais.close();
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                throw new VirtualFileException(e);
            }
        }
        return fileList;
    }
}
