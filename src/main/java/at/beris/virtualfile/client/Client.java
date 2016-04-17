/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.attribute.FileAttribute;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public interface Client {
    URL getUrl();

    void setUrl(URL url);

    void connect() throws IOException;

    void disconnect() throws IOException;

    void deleteFile(String path) throws IOException;

    void createFile(String path) throws IOException;

    boolean exists(String path) throws IOException;

    void createDirectory(String path) throws IOException;

    void deleteDirectory(String path) throws IOException;

    InputStream getInputStream(String path) throws IOException;

    OutputStream getOutputStream(String path) throws IOException;

    FileInfo getFileInfo(String path) throws IOException;

    List<FileInfo> list(String path) throws IOException;

    void setLastModifiedTime(String path, FileTime time) throws IOException;

    void setAttributes(String path, Set<FileAttribute> attributes) throws IOException;

    void setOwner(String path, UserPrincipal owner) throws IOException;

    void setGroup(String path, GroupPrincipal group) throws IOException;
}
