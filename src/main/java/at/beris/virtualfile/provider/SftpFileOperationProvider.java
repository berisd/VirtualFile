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
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.client.IClient;
import at.beris.virtualfile.client.IFileInfo;
import at.beris.virtualfile.exception.NotImplementedException;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.IFilter;
import at.beris.virtualfile.util.FileUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SftpFileOperationProvider implements IFileOperationProvider {

    @Override
    public void create(IClient client, FileModel model) {
        if (model.isDirectory())
            client.createDirectory(model.getPath());
        else {
            client.createFile(model.getPath());
        }
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
        throw new NotImplementedException();
    }

    @Override
    public byte[] checksum(IClient client, FileModel model) {
        String tempDir = System.getProperty("java.io.tmpdir");
        String tempFilePath = tempDir + File.separator + "tmpfile_" + Thread.currentThread().getName() + "_" + System.currentTimeMillis();
        IFile tempFile = copyToLocalFile(client, model, tempFilePath);
        return tempFile.checksum();
    }

    @Override
    public List<IFile> list(IClient client, FileModel model, Optional<IFilter> filter) {
        List<IFileInfo> fileInfoList = client.list(model.getPath());
        List<IFile> fileList = new ArrayList<>();

        for (IFileInfo fileInfo : fileInfoList) {
            IFile file = FileManager.newFile(FileUtils.newUrl(model.getUrl(), fileInfo.getPath()));
            if (!filter.isPresent() || filter.get().filter(file)) {
                fileList.add(file);
            }
        }
        return fileList;
    }

    @Override
    public void updateModel(IClient client, FileModel model) {
        IFileInfo fileInfo = client.getFileInfo(model.getPath());
        model.setLastAccessTime(fileInfo.getLastAccessTime());
        model.setLastModifiedTime(fileInfo.getLastModifiedTime());
        model.setSize(fileInfo.getSize());
        model.setAttributes(fileInfo.getAttributes());
    }

    @Override
    public void setAcl(IClient client, FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public InputStream getInputStream(IClient client, FileModel model) {
        return client.getInputStream(model.getPath());
    }

    @Override
    public OutputStream getOutputStream(IClient client, FileModel model) {
        return client.getOutputStream(model.getPath());
    }

    @Override
    public Set<IAttribute> getAttributes(IClient client, FileModel model) {
        return client.getFileInfo(model.getPath()).getAttributes();
    }

    @Override
    public void setAttributes(IClient client, FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setCreationTime(IClient client, FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(IClient client, FileModel model) {
        throw new NotImplementedException();
    }

    @Override
    public void setLastAccessTime(IClient client, FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastModifiedTime(IClient client, FileModel model) {
        client.setLastModifiedTime(model.getPath(), model.getLastModifiedTime());
    }

    @Override
    public void setOwner(IClient client, FileModel model) {
        throw new NotImplementedException();
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
            new VirtualFileException(e);
        }

        return FileManager.newLocalFile(path);
    }
}
