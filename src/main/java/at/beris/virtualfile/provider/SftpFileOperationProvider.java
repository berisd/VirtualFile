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
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.UrlFileContext;
import at.beris.virtualfile.client.sftp.SftpClient;
import at.beris.virtualfile.client.sftp.SftpFile;
import at.beris.virtualfile.client.sftp.SftpFileTranslator;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.UrlUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class SftpFileOperationProvider extends AbstractFileOperationProvider<SftpClient> {

    private SftpFileTranslator sftpFileTranslator;

    public SftpFileOperationProvider(UrlFileContext fileContext, SftpClient client) {
        super(fileContext, client);
        sftpFileTranslator = new SftpFileTranslator();
    }

    @Override
    public void create(FileModel model) {
        if (model.isDirectory())
            client.createDirectory(model.getUrl().getPath());
        else {
            client.createFile(model.getUrl().getPath());
        }
    }

    @Override
    public Boolean exists(FileModel model) {
        return client.exists(model.getUrl().getPath());
    }

    @Override
    public void delete(FileModel model) {
        if (model.isDirectory())
            client.deleteDirectory(model.getUrl().getPath());
        else
            client.deleteFile(model.getUrl().getPath());
    }

    @Override
    public Byte[] checksum(FileModel model) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + java.io.File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        UrlFile tempFile = copyToLocalFile(model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<UrlFile> list(FileModel model, Filter filter) {
        List<SftpFile> fileInfoList = client.list(model.getUrl().getPath());
        List<UrlFile> fileList = new ArrayList<>();

        for (SftpFile sftpFile : fileInfoList) {
            UrlFile childFile = fileContext.resolveFile(UrlUtils.newUrl(model.getUrl(), sftpFile.getPath()));
            FileModel childModel = new FileModel();
            sftpFileTranslator.fillModel(childModel, sftpFile, client);
            childFile.setModel(childModel);
            if (filter == null || filter.filter(childFile)) {
                fileList.add(childFile);
            }
        }
        return fileList;
    }

    @Override
    public void updateModel(FileModel model) {
        model.setFileExists(client.exists(model.getUrl().getPath()));
        if (!model.isFileExists())
            return;

        SftpFile sftpFile = client.getFileInfo(model.getUrl().getPath());
        sftpFileTranslator.fillModel(model, sftpFile, client);
    }

    @Override
    public void setAcl(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(FileModel model) {
        return client.getInputStream(model.getUrl().getPath());
    }

    @Override
    public OutputStream getOutputStream(FileModel model) {
        return client.getOutputStream(model.getUrl().getPath());
    }

    @Override
    public void setAttributes(FileModel model) {
        client.setAttributes(model.getUrl().getPath(), model.getAttributes());
    }

    @Override
    public void setCreationTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(FileModel model) {
        client.setGroup(model.getUrl().getPath(), model.getGroup());
    }

    @Override
    public void setLastAccessTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastModifiedTime(FileModel model) {
        client.setLastModifiedTime(model.getUrl().getPath(), model.getLastModifiedTime());
    }

    @Override
    public void setOwner(FileModel model) {
        client.setOwner(model.getUrl().getPath(), model.getOwner());
    }

    @Override
    public void rename(FileModel model, String newName) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void move(FileModel model, UrlFile targetFile) {
        throw new OperationNotSupportedException();
    }

    @Override
    public boolean isReadable(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public boolean isWritable(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public boolean isExecutable(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public boolean isHidden(FileModel model) {
        throw new OperationNotSupportedException();
    }

    private UrlFile copyToLocalFile(FileModel model, String path) {
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
            throw new VirtualFileException(e);
        }

        return fileContext.resolveFile(UrlUtils.getUrlForLocalPath(path));
    }
}
