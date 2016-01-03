/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.client.IClient;
import org.apache.commons.compress.archivers.ArchiveException;
import org.apache.commons.compress.archivers.ArchiveOutputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class LocalArchivedFileOperationProvider implements IFileOperationProvider {
    @Override
    public List<IFile> list(IClient client, FileModel model) {
        List<IFile> files = new ArrayList<>();
//        IFile backFile = add(this.archiveFile, createEmptyArchiveEntry());
//        backFile.setParent(this.parentFile);
//
//        files.add(backFile);
//        for (IFile childFile : children) {
//            files.add(childFile);
//        }

        return files;
    }


    @Override
    public IFile create(IClient client, FileModel model) {
        try {
            // if not exists create Archive
            // insert or update ArchiveEntry
            FileOutputStream fileOutputStream = new FileOutputStream(model.getPath());
            ArchiveStreamFactory archiveStreamFactory = new ArchiveStreamFactory();
            ArchiveOutputStream archiveOutputStream = archiveStreamFactory.createArchiveOutputStream(ArchiveStreamFactory.ZIP, fileOutputStream);
            archiveOutputStream.close();
            return null;

        } catch (ArchiveException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean exists(IClient client, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public void delete(IClient client, FileModel file) {
        throw new NotImplementedException("");
    }

    @Override
    public void add(IFile parent, IFile child) {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum(IClient client, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public void updateModel(IClient client, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public void save(URL url, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public InputStream getInputStream(IClient client, FileModel model) throws IOException {
        throw new NotImplementedException("");
    }

    @Override
    public OutputStream getOutputStream(IClient client, FileModel model) throws IOException {
        throw new NotImplementedException("");
    }
}
