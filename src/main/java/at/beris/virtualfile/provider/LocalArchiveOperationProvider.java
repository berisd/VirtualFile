/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.FileOperation;
import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Consumer;

public class LocalArchiveOperationProvider extends LocalFileOperationProvider implements ArchiveOperationProvider {

    private final static String URL = "url";
    private final static String PARENT_URL = "parentUrl";

    public LocalArchiveOperationProvider(VirtualFileContext fileContext, Client client) {
        super(fileContext, client);
        this.supportedOperations = new HashSet<>(BASIC_FILE_OPERATIONS);
        this.supportedOperations.add(FileOperation.EXTRACT);
    }

    @Override
    public void create(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public List<VirtualFile> list(FileModel model, Filter filter) {
        List<VirtualFile> fileList = new ArrayList<>();
        URL rootUrl = model.getUrl();
        processArchiveEntries(model, listFileFromArchive(rootUrl, filter, fileList));
        return fileList;
    }

    @Override
    public List<VirtualFile> extract(FileModel model, VirtualFile target) {
        List<VirtualFile> fileList = new ArrayList<>();
        target.create();
        processArchiveEntries(model, copyFileFromArchive(target, fileList));
        return fileList;
    }

    private void processArchiveEntries(FileModel model, Consumer<ArchiveEntry> consumer) {
        try {
            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            InputStream fileInputStream = new BufferedInputStream(new FileInputStream(new File(model.getUrl().toURI())));
            ArchiveInputStream archiveInputStream = archiveStreamFactory.createArchiveInputStream(fileInputStream);
            iterateArchiveEntries(archiveInputStream, consumer);
        } catch (URISyntaxException | IOException | ArchiveException e) {
            throw new VirtualFileException(e);
        }
    }

    private void iterateArchiveEntries(ArchiveInputStream archiveInputStream, Consumer<ArchiveEntry> operation) {
        try {
            ArchiveEntry archiveEntry;
            while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
                operation.accept(archiveEntry);
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private Consumer<ArchiveEntry> listFileFromArchive(URL rootUrl, Filter filter, final List<VirtualFile> fileList) {
        return archiveEntry -> {
            try {
                Map<String, URL> urlMap = getArchiveEntryURLMap(rootUrl, archiveEntry);
                VirtualFile file = fileContext.newFile(urlMap.get(URL));
                if (filter == null || filter.filter(file))
                    fileList.add(file);
            } catch (MalformedURLException e) {
                throw new VirtualFileException(e);
            }
        };
    }

    private Consumer<ArchiveEntry> copyFileFromArchive(final VirtualFile target, final List<VirtualFile> fileList) {
        return archiveEntry -> {
            try {
                Map<String, URL> urlMap = getArchiveEntryURLMap(target.getUrl(), archiveEntry);

                if (archiveEntry.isDirectory()) {
                    Files.createDirectory(new File(urlMap.get(URL).toURI()).toPath());
                }
                VirtualFile file = fileContext.newFile(urlMap.get(URL));
                fileList.add(file);
            } catch (URISyntaxException | IOException e) {
                throw new VirtualFileException(e);
            }
        };
    }

    private Map<String, URL> getArchiveEntryURLMap(URL rootUrl, ArchiveEntry archiveEntry) throws MalformedURLException {
        Map<String, URL> urlMap = new HashMap<>();

        String archiveEntryPath = archiveEntry.getName();

        String[] pathParts = archiveEntryPath.split("/");
        String path = StringUtils.join(pathParts, "/", 0, pathParts.length - 1);

        String parentUrlString = rootUrl.toString() + (rootUrl.toString().endsWith("/") ? "" : "/")
                + path + (path.equals("") ? "" : "/");
        String urlString = parentUrlString + pathParts[pathParts.length - 1]
                + (archiveEntryPath.endsWith("/") ? "/" : "");

        urlMap.put(PARENT_URL, new URL(parentUrlString));
        urlMap.put(URL, new URL(urlString));
        return urlMap;
    }
}
