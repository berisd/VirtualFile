/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.util.DisposableObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public interface Client<F, C> extends DisposableObject {
    void connect();

    void disconnect();

    void deleteFile(String path);

    void createFile(String path);

    boolean exists(String path);

    void createDirectory(String path);

    void deleteDirectory(String path);

    InputStream getInputStream(String path);

    OutputStream getOutputStream(String path);

    F getFileInfo(String path);

    List<F> list(String path);

    void setLastModifiedTime(String path, FileTime time);

    void setAttributes(String path, Set<FileAttribute> attributes);

    void setOwner(String path, UserPrincipal owner);

    void setGroup(String path, GroupPrincipal group);

    void dispose();

    C getConfiguration();

    String getCurrentDirectory();

}
