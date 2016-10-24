/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.Archive;
import at.beris.virtualfile.CustomArchiveEntry;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.exception.VirtualFileException;
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
import java.util.function.Consumer;

public class ArchiveOperationProvider {

    private final static String URL = "url";
    private final static String PARENT_URL = "parentUrl";

    public List<CustomArchiveEntry> list(Archive archive) {
        List<CustomArchiveEntry> archiveEntryList = new ArrayList<>();

        IOSructure ioSructure = new IOSructure();
        ioSructure.setArchive(archive);
        ioSructure.setArchiveEntryList(archiveEntryList);
        processArchiveEntries(ioSructure, listFileFromArchive());
        return archiveEntryList;
    }

    public List<VirtualFile> extract(Archive archive, VirtualFile target) {
        List<VirtualFile> fileList = new ArrayList<>();
        target.create();
        IOSructure ioSructure = new IOSructure();
        ioSructure.setArchive(archive);
        ioSructure.setTarget(target);
        ioSructure.setFileList(fileList);
        processArchiveEntries(ioSructure, copyFileFromArchive());
        return fileList;
    }

    private void processArchiveEntries(IOSructure ioSructure, Consumer<IOSructure> consumer) {
        Archive archive = ioSructure.getArchive();
        ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
        try (InputStream fileInputStream = new BufferedInputStream(archive.getFile().getInputStream()); ArchiveInputStream archiveInputStream = archiveStreamFactory.createArchiveInputStream(fileInputStream)) {
            ioSructure.setArchiveInputStream(archiveInputStream);
            iterateArchiveEntries(ioSructure, consumer);
        } catch (ArchiveException | IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private void iterateArchiveEntries(IOSructure ioSructure, Consumer<IOSructure> operation) {
        try {
            ArchiveInputStream archiveInputStream = ioSructure.getArchiveInputStream();
            ArchiveEntry archiveEntry;
            while ((archiveEntry = archiveInputStream.getNextEntry()) != null) {
                ioSructure.setArchiveEntry(archiveEntry);
                operation.accept(ioSructure);
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private Consumer<IOSructure> listFileFromArchive() {
        return ioSructure -> {
            ArchiveEntry archiveEntry = ioSructure.getArchiveEntry();
            List<CustomArchiveEntry> archiveEntryList = ioSructure.getArchiveEntryList();
            CustomArchiveEntry entry = new CustomArchiveEntry();
            entry.setName(archiveEntry.getName());
            archiveEntryList.add(entry);
        };
    }

    private Consumer<IOSructure> copyFileFromArchive() {
        return ioSructure -> {
            try {
                Archive archive = ioSructure.getArchive();
                ArchiveEntry archiveEntry = ioSructure.getArchiveEntry();
                List<VirtualFile> fileList = ioSructure.getFileList();
                VirtualFile target = ioSructure.getTarget();
                ArchiveInputStream archiveInputStream = ioSructure.getArchiveInputStream();
                Map<String, URL> urlMap = getArchiveEntryURLMap(target.getUrl(), archiveEntry);

                if (archiveEntry.isDirectory()) {
                    Files.createDirectory(new File(urlMap.get(URL).toURI()).toPath());
                } else {
                    OutputStream out = new FileOutputStream(new File(urlMap.get(URL).toURI()));
                    IOUtils.copy(archiveInputStream, out);
                    out.close();
                }

                VirtualFile file = archive.getContext().newFile(urlMap.get(URL));
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

    private class IOSructure {
        private ArchiveInputStream archiveInputStream;
        private Archive archive;
        private VirtualFile target;
        private List<VirtualFile> fileList;
        private List<CustomArchiveEntry> archiveEntryList;
        private ArchiveEntry archiveEntry;

        public ArchiveInputStream getArchiveInputStream() {
            return archiveInputStream;
        }

        public void setArchiveInputStream(ArchiveInputStream archiveInputStream) {
            this.archiveInputStream = archiveInputStream;
        }

        public Archive getArchive() {
            return archive;
        }

        public void setArchive(Archive archive) {
            this.archive = archive;
        }

        public VirtualFile getTarget() {
            return target;
        }

        public void setTarget(VirtualFile target) {
            this.target = target;
        }

        public List<VirtualFile> getFileList() {
            return fileList;
        }

        public void setFileList(List<VirtualFile> fileList) {
            this.fileList = fileList;
        }

        public List<CustomArchiveEntry> getArchiveEntryList() {
            return archiveEntryList;
        }

        public void setArchiveEntryList(List<CustomArchiveEntry> archiveEntryList) {
            this.archiveEntryList = archiveEntryList;
        }

        public ArchiveEntry getArchiveEntry() {
            return archiveEntry;
        }

        public void setArchiveEntry(ArchiveEntry archiveEntry) {
            this.archiveEntry = archiveEntry;
        }
    }
}
