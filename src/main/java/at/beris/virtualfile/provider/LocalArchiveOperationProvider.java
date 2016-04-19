/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.File;
import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.filter.Filter;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LocalArchiveOperationProvider extends LocalFileOperationProvider implements ArchiveOperationProvider {

    private final static String URL = "url";
    private final static String PARENT_URL = "parentUrl";

    public LocalArchiveOperationProvider(FileContext fileContext, Client client) {
        super(fileContext, client);
    }

    @Override
    public void create(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public List<File> list(FileModel model, Filter filter) throws IOException {
        List<File> fileList = new ArrayList<>();
        ArchiveInputStream ais = null;
        InputStream fis = null;

        URL rootUrl = model.getUrl();

        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            fis = new BufferedInputStream(new FileInputStream(new java.io.File(model.getPath())));
            ais = factory.createArchiveInputStream(fis);
            ArchiveEntry archiveEntry;

            while ((archiveEntry = ais.getNextEntry()) != null) {
                Map<String, URL> urlMap = getArchiveEntryURLMap(rootUrl, archiveEntry);
                File file = fileContext.newFile(fileContext.getFile(PARENT_URL.toString()), urlMap.get(URL));
                if (filter == null || filter.filter(file))
                    fileList.add(file);
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        } finally {
            if (ais != null)
                ais.close();
            if (fis != null)
                fis.close();
        }
        return fileList;
    }

    @Override
    public List<File> extract(FileModel model, File target) throws IOException {
        List<File> fileList = new ArrayList<>();
        ArchiveInputStream ais = null;
        InputStream fis = null;

        try {
            target.create();

            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            fis = new BufferedInputStream(new FileInputStream(new java.io.File(model.getPath())));
            ais = archiveStreamFactory.createArchiveInputStream(fis);
            ArchiveEntry archiveEntry;

            while ((archiveEntry = ais.getNextEntry()) != null) {
                Map<String, URL> urlMap = getArchiveEntryURLMap(target.getUrl(), archiveEntry);

                if (archiveEntry.isDirectory()) {
                    Files.createDirectory(new java.io.File(urlMap.get(URL).toURI()).toPath());
                } else {
                    OutputStream out = new FileOutputStream(new java.io.File(urlMap.get(URL).toURI()));
                    IOUtils.copy(ais, out);
                    out.close();
                }

                File file = fileContext.newFile(urlMap.get(URL));
                fileList.add(file);
            }
        } catch (ArchiveException e) {
            throw new IOException(e);
        } catch (URISyntaxException e) {
            throw new IOException(e);
        } finally {
            if (ais != null)
                ais.close();
            if (fis != null)
                fis.close();
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
