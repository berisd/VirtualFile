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

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public interface Client {
    void init();

    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);

    String getUsername();

    void setUsername(String username);

    char[] getPassword();

    void setPassword(char[] password);

    void connect();

    void disconnect();

    void deleteFile(String Path);

    void createFile(String path);

    boolean exists(String path);

    void createDirectory(String path);

    void deleteDirectory(String Path);

    InputStream getInputStream(String path);

    OutputStream getOutputStream(String path);

    FileInfo getFileInfo(String path);

    List<FileInfo> list(String path);

    void setLastModifiedTime(String path, FileTime time);

    void setAttributes(String path, Set<FileAttribute> attributes);

    void setOwner(String path, UserPrincipal owner);

    void setGroup(String path, GroupPrincipal group);
}
