/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.ftp;

import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.AbstractClient;
import at.beris.virtualfile.client.FileInfo;
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.exception.OperationNotSupportedException;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FtpClient extends AbstractClient {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FtpClient.class);

    private FTPClient ftpClient;

    public FtpClient(URL url, Configuration config) {
        super(url, config);
        init();
    }

    private void init() {
        LOGGER.debug("init");
        ftpClient = new FTPClient();
    }

    @Override
    public void connect() throws IOException {
        LOGGER.info("Connecting to " + username() + "@" + host() + ":" + String.valueOf(port()));
        ftpClient.connect(host(), port());
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            disconnect();
            return;
        }

        ftpClient.login(username(), String.valueOf(password()));
    }

    @Override
    public void disconnect() throws IOException {
        LOGGER.info("Disconnecting from " + username() + "@" + host() + ":" + String.valueOf(port()));
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    @Override
    public void deleteFile(String path) throws IOException {
        LOGGER.debug("deleteFile (path : {})", path);
        checkConnection();
        ftpClient.deleteFile(path);
    }

    @Override
    public void createFile(String path) throws IOException {
        LOGGER.debug("createFile (path : {})", path);
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{})) {
            checkConnection();
            ftpClient.storeFile(path, inputStream);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean exists(String path) throws IOException {
        LOGGER.debug("exists (path : {})", path);
        checkConnection();
        String status = ftpClient.getStatus(path);
        return status != null;
    }

    @Override
    public void createDirectory(String path) throws IOException {
        LOGGER.debug("createDirectory (path : {})", path);
        checkConnection();
        ftpClient.makeDirectory(path);
    }

    @Override
    public void deleteDirectory(String path) throws IOException {
        LOGGER.debug("deleteDirectory (path : {})", path);
        checkConnection();
        ftpClient.removeDirectory(path);
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        LOGGER.debug("getInputStream (path : {})", path);
        checkConnection();
        return new FtpInputStream(ftpClient.retrieveFileStream(path), ftpClient);
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        LOGGER.debug("getOutputStream (path : {})", path);
        checkConnection();
        return new FtpOutputStream(ftpClient.storeFileStream(path), ftpClient);
    }

    @Override
    public FileInfo getFileInfo(String path) throws IOException {
        LOGGER.debug("getFileInfo (path: {})", path);
        checkConnection();
        if ("/".equals(path)) {
            return new FtpFileInfo(path, null);
        } else {
            String lastPathPart = UrlUtils.getLastPathPart(path);
            String parentPath = UrlUtils.getParentPath(path);
            ftpClient.changeWorkingDirectory(parentPath);
            FTPFile[] ftpFiles = ftpClient.listFiles();
            for (FTPFile ftpFile : ftpFiles) {
                if (ftpFile.getName().equals(lastPathPart))
                    return new FtpFileInfo(path + (ftpFile.isDirectory() ? "/" : ""), ftpFile);
            }
            return new FtpFileInfo(path, null);
        }
    }

    protected String username() {
        String username = super.username();
        return StringUtils.isBlank(username) ? "anonymous" : username;
    }

    @Override
    protected int defaultPort() {
        return 21;
    }

    public List<FileInfo> list(String path) throws IOException {
        LOGGER.debug("list (path: {})", path);
        List<FileInfo> fileList = new ArrayList<>();
        checkConnection();
        ftpClient.changeWorkingDirectory(path);
        for (FTPFile ftpFile : ftpClient.listFiles()) {
            FtpFileInfo ftpFileInfo = new FtpFileInfo(ftpClient.printWorkingDirectory() +
                    (!ftpClient.printWorkingDirectory().endsWith("/") ? "/" : "") +
                    ftpFile.getName() + (ftpFile.isDirectory() ? "/" : ""), ftpFile);
            fileList.add(ftpFileInfo);
        }

        return fileList;
    }

    @Override
    public void setLastModifiedTime(String path, FileTime time) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setAttributes(String path, Set<FileAttribute> attributes) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setOwner(String path, UserPrincipal owner) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void setGroup(String path, GroupPrincipal group) {
        throw new OperationNotSupportedException();
    }

    @Override
    public void dispose() throws IOException {
        disconnect();
        ftpClient = null;
    }

    private void checkConnection() throws IOException {
        if (!ftpClient.isConnected())
            connect();
    }
}
