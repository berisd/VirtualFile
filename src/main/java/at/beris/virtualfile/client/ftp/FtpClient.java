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
import org.apache.commons.net.ftp.*;
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
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

public class FtpClient extends AbstractClient {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FtpClient.class);
    private final static int MAX_CONNECTION_ATTEMPTS = 3;
    private String physicalRootPath = "";

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
        login();
        setFileType(FTP.BINARY_FILE_TYPE);
    }

    @Override
    public void disconnect() throws IOException {
        LOGGER.info("Disconnecting from " + username() + "@" + host() + ":" + String.valueOf(port()));
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                if (ftpClient.isConnected()) {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
                return null;
            }
        });
    }

    @Override
    public void deleteFile(final String path) throws IOException {
        LOGGER.debug("deleteFile (path : {})", path);
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ftpClient.deleteFile(path);
                return null;
            }
        });
    }

    @Override
    public void createFile(final String path) throws IOException {
        LOGGER.debug("createFile (path : {})", path);
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try (ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[]{})) {
                    checkConnection();
                    ftpClient.storeFile(path, inputStream);
                } catch (IOException e) {
                    throw e;
                }
                return null;
            }
        });
    }

    @Override
    public boolean exists(final String path) throws IOException {
        LOGGER.debug("exists (path : {})", path);
        return executionHandler(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String status = ftpClient.getStatus(path);
                return status.split("\n").length > 2;
            }
        });
    }

    @Override
    public void createDirectory(final String path) throws IOException {
        LOGGER.debug("createDirectory (path : {})", path);
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ftpClient.makeDirectory(path);
                return null;
            }
        });
    }

    @Override
    public void deleteDirectory(final String path) throws IOException {
        LOGGER.debug("deleteDirectory (path : {})", path);
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ftpClient.removeDirectory(path);
                return null;
            }
        });
    }

    @Override
    public InputStream getInputStream(final String path) throws IOException {
        LOGGER.debug("getInputStream (path : {})", path);
        return executionHandler(new Callable<FtpInputStream>() {
            @Override
            public FtpInputStream call() throws Exception {
                return new FtpInputStream(ftpClient.retrieveFileStream(path), ftpClient);
            }
        });
    }

    @Override
    public OutputStream getOutputStream(final String path) throws IOException {
        LOGGER.debug("getOutputStream (path : {})", path);
        return executionHandler(new Callable<FtpOutputStream>() {
            @Override
            public FtpOutputStream call() throws Exception {
                return new FtpOutputStream(ftpClient.storeFileStream(path), ftpClient);
            }
        });
    }

    @Override
    public FileInfo getFileInfo(final String path) throws IOException {
        LOGGER.debug("getFileInfo (path: {})", path);
        return executionHandler(new Callable<FileInfo>() {
            @Override
            public FileInfo call() throws Exception {
                if ("/".equals(path)) {
                    if ("".equals(physicalRootPath)) {
                        initPhysicalRootPath();
                    }
                    return new FtpFileInfo(path, null);
                } else {
                    String lastPathPart = UrlUtils.getLastPathPart(path);
                    String parentPath = UrlUtils.getParentPath(path);
                    ftpClient.changeWorkingDirectory(parentPath);
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    for (FTPFile ftpFile : ftpFiles) {
                        if (ftpFile.getName().equals(lastPathPart)) {
                            if (ftpFile.isSymbolicLink()) {
                                String path = ftpFile.getLink() + (ftpFile.isDirectory() ? "/" : "");
                                if (path.substring(0, physicalRootPath.length()).equals(physicalRootPath))
                                    path = "/" + path.substring(physicalRootPath.length()) + "/";
                                return new FtpFileInfo(path, ftpFile);
                            } else
                                return new FtpFileInfo(path + (ftpFile.isDirectory() && !path.endsWith("/") ? "/" : ""), ftpFile);
                        }
                    }
                    return new FtpFileInfo(path, null);
                }
            }
        });
    }

    protected String username() {
        String username = super.username();
        return StringUtils.isBlank(username) ? "anonymous" : username;
    }

    @Override
    protected int defaultPort() {
        return 21;
    }

    public List<FileInfo> list(final String path) throws IOException {
        LOGGER.debug("list (path: {})", path);
        List<FileInfo> fileInfoList = executionHandler(new Callable<List<FileInfo>>() {
            @Override
            public List<FileInfo> call() throws Exception {
                List<FileInfo> fileList = new ArrayList<>();
                ftpClient.changeWorkingDirectory(path);
                for (FTPFile ftpFile : ftpClient.listFiles()) {
                    FtpFileInfo ftpFileInfo = new FtpFileInfo(ftpClient.printWorkingDirectory() +
                            (!ftpClient.printWorkingDirectory().endsWith("/") ? "/" : "") +
                            ftpFile.getName() + (ftpFile.isDirectory() ? "/" : ""), ftpFile);
                    fileList.add(ftpFileInfo);
                }
                return fileList;
            }
        });

        return fileInfoList != null ? fileInfoList : Collections.EMPTY_LIST;
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

    private void login() throws IOException {
        LOGGER.debug("login");
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ftpClient.login(username(), String.valueOf(password()));
                return null;
            }
        });
    }

    private void setFileType(final int fileType) throws IOException {
        LOGGER.debug("setFileType (fileType: {})", fileType);
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                ftpClient.setFileType(fileType);
                return null;
            }
        });
    }

    private void initPhysicalRootPath() throws IOException {
        physicalRootPath = executionHandler(new Callable<String>() {
            @Override
            public String call() throws Exception {
                ftpClient.changeWorkingDirectory("/");
                for (FTPFile ftpFile : ftpClient.listFiles()) {
                    if (ftpFile.isSymbolicLink() && ".".equals(ftpFile.getLink())) {
                        return "/" + ftpFile.getName() + "/";
                    }
                }
                return "";
            }
        });
    }

    private <T> T executionHandler(Callable<T> action) throws IOException {
        int connectionAttempts = MAX_CONNECTION_ATTEMPTS;
        while (connectionAttempts > 0) {
            try {
                checkConnection();
                return action.call();
            } catch (FTPConnectionClosedException e) {
                LOGGER.warn("Server closed connection. Reconnecting.");
                LOGGER.debug("Exception", e);
                connectionAttempts--;
                connect();
            } catch (IOException e) {
                LOGGER.debug("Exception", e);
                throw e;
            } catch (Exception e) {
                LOGGER.debug("Exception", e);
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
