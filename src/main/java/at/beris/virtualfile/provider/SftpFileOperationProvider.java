/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.*;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.client.FileInfo;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SftpFileOperationProvider implements FileOperationProvider {

    @Override
    public void create(Client client, FileModel model) {
        if (model.isDirectory())
            client.createDirectory(model.getPath());
        else {
            client.createFile(model.getPath());
        }
    }

    @Override
    public boolean exists(Client client, FileModel model) {
        return client.exists(model.getPath());
    }

    @Override
    public void delete(Client client, FileModel model) {
        if (model.isDirectory())
            client.deleteDirectory(model.getPath());
        else
            client.deleteFile(model.getPath());
    }

    @Override
    public void add(at.beris.virtualfile.File parent, at.beris.virtualfile.File child) {
        throw new NotImplementedException();
    }

    @Override
    public byte[] checksum(Client client, FileModel model) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + java.io.File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        at.beris.virtualfile.File tempFile = copyToLocalFile(client, model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<at.beris.virtualfile.File> list(Client client, FileModel model, Filter filter) {
        List<FileInfo> fileInfoList = client.list(model.getPath());
        List<at.beris.virtualfile.File> fileList = new ArrayList<>();

        for (FileInfo fileInfo : fileInfoList) {
            at.beris.virtualfile.File file = FileManager.newFile(FileUtils.newUrl(model.getUrl(), fileInfo.getPath()));
            if (filter == null || filter.filter(file)) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    @Override
    public void updateModel(Client client, FileModel model) {
        model.setFileExists(false);
        FileInfo fileInfo = client.getFileInfo(model.getPath());
        fileInfo.fillModel(model);
    }

    @Override
    public void setAcl(Client client, FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(Client client, FileModel model) {
        return client.getInputStream(model.getPath());
    }

    @Override
    public OutputStream getOutputStream(Client client, FileModel model) {
        return client.getOutputStream(model.getPath());
    }

    @Override
    public void setAttributes(Client client, FileModel model) {
        client.setAttributes(model.getPath(), model.getAttributes());
    }

    @Override
    public void setCreationTime(Client client, FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(Client client, FileModel model) {
        client.setGroup(model.getPath(), model.getGroup());
    }

    @Override
    public void setLastAccessTime(Client client, FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastModifiedTime(Client client, FileModel model) {
        client.setLastModifiedTime(model.getPath(), model.getLastModifiedTime());
    }

    @Override
    public void setOwner(Client client, FileModel model) {
        client.setOwner(model.getPath(), model.getOwner());
    }

    private at.beris.virtualfile.File copyToLocalFile(Client client, FileModel model, String path) {
        byte[] buffer = new byte[1024];
        int length;

        try (
                InputStream inputStream = client.getInputStream(model.getPath());
                OutputStream outputStream = new FileOutputStream(path)
        ) {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (FileNotFoundException e) {
            throw new at.beris.virtualfile.exception.FileNotFoundException(e);
        } catch (IOException e) {
            new VirtualFileException(e);
        }

        return FileManager.newLocalFile(path);
    }
}
