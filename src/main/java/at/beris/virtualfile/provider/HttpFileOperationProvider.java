/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UrlFile;
import at.beris.virtualfile.UrlFileContext;
import at.beris.virtualfile.client.http.HttpClient;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import at.beris.virtualfile.provider.operation.FileOperation;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.nio.file.attribute.FileTime;
import java.util.EnumSet;
import java.util.List;

public class HttpFileOperationProvider extends AbstractFileOperationProvider<HttpClient> {
    public HttpFileOperationProvider(UrlFileContext fileContext, HttpClient httpURLConnection) {
        super(fileContext, httpURLConnection);
        this.supportedOperations = EnumSet.of(FileOperation.GET_INPUT_STREAM, FileOperation.GET_OUTPUT_STREAM);
    }

    @Override
    public Byte[] checksum(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void create(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void delete(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public Boolean exists(FileModel model) {
        try {
            return model.getUrl().openConnection().getContentLengthLong() != -1;
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public InputStream getInputStream(FileModel model) {
        try {
            return model.getUrl().openStream();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public OutputStream getOutputStream(FileModel model) {
        try {
            return model.getUrl().openConnection().getOutputStream();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public List<UrlFile> list(FileModel model, Filter filter) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void updateModel(FileModel model) {
        try {
            URLConnection urlConnection = model.getUrl().openConnection();
            model.setSize(urlConnection.getContentLengthLong());
            model.setLastModifiedTime(FileTime.fromMillis(urlConnection.getLastModified()));
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void setAcl(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setAttributes(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setCreationTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastAccessTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setLastModifiedTime(FileModel model) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setOwner(FileModel model) {
        throw new OperationNotSupportedException();
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
}
