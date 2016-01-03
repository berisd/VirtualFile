/*
 * This file is part of JarCommander.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.exception.AccessDeniedException;
import at.beris.virtualfile.exception.FileNotFoundException;
import com.jcraft.jsch.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class SftpClient implements IClient {
    private JSch jsch;
    private Session session;
    private ChannelSftp sftpChannel;
    private String username;
    private String password;
    private String host;
    private int port;

    @Override
    public void init() {
        java.util.Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", "no");

        jsch = new JSch();

        try {
            session = jsch.getSession(username, host, port);
            session.setConfig(config);
            session.setPassword(password);
        } catch (JSchException e) {
            new RuntimeException(e);
        }
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void connect() {
        try {
            session.connect();
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void disconnect() {
        if (sftpChannel != null)
            sftpChannel.disconnect();
        if (session != null)
            session.disconnect();
    }

    @Override
    public void deleteFile(String path) {
        try {
            checkChannel();
            sftpChannel.rm(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void createFile(String path) {
        try {
            checkChannel();
            sftpChannel.put(new ByteArrayInputStream(new byte[]{}), path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public boolean exists(String path) {
        try {
            checkChannel();
            sftpChannel.stat(path);
        } catch (SftpException e) {
            if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
                return false;
            else
                handleSftpException(e);
        }
        return true;
    }

    @Override
    public void createDirectory(String path) {
        try {
            checkChannel();
            sftpChannel.mkdir(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void deleteDirectory(String path) {
        try {
            checkChannel();
            if (isDir(path)) {
                sftpChannel.cd(path);
                Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls(".");
                for (ChannelSftp.LsEntry entry : entries) {
                    if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                        continue;
                    deleteDirectory(path + entry.getFilename() + (isDir(path + entry.getFilename()) ? "/" : ""));
                }
                sftpChannel.cd("..");
                sftpChannel.rmdir(path);
            } else {
                sftpChannel.rm(path);
            }

        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public InputStream getInputStream(String path) {
        InputStream inputStream = null;
        try {
            checkChannel();
            inputStream = sftpChannel.get(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
        return inputStream;
    }

    @Override
    public OutputStream getOutputStream(String path) {
        OutputStream outputStream = null;
        try {
            checkChannel();
            outputStream = sftpChannel.put(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
        return outputStream;
    }

    @Override
    public FileInfo getFileInfo(String path) {
        FileInfo fileInfo = new FileInfo();
        try {
            checkChannel();
            SftpATTRS sftpATTRS = sftpChannel.stat(path);
            fileInfo.setPath(path + (sftpATTRS.isDir() && !path.endsWith("/") ? "/" : ""));
            fileInfo.setSize(sftpATTRS.getSize());
            fileInfo.setLastModified(new Date(sftpATTRS.getMTime() * 1000L));
        } catch (SftpException e) {
            handleSftpException(e);
        }
        return fileInfo;
    }

    @Override
    public List<FileInfo> list(String path) {
        List<FileInfo> fileInfoList = new ArrayList<>();

        try {
            checkChannel();
            Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls(path);
            for (ChannelSftp.LsEntry entry : entries) {
                if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                    continue;
                fileInfoList.add(getFileInfo(path + entry.getFilename()));
            }
        } catch (SftpException e) {
            handleSftpException(e);
        }

        return fileInfoList;
    }

    private void checkChannel() {
        try {
            if (session == null)
                init();
            if (!session.isConnected())
                connect();
            if (sftpChannel.isClosed() || !sftpChannel.isConnected())
                sftpChannel.connect();
        } catch (JSchException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleSftpException(SftpException e) {
        if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED)
            throw new AccessDeniedException(e);
        else if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
            throw new FileNotFoundException(e);
        else
            throw new RuntimeException(e);
    }

    private boolean isDir(String path) throws SftpException {
        return sftpChannel.stat(path).isDir();
    }
}
