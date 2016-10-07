/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileContext;
import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.client.ftp.FtpClient;
import at.beris.virtualfile.client.ftp.FtpFileTranslator;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FtpClientFileOperationProvider extends AbstractFileOperationProvider<FtpClient> {

    public FtpClientFileOperationProvider(FileContext fileContext, FtpClient client) {
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
        String tempFilePath = tempDir + File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        VirtualFile tempFile = copyToLocalFile(model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<VirtualFile> list(FileModel model, Filter filter) throws IOException {
        List<FTPFile> ftpFileList = client.list(resolveUrl(model).getPath());
        List<VirtualFile> fileList = new ArrayList<>();

        String parentPath = model.getUrl().getPath();
        for (FTPFile ftpFile : ftpFileList) {
            FileModel childModel = new FileModel();
            childModel.setParent(model);
            String childPath = parentPath + ftpFile.getName() + (ftpFile.isDirectory() ? "/" : "");
            VirtualFile childFile = fileContext.newFile(UrlUtils.newUrl(model.getUrl(), childPath));
            FtpFileTranslator.fillModel(childModel, ftpFile, client);
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

        FTPFile ftpFile = client.getFileInfo(model.getUrl().getPath());
        FtpFileTranslator.fillModel(model, ftpFile, client);
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

    private VirtualFile copyToLocalFile(FileModel model, String path) throws IOException {
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

    private URL resolveUrl(FileModel model) {
        if (model.isSymbolicLink())
            return model.getLinkTarget();
        else
            return model.getUrl();
    }
}
