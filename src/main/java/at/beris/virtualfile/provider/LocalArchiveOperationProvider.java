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
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.IFilter;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;

public class LocalArchiveOperationProvider extends LocalFileOperationProvider implements IArchiveOperationProvider {

    private final static String URL = "url";
    private final static String PARENT_URL = "parentUrl";

    @Override
    public void create(IClient client, FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void add(IFile parent, IFile child) {
        throw new NotImplementedException();
    }

    @Override
    public List<IFile> list(IClient client, FileModel model, IFilter filter) {
        List<IFile> fileList = new ArrayList<>();
        ArchiveInputStream ais = null;
        InputStream fis = null;

        URL rootUrl = model.getUrl();

        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            fis = new BufferedInputStream(new FileInputStream(new File(model.getPath())));
            ais = factory.createArchiveInputStream(fis);
            ArchiveEntry archiveEntry;

            while ((archiveEntry = ais.getNextEntry()) != null) {
                Map<String, URL> urlMap = getArchiveEntryURLMap(rootUrl, archiveEntry);
                IFile file = FileManager.newFile(urlMap.get(PARENT_URL), urlMap.get(URL));
                if (filter == null || filter.filter(file))
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

    @Override
    public List<IFile> extract(IClient client, FileModel model, IFile target) {
        List<IFile> fileList = new ArrayList<>();
        ArchiveInputStream ais = null;
        InputStream fis = null;

        try {
            target.create();

            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            fis = new BufferedInputStream(new FileInputStream(new File(model.getPath())));
            ais = archiveStreamFactory.createArchiveInputStream(fis);
            ArchiveEntry archiveEntry;

            while ((archiveEntry = ais.getNextEntry()) != null) {
                Map<String, URL> urlMap = getArchiveEntryURLMap(target.getUrl(), archiveEntry);

                if (archiveEntry.isDirectory()) {
                    Files.createDirectory(new File(urlMap.get(URL).toURI()).toPath());
                } else {
                    OutputStream out = new FileOutputStream(new File(urlMap.get(URL).toURI()));
                    IOUtils.copy(ais, out);
                    out.close();
                }

                IFile file = FileManager.newFile(urlMap.get(URL));
                fileList.add(file);
            }
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        } catch (ArchiveException e) {
            throw new VirtualFileException(e);
        } catch (URISyntaxException e) {
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

    private Map<String, URL> getArchiveEntryURLMap(URL rootUrl, ArchiveEntry archiveEntry) throws MalformedURLException {
        Map<String, URL> urlMap = new HashMap<>();

        String archiveEntryPath = archiveEntry.getName();

        String[] pathParts = archiveEntryPath.split("/");
        String path = StringUtils.join(pathParts, "/", 0, pathParts.length - 1);

        String parentUrlString = rootUrl.toString() + (rootUrl.toString().endsWith("/") ? "" : "/")
                + path + (path != "" ? "/" : "");
        String urlString = parentUrlString + pathParts[pathParts.length - 1]
                + (archiveEntryPath.endsWith("/") ? "/" : "");

        urlMap.put(PARENT_URL, new URL(parentUrlString));
        urlMap.put(URL, new URL(urlString));
        return urlMap;
    }
}
