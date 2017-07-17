/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.http;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.Client;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.List;
import java.util.Set;

public class HttpClient implements Client<HttpFile, HttpClientConfiguration> {

    public HttpClient(HttpClientConfiguration configuration) {
    }

    @Override
    public void connect() {

    }

    @Override
    public void disconnect() {

    }

    @Override
    public void deleteFile(String path) {

    }

    @Override
    public void createFile(String path) {

    }

    @Override
    public boolean exists(String path) {
        return false;
    }

    @Override
    public void createDirectory(String path) {

    }

    @Override
    public void deleteDirectory(String path) {

    }

    @Override
    public InputStream getInputStream(String path) {
        return null;
    }

    @Override
    public OutputStream getOutputStream(String path) {
        return null;
    }

    @Override
    public HttpFile getFileInfo(String path) {
        return null;
    }

    @Override
    public List<HttpFile> list(String path) {
        return null;
    }

    @Override
    public void setLastModifiedTime(String path, FileTime time) {

    }

    @Override
    public void setAttributes(String path, Set<FileAttribute> attributes) {

    }

    @Override
    public void setOwner(String path, UserPrincipal owner) {

    }

    @Override
    public void setGroup(String path, GroupPrincipal group) {

    }

    @Override
    public void dispose() {

    }

    @Override
    public HttpClientConfiguration getConfiguration() {
        return null;
    }

    @Override
    public String getCurrentDirectory() {
        return "/";
    }
}
