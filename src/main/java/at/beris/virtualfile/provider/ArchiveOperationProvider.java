/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.VirtualArchive;
import at.beris.virtualfile.VirtualArchiveEntry;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
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
import java.util.*;
import java.util.function.Consumer;

public class ArchiveOperationProvider {

    private final static String URL = "url";
    private final static String PARENT_URL = "parentUrl";

    private VirtualFileContext context;

    public ArchiveOperationProvider(VirtualFileContext context) {
        this.context = context;
    }

    public List<VirtualArchiveEntry> list(VirtualArchive archive) {
        List<VirtualArchiveEntry> archiveEntryList = new ArrayList<>();

        IOSructure ioSructure = new IOSructure();
        ioSructure.setArchive(archive);
        ioSructure.setArchiveEntryList(archiveEntryList);
        processArchiveEntries(ioSructure, listFileFromArchive());
        return archiveEntryList;
    }

    public List<VirtualFile> extract(VirtualArchive archive, VirtualFile target) {
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
        VirtualArchive archive = ioSructure.getArchive();
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
                ioSructure.setCommonsArchiveEntry(archiveEntry);
                operation.accept(ioSructure);
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    private Consumer<IOSructure> listFileFromArchive() {
        return ioSructure -> {
            ArchiveEntry commonsArchiveEntry = ioSructure.getCommonsArchiveEntry();
            List<VirtualArchiveEntry> archiveEntryList = ioSructure.getArchiveEntryList();
            VirtualArchiveEntry entry = context.createArchiveEntry();

            List<String> pathParts = new ArrayList<>(Arrays.asList(StringUtils.split(commonsArchiveEntry.getName(), '/')));
            String name = pathParts.remove(pathParts.size() - 1);
            String path = StringUtils.join(pathParts, '/');

            entry.setName(name);
            entry.setPath(path);
            entry.setLastModified(commonsArchiveEntry.getLastModifiedDate().toInstant());
            entry.setSize(commonsArchiveEntry.getSize());
            entry.setDirectory(commonsArchiveEntry.isDirectory());
            archiveEntryList.add(entry);
        };
    }

    private Consumer<IOSructure> copyFileFromArchive() {
        return ioSructure -> {
            try {
                VirtualArchive archive = ioSructure.getArchive();
                ArchiveEntry archiveEntry = ioSructure.getCommonsArchiveEntry();
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

                VirtualFile file = context.newFile(urlMap.get(URL));
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
        private VirtualArchive archive;
        private VirtualFile target;
        private List<VirtualFile> fileList;
        private List<VirtualArchiveEntry> archiveEntryList;
        private ArchiveEntry commonsArchiveEntry;

        public ArchiveInputStream getArchiveInputStream() {
            return archiveInputStream;
        }

        public void setArchiveInputStream(ArchiveInputStream archiveInputStream) {
            this.archiveInputStream = archiveInputStream;
        }

        public VirtualArchive getArchive() {
            return archive;
        }

        public void setArchive(VirtualArchive archive) {
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

        public List<VirtualArchiveEntry> getArchiveEntryList() {
            return archiveEntryList;
        }

        public void setArchiveEntryList(List<VirtualArchiveEntry> archiveEntryList) {
            this.archiveEntryList = archiveEntryList;
        }

        public ArchiveEntry getCommonsArchiveEntry() {
            return commonsArchiveEntry;
        }

        public void setCommonsArchiveEntry(ArchiveEntry archiveEntry) {
            this.commonsArchiveEntry = archiveEntry;
        }
    }
}
