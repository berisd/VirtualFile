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
import at.beris.virtualfile.client.FileInfo;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SftpFileOperationProvider extends AbstractFileOperationProvider {
    protected RemoteSite site;


    public SftpFileOperationProvider(FileContext fileContext, Site site) {
        super(fileContext);
        this.site = (RemoteSite) site;
    }

    @Override
    public void create(FileModel model) {
        if (model.isDirectory())
            site.getClient().createDirectory(model.getPath());
        else {
            site.getClient().createFile(model.getPath());
        }
    }

    @Override
    public Boolean exists(FileModel model) {
        return site.getClient().exists(model.getPath());
    }

    @Override
    public void delete(FileModel model) {
        if (model.isDirectory())
            site.getClient().deleteDirectory(model.getPath());
        else
            site.getClient().deleteFile(model.getPath());
    }

    @Override
    public Byte[] checksum(FileModel model) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + java.io.File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        at.beris.virtualfile.File tempFile = copyToLocalFile(model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<at.beris.virtualfile.File> list(FileModel model, Filter filter) {
        List<FileInfo> fileInfoList = site.getClient().list(model.getPath());
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
    public void updateModel(FileModel model) {
        model.setFileExists(false);
        FileInfo fileInfo = site.getClient().getFileInfo(model.getPath());
        fileInfo.fillModel(model);
    }

    @Override
    public void setAcl(FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(FileModel model) {
        return site.getClient().getInputStream(model.getPath());
    }

    @Override
    public OutputStream getOutputStream(FileModel model) {
        return site.getClient().getOutputStream(model.getPath());
    }

    @Override
    public void setAttributes(FileModel model) {
        site.getClient().setAttributes(model.getPath(), model.getAttributes());
    }

    @Override
    public void setCreationTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(FileModel model) {
        site.getClient().setGroup(model.getPath(), model.getGroup());
    }

    @Override
    public void setLastAccessTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastModifiedTime(FileModel model) {
        site.getClient().setLastModifiedTime(model.getPath(), model.getLastModifiedTime());
    }

    @Override
    public void setOwner(FileModel model) {
        site.getClient().setOwner(model.getPath(), model.getOwner());
    }

    private at.beris.virtualfile.File copyToLocalFile(FileModel model, String path) {
        byte[] buffer = new byte[1024];
        int length;

        try (
                InputStream inputStream = site.getClient().getInputStream(model.getPath());
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
