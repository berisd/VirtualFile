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
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.*;
import java.util.concurrent.Callable;

public class FtpClient extends AbstractClient<FTPFile> {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(FtpClient.class);
    private final static int MAX_CONNECTION_ATTEMPTS = 3;
    private String physicalRootPath;
    private boolean reconnect;
    private FTPClient ftpClient;

    public FtpClient(URL url, Configuration config) {
        super(url, config);
        init();
    }

    private void init() {
        LOGGER.debug("init");
        ftpClient = new FTPClient();
        ftpClient.setControlEncoding("UTF-8");
    }

    @Override
    public void connect() throws IOException {
        LOGGER.info("Connecting to " + username() + "@" + host() + ":" + String.valueOf(port()));
        reconnect = true;
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
                reconnect = false;
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
                int replyCode = ftpClient.dele(path);
                String replyText = ftpClient.getReplyString();
                if (!FTPReply.isPositiveCompletion(replyCode))
                    LOGGER.warn("Unexpected Reply (Code: {}, Text: '{}'", replyCode, replyText);
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
        Boolean exists = executionHandler(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {

                int replyCode = ftpClient.stat(path);
                String replyText = ftpClient.getReplyString();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    // this replyText code is set when file doesn't exist on the server
                    if (FTPReply.FILE_ACTION_NOT_TAKEN == replyCode)
                        return false;
                    else {
                        LOGGER.warn("Unexpected Reply (Code: {}, Text: '{}'", replyCode, replyText);
                    }
                }

                String[] replyTextParts = replyText.split("\n");
                if (replyTextParts.length <= 2) {
                    if (ftpClient.changeWorkingDirectory(path))
                        ftpClient.changeToParentDirectory();
                    else
                        return false;
                }
                return true;
            }
        });
        LOGGER.debug("Returns: {}", exists);
        return exists;
    }

    @Override
    public void createDirectory(final String path) throws IOException {
        LOGGER.debug("createDirectory (path : {})", path);
        executionHandler(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                int replyCode = ftpClient.mkd(path);
                String replyText = ftpClient.getReplyString();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    // this replyText code is set when file already exists on the server
                    if (FTPReply.FILE_UNAVAILABLE == replyCode)
                        throw new FileAlreadyExistsException(path);
                    else
                        LOGGER.warn("Unexpected Reply (Code: {}, Text: '{}'", replyCode, replyText);
                }
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
                int replyCode = ftpClient.rmd(path);
                String replyText = ftpClient.getReplyString();
                if (!FTPReply.isPositiveCompletion(replyCode)) {
                    LOGGER.warn("Unexpected Reply (Code: {}, Text: '{}'", replyCode, replyText);
                }
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
    public FTPFile getFileInfo(final String path) throws IOException {
        LOGGER.debug("getFileInfo (path: {})", path);
        return executionHandler(new Callable<FTPFile>() {
            @Override
            public FTPFile call() throws Exception {
                if ("/".equals(path)) {
                    FTPFile rootFile = new FTPFile();
                    rootFile.setName("/");
                    rootFile.setTimestamp(GregorianCalendar.getInstance());
                    return rootFile;
                } else {
                    String lastPathPart = UrlUtils.getLastPathPart(path);
                    String parentPath = UrlUtils.getParentPath(path);
                    ftpClient.changeWorkingDirectory(parentPath);
                    FTPFile[] ftpFiles = ftpClient.listFiles();
                    for (FTPFile ftpFile : ftpFiles) {
                        if (ftpFile.getName().equals(lastPathPart)) {
                            return ftpFile;
                        }
                    }
                    return new FTPFile();
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

    public List<FTPFile> list(final String path) throws IOException {
        LOGGER.debug("list (path: {})", path);
        List<FTPFile> fileInfoList = executionHandler(new Callable<List<FTPFile>>() {
            @Override
            public List<FTPFile> call() throws Exception {
                int replyCode = ftpClient.cwd(path);
                String replyText = ftpClient.getReplyString();

                if (FTPReply.isPositiveCompletion(replyCode)) {
                    return Arrays.asList(ftpClient.listFiles());
                } else {
                    LOGGER.warn("Unexpected Reply (Code: {}, Text: '{}'", replyCode, replyText);
                }
                return Collections.emptyList();
            }
        });

        return fileInfoList;
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

    public String getPhysicalRootPath() throws IOException {
        if (physicalRootPath == null) {
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
        return physicalRootPath;
    }

    private <T> T executionHandler(Callable<T> action) throws IOException {
        int connectionAttempts = MAX_CONNECTION_ATTEMPTS;
        while (connectionAttempts > 0) {
            try {
                checkConnection();
                return action.call();
            } catch (FTPConnectionClosedException e) {
                LOGGER.debug("Exception", e);
                if (reconnect) {
                    LOGGER.warn("Server closed connection. Reconnecting.");
                    connectionAttempts--;
                    connect();
                }
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
