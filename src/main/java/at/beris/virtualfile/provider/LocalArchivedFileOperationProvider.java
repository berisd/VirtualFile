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
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.FileUtils;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class LocalArchivedFileOperationProvider extends AbstractFileOperationProvider {

    public LocalArchivedFileOperationProvider(VirtualFileContext fileContext, Client client) {
        super(fileContext, client);
    }

    @Override
    public List<VirtualFile> list(FileModel model, Filter filter) {
        List<VirtualFile> files = new ArrayList<>();
//        VirtualFile backFile = add(this.archiveFile, createEmptyArchiveEntry());
//        backFile.setParent(this.parentFile);
//
//        files.add(backFile);
//        for (VirtualFile childFile : children) {
//            files.add(childFile);
//        }

        return files;
    }


    @Override
    public void create(FileModel model) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(model.getUrl().toURI()));
            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            ArchiveOutputStream archiveOutputStream = archiveStreamFactory.createArchiveOutputStream(ArchiveStreamFactory.ZIP, fileOutputStream);
            archiveOutputStream.close();

        } catch (ArchiveException | URISyntaxException | IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public Boolean exists(FileModel model) {
        String archivePath = getArchivePath(model);
        String targetArchiveEntryPath = model.getUrl().getPath().substring(archivePath.length() + 1);
        ArchiveStreamFactory factory = new ArchiveStreamFactory();
        try (InputStream fis = new BufferedInputStream(new FileInputStream(new File(archivePath)));
             ArchiveInputStream ais = factory.createArchiveInputStream(fis)) {
            ArchiveEntry archiveEntry;
            while ((archiveEntry = ais.getNextEntry()) != null) {
                String archiveEntryPath = archiveEntry.getName();

                if (archiveEntryPath.equals(targetArchiveEntryPath)) {
                    model.setSize(archiveEntry.getSize());
                    model.setLastModifiedTime(FileTime.fromMillis(archiveEntry.getLastModifiedDate().getTime()));

                    if (model.getUrl().toString().endsWith("/") && (!archiveEntry.isDirectory())) {
                        String urlString = model.getUrl().toString();
                        model.setUrl(UrlUtils.newUrl(urlString.substring(0, urlString.length() - 1)));
                    } else if (!model.getUrl().toString().endsWith("/") && (archiveEntry.isDirectory())) {
                        String urlString = model.getUrl().toString() + "/";
                        model.setUrl(UrlUtils.newUrl(urlString));
                    }
                    break;
                }
            }
        } catch (ArchiveException | IOException e) {
            throw new VirtualFileException(e);
        }
        return false;
    }

    @Override
    public void delete(FileModel file) {
        throw new NotImplementedException();
    }

    @Override
    public Byte[] checksum(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void updateModel(FileModel model) {
        //TODO Call new class ArchiveClient here
    }

    @Override
    public void setAcl(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public OutputStream getOutputStream(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setAttributes(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setCreationTime(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setGroup(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setLastAccessTime(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setLastModifiedTime(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setOwner(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public List<VirtualFile> extract(FileModel model, VirtualFile target) {
        throw new OperationNotSupportedException();
    }

    private String getArchivePath(FileModel model) {
        String[] pathParts = model.getUrl().getPath().split("/");
        String archivePath = "";
        for (int endIndex = pathParts.length - 2; endIndex >= 0; endIndex--) {
            if (FileUtils.isArchive(pathParts[endIndex])) {
                archivePath = StringUtils.join(pathParts, "/", 0, endIndex + 1);
                break;
            }
        }
        return archivePath;
    }
}
