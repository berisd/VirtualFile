/*
 * This file is part of VirtualFile.
 *
 * Copyright 2015 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.config.AuthenticationType;
import at.beris.virtualfile.config.FileConfig;
import at.beris.virtualfile.exception.AccessDeniedException;
import at.beris.virtualfile.exception.FileNotFoundException;
import at.beris.virtualfile.exception.VirtualFileException;
import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SftpClient implements Client {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpClient.class);

    private JSch jsch;
    private Session session;
    private ChannelSftp sftpChannel;
    private String username;
    private String password;
    private String host;
    private int port;
    private FileConfig config;

    public SftpClient(FileConfig config) {
        this.config = config;
    }

    @Override
    public void init() {
        java.util.Properties sessionConfig = new java.util.Properties();
        sessionConfig.put("StrictHostKeyChecking", config.isClientStrictHostKeyChecking() ? "yes" : "no");
        sessionConfig.put("PreferredAuthentications", config.getClientAuthenticationType().getJschConfigValue());

        jsch = new JSch();

        try {
            if (config.isClientStrictHostKeyChecking() && !StringUtils.isBlank(config.getKnownHostsFile()))
                jsch.setKnownHosts(config.getKnownHostsFile());

            if (config.getClientAuthenticationType() == AuthenticationType.PUBLIC_KEY && !StringUtils.isBlank(config.getPrivateKeyFile()))
                jsch.addIdentity(config.getPrivateKeyFile());

            session = jsch.getSession(username, host, port);
            session.setConfig(sessionConfig);
            if (config.getClientAuthenticationType() == AuthenticationType.PASSWORD)
                session.setPassword(password);
            session.setTimeout(config.getClientTimeOut() * 1000);
        } catch (JSchException e) {
            throw new VirtualFileException(e);
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
        LOGGER.info("Connect to " + username + "@" + host + ":" + String.valueOf(port));
        try {
            session.connect();
            HostKey hostkey = session.getHostKey();
            LOGGER.info("HostKey: " + hostkey.getHost() + " " + hostkey.getType() + " " + hostkey.getFingerPrint(jsch));
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
        } catch (JSchException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void disconnect() {
        LOGGER.info("Disconnect from " + username + "@" + host + ":" + String.valueOf(port));
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
        SftpFileInfo fileInfo = new SftpFileInfo();
        try {
            checkChannel();
            SftpATTRS sftpATTRS = sftpChannel.stat(path);
            fileInfo.setPath(path + (sftpATTRS.isDir() && !path.endsWith("/") ? "/" : ""));
            fileInfo.setSftpATTRS(sftpATTRS);
        } catch (SftpException e) {
            handleSftpException(e);
        }
        return fileInfo;
    }

    @Override
    public List<FileInfo> list(String path) {
        List<FileInfo> IFileInfoList = new ArrayList<>();

        try {
            checkChannel();
            Vector<ChannelSftp.LsEntry> entries = sftpChannel.ls(path);
            for (ChannelSftp.LsEntry entry : entries) {
                if (entry.getFilename().equals(".") || entry.getFilename().equals(".."))
                    continue;
                IFileInfoList.add(getFileInfo(path + entry.getFilename()));
            }
        } catch (SftpException e) {
            handleSftpException(e);
        }

        return IFileInfoList;
    }

    @Override
    public void setLastModifiedTime(String path, FileTime time) {
        try {
            checkChannel();
            sftpChannel.setMtime(path, (int) (time.toMillis() / 1000));
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void setAttributes(String path, Set<FileAttribute> attributes) {
        try {
            checkChannel();
            SftpATTRS sftpATTRS = sftpChannel.stat(path);

            int permissions = 0;
            for (FileAttribute attribute : attributes) {
                permissions = permissions | Sftp.getPermission(attribute);
            }

            sftpATTRS.setPERMISSIONS(permissions);
            sftpChannel.setStat(path, sftpATTRS);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void setOwner(String path, UserPrincipal owner) {
        try {
            UnixUserPrincipal user = (UnixUserPrincipal) owner;
            checkChannel();
            SftpATTRS sftpATTRS = sftpChannel.stat(path);
            sftpATTRS.setUIDGID(user.getUid(), sftpATTRS.getGId());
            sftpChannel.setStat(path, sftpATTRS);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void setGroup(String path, GroupPrincipal group) {
        try {
            UnixGroupPrincipal unixGroup = (UnixGroupPrincipal) group;
            checkChannel();
            SftpATTRS sftpATTRS = sftpChannel.stat(path);
            sftpATTRS.setUIDGID(sftpATTRS.getUId(), unixGroup.getGid());
            sftpChannel.setStat(path, sftpATTRS);
        } catch (SftpException e) {
            handleSftpException(e);
        }
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
            throw new VirtualFileException(e);
        }
    }

    private void handleSftpException(SftpException e) {
        if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED)
            throw new AccessDeniedException(e);
        else if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
            throw new FileNotFoundException(e);
        else
            throw new VirtualFileException(e);
    }

    private boolean isDir(String path) throws SftpException {
        return sftpChannel.stat(path).isDir();
    }
}
