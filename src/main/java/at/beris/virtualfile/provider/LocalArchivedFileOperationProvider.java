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
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.FileUtils;
import org.apache.commons.compress.archivers.*;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.List;

public class LocalArchivedFileOperationProvider extends AbstractFileOperationProvider {

    public LocalArchivedFileOperationProvider(FileContext fileContext, Client client) {
        super(fileContext, client);
    }

    @Override
    public List<File> list(FileModel model, Filter filter) {
        List<File> files = new ArrayList<>();
//        File backFile = add(this.archiveFile, createEmptyArchiveEntry());
//        backFile.setParent(this.parentFile);
//
//        files.add(backFile);
//        for (File childFile : children) {
//            files.add(childFile);
//        }

        return files;
    }


    @Override
    public void create(FileModel model) {
        try {
            // if not exists create UrlArchive
            // insert or update ArchiveEntry
            FileOutputStream fileOutputStream = new FileOutputStream(model.getPath());
            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            ArchiveOutputStream archiveOutputStream = archiveStreamFactory.createArchiveOutputStream(ArchiveStreamFactory.ZIP, fileOutputStream);
            archiveOutputStream.close();

        } catch (ArchiveException e) {
            throw new VirtualFileException(e);
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public Boolean exists(FileModel model) {
        String archivePath = getArchivePath(model);
        String targetArchiveEntryPath = model.getPath().substring(archivePath.length() + 1);

        ArchiveInputStream ais = null;
        InputStream fis = null;

        try {
            ArchiveStreamFactory factory = new ArchiveStreamFactory();
            fis = new BufferedInputStream(new FileInputStream(new java.io.File(archivePath)));
            ais = factory.createArchiveInputStream(fis);
            ArchiveEntry archiveEntry;

            while ((archiveEntry = ais.getNextEntry()) != null) {
                String archiveEntryPath = archiveEntry.getName();

                if (archiveEntryPath.equals(targetArchiveEntryPath)) {
                    model.setSize(archiveEntry.getSize());
                    model.setLastModifiedTime(FileTime.fromMillis(archiveEntry.getLastModifiedDate().getTime()));

                    if (model.getUrl().toString().endsWith("/") && (!archiveEntry.isDirectory())) {
                        String urlString = model.getUrl().toString();
                        model.setUrl(FileUtils.newUrl(urlString.substring(0, urlString.length() - 1)));
                    } else if (!model.getUrl().toString().endsWith("/") && (archiveEntry.isDirectory())) {
                        String urlString = model.getUrl().toString() + "/";
                        model.setUrl(FileUtils.newUrl(urlString));
                    }
                    break;
                }
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

    private String getArchivePath(FileModel model) {
        String[] pathParts = model.getPath().split("/");
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
