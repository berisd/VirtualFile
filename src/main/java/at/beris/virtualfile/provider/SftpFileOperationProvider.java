/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.FileUtils;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.client.FileInfo;
import at.beris.virtualfile.client.IClient;
import org.apache.commons.lang3.NotImplementedException;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class SftpFileOperationProvider implements IFileOperationProvider {

    @Override
    public IFile create(IClient client, FileModel model) {
        if (model.isDirectory())
            client.createDirectory(model.getPath());
        else {
            client.createFile(model.getPath());
        }
        return null;
    }

    @Override
    public boolean exists(IClient client, FileModel model) {
        return client.exists(model.getPath());
    }

    @Override
    public void delete(IClient client, FileModel model) {
        if (model.isDirectory())
            client.deleteDirectory(model.getPath());
        else
            client.deleteFile(model.getPath());
    }

    @Override
    public void add(IFile parent, IFile child) {
        throw new NotImplementedException("");
    }

    @Override
    public byte[] checksum(IClient client, FileModel model) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        IFile tempFile = copyToLocalFile(client, model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<IFile> list(IClient client, FileModel model) {
        List<FileInfo> fileInfoList = client.list(model.getPath());
        List<IFile> fileList = new ArrayList<>();

        for (FileInfo fileInfo : fileInfoList) {
            fileList.add(FileManager.newFile(FileUtils.newUrl(model.getUrl(), fileInfo.getPath())));
        }
        return fileList;
    }

    @Override
    public void updateModel(IClient client, FileModel model) {
        FileInfo fileInfo = client.getFileInfo(model.getPath());
        model.setLastModified(fileInfo.getLastModified());
        model.setSize(fileInfo.getSize());
    }

    @Override
    public void save(URL url, FileModel model) {
        throw new NotImplementedException("");
    }

    @Override
    public InputStream getInputStream(IClient client, FileModel model) {
        return client.getInputStream(model.getPath());
    }

    @Override
    public OutputStream getOutputStream(IClient client, FileModel model) {
        return client.getOutputStream(model.getPath());
    }

    private IFile copyToLocalFile(IClient client, FileModel model, String path) {
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
            new RuntimeException(e);
        }

        return FileManager.newLocalFile(path);
    }
}
