/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.client.sftp.SftpFile;
import at.beris.virtualfile.client.sftp.SftpFileTranslator;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.UrlUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SftpClientFileOperationProvider extends AbstractFileOperationProvider<SftpClient> {
    public SftpClientFileOperationProvider(FileContext fileContext, SftpClient client) {
        super(fileContext, client);
    }

    @Override
    public void create(FileModel model) throws IOException {
        if (model.isDirectory())
            client.createDirectory(model.getUrl().getPath());
        else {
            client.createFile(model.getUrl().getPath());
        }
    }

    @Override
    public Boolean exists(FileModel model) throws IOException {
        return client.exists(model.getUrl().getPath());
    }

    @Override
    public void delete(FileModel model) throws IOException {
        if (model.isDirectory())
            client.deleteDirectory(model.getUrl().getPath());
        else
            client.deleteFile(model.getUrl().getPath());
    }

    @Override
    public Byte[] checksum(FileModel model) throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + java.io.File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        at.beris.virtualfile.File tempFile = copyToLocalFile(model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<at.beris.virtualfile.File> list(FileModel model, Filter filter) throws IOException {
        List<SftpFile> fileInfoList = client.list(model.getUrl().getPath());
        List<at.beris.virtualfile.File> fileList = new ArrayList<>();

        for (SftpFile sftpFile : fileInfoList) {
            at.beris.virtualfile.File childFile = fileContext.newFile(UrlUtils.newUrl(model.getUrl(), sftpFile.getPath()));
            FileModel childModel = new FileModel();
            SftpFileTranslator.fillModel(childModel, sftpFile);
            childFile.setModel(childModel);
            if (filter == null || filter.filter(childFile)) {
                fileList.add(childFile);
            }
        }
        return fileList;
    }

    @Override
    public void updateModel(FileModel model) throws IOException {
        model.setFileExists(client.exists(model.getUrl().getPath()));
        if (!model.isFileExists())
            return;

        SftpFile sftpFile = client.getFileInfo(model.getUrl().getPath());
        SftpFileTranslator.fillModel(model, sftpFile);
    }

    @Override
    public void setAcl(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(FileModel model) throws IOException {
        return client.getInputStream(model.getUrl().getPath());
    }

    @Override
    public OutputStream getOutputStream(FileModel model) throws IOException {
        return client.getOutputStream(model.getUrl().getPath());
    }

    @Override
    public void setAttributes(FileModel model) throws IOException {
        client.setAttributes(model.getUrl().getPath(), model.getAttributes());
    }

    @Override
    public void setCreationTime(FileModel model) throws IOException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(FileModel model) throws IOException {
        client.setGroup(model.getUrl().getPath(), model.getGroup());
    }

    @Override
    public void setLastAccessTime(FileModel model) throws IOException {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastModifiedTime(FileModel model) throws IOException {
        client.setLastModifiedTime(model.getUrl().getPath(), model.getLastModifiedTime());
    }

    @Override
    public void setOwner(FileModel model) throws IOException {
        client.setOwner(model.getUrl().getPath(), model.getOwner());
    }

    private at.beris.virtualfile.File copyToLocalFile(FileModel model, String path) throws IOException {
        byte[] buffer = new byte[1024];
        int length;

        try (
                InputStream inputStream = client.getInputStream(model.getUrl().getPath());
                OutputStream outputStream = new FileOutputStream(path)
        ) {
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            throw e;
        }

        return fileContext.newLocalFile(path);
    }
}
