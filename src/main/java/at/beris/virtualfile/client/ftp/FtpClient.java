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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPCmd;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.parser.MLSxEntryParser;
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
        ftpClient = new FTPClient();
    }

    @Override
    public void connect() throws IOException {
        ftpClient.connect(host(), port());
        if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
            disconnect();
            return;
        }

        ftpClient.login(username(), String.valueOf(password()));
    }

    @Override
    public void disconnect() throws IOException {
        if (ftpClient.isConnected()) {
            ftpClient.logout();
            ftpClient.disconnect();
        }
    }

    @Override
    public void deleteFile(String path) throws IOException {
        checkConnection();
        ftpClient.deleteFile(path);
    }

    @Override
    public void createFile(String path) throws IOException {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{})) {
            checkConnection();
            ftpClient.storeFile(path, inputStream);
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public boolean exists(String path) throws IOException {
        checkConnection();
        String status = ftpClient.getStatus(path);
        return status != null;
    }

    @Override
    public void createDirectory(String path) throws IOException {
        checkConnection();
        ftpClient.makeDirectory(path);
    }

    @Override
    public void deleteDirectory(String path) throws IOException {
        checkConnection();
        ftpClient.removeDirectory(path);
    }

    @Override
    public InputStream getInputStream(String path) throws IOException {
        checkConnection();
        return ftpClient.retrieveFileStream(path);
    }

    @Override
    public OutputStream getOutputStream(String path) throws IOException {
        checkConnection();
        return ftpClient.storeFileStream(path);
    }

    @Override
    public FileInfo getFileInfo(String path) throws IOException {
        checkConnection();
        return new FtpFileInfo(path, mlistFile(path));
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
        List<FileInfo> fileList = new ArrayList<>();
        checkConnection();
        ftpClient.changeWorkingDirectory(path);
        for (FTPFile ftpFile : ftpClient.listFiles()) {
            FtpFileInfo ftpFileInfo = new FtpFileInfo(ftpClient.printWorkingDirectory() + ftpFile.getName(), ftpFile);
            fileList.add(ftpFileInfo);
        }

        return fileList;
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
    public void dispose() throws IOException {
        disconnect();
        ftpClient = null;
    }

    public boolean completePendingCommand() throws IOException {
        return ftpClient.completePendingCommand();
    }

    private void checkConnection() throws IOException {
        if (!ftpClient.isConnected())
            connect();
    }

    private FTPFile mlistFile(String pathname) throws IOException {
        boolean success = FTPReply.isPositiveCompletion(ftpClient.sendCommand(FTPCmd.MLST, pathname));
        if (success) {
            String entry = StringUtils.trim(ftpClient.getReplyStrings()[1]);
            return MLSxEntryParser.parseEntry(entry);
        } else {
            return null;
        }
    }
}
