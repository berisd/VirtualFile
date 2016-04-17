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
import at.beris.virtualfile.exception.VirtualFileException;
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
    public void connect() {
        try {
            ftpClient.connect(host(), port());
            if (!FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                disconnect();
                return;
            }

            ftpClient.login(username(), String.valueOf(password()));
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void disconnect() {
        try {
            if (ftpClient.isConnected()) {
                ftpClient.logout();
                ftpClient.disconnect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteFile(String path) {
        try {
            checkConnection();
            ftpClient.deleteFile(path);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void createFile(String path) {
        try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{})) {
            checkConnection();
            ftpClient.storeFile(path, inputStream);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            checkConnection();
            String status = ftpClient.getStatus(path);
            return status != null;
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void createDirectory(String path) {
        try {
            checkConnection();
            ftpClient.makeDirectory(path);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void deleteDirectory(String path) {
        try {
            checkConnection();
            ftpClient.removeDirectory(path);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public InputStream getInputStream(String path) {
        try {
            checkConnection();
            return ftpClient.retrieveFileStream(path);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public OutputStream getOutputStream(String path) {
        try {
            checkConnection();
            return ftpClient.storeFileStream(path);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public FileInfo getFileInfo(String path) {
        try {
            checkConnection();
            return new FtpFileInfo(path, mlistFile(path));
        } catch (IOException e) {
            throw new VirtualFileException();
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

    public List<FileInfo> list(String path) {
        List<FileInfo> fileList = new ArrayList<>();
        try {
            checkConnection();
            ftpClient.changeWorkingDirectory(path);
            for (FTPFile ftpFile : ftpClient.listFiles()) {
                FtpFileInfo ftpFileInfo = new FtpFileInfo(ftpClient.printWorkingDirectory(), ftpFile);
                fileList.add(ftpFileInfo);
            }
        } catch (IOException e) {
            throw new VirtualFileException(e);
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

    public boolean completePendingCommand() {
        try {
            return ftpClient.completePendingCommand();
        } catch (IOException e) {
            throw new VirtualFileException();
        }
    }

    private void checkConnection() {
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
