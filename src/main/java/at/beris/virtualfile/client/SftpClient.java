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
import at.beris.virtualfile.config.ClientConfig;
import at.beris.virtualfile.exception.AccessDeniedException;
import at.beris.virtualfile.exception.AuthenticationException;
import at.beris.virtualfile.exception.FileNotFoundException;
import at.beris.virtualfile.exception.VirtualFileException;
import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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
    private ClientConfig config;
    private URL url;

    public SftpClient(URL url, ClientConfig config) {
        this.url = url;
        this.config = config;
    }

    @Override
    public void init() {
        java.util.Properties sessionConfig = new java.util.Properties();
        sessionConfig.put("StrictHostKeyChecking", config.isStrictHostKeyChecking() ? "yes" : "no");
        sessionConfig.put("PreferredAuthentications", config.getAuthenticationType().getJschConfigValue());

        jsch = new JSch();

        try {
            if (config.isStrictHostKeyChecking() && !StringUtils.isBlank(config.getKnownHostsFile()))
                jsch.setKnownHosts(config.getKnownHostsFile());

            if (config.getAuthenticationType() == AuthenticationType.PUBLIC_KEY && !StringUtils.isBlank(String.valueOf(config.getPrivateKeyFile())))
                jsch.addIdentity(String.valueOf(config.getPrivateKeyFile()));

            session = jsch.getSession(username(), host(), port());
            session.setConfig(sessionConfig);
            if (config.getAuthenticationType() == AuthenticationType.PASSWORD)
                session.setPassword(String.valueOf(password()));
            session.setTimeout(config.getTimeOut() * 1000);
        } catch (JSchException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    @Override
    public void setUrl(URL url) {
        this.url = url;
    }

    @Override
    public void connect() {
        LOGGER.info("Connect to " + username() + "@" + host() + ":" + String.valueOf(port()));
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
        LOGGER.info("Disconnect from " + username() + "@" + host() + ":" + String.valueOf(port()));
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

    private String username() {
        String usernameFromUrl = getUsernameFromUrl();
        if (usernameFromUrl != null)
            return usernameFromUrl;
        else if (config.getUsername() != null)
            return config.getUsername();
        else
            throw new AuthenticationException("Username not found.");
    }

    private char[] password() {
        String passwordFromUrl = getPasswordFromUrl();
        if (passwordFromUrl != null)
            return passwordFromUrl.toCharArray();
        else if (config.getPassword() != null)
            return config.getPassword();
        else
            throw new AuthenticationException("Password not found.");
    }

    private String host() {
        return url.getHost();
    }

    private int port() {
        int port = url.getPort();
        return port != -1 ? port : 22;
    }

    private String getUsernameFromUrl() {
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            String userInfoParts[] = url.getUserInfo().split(":");
            return userInfoParts[0];
        }
        return null;
    }

    private String getPasswordFromUrl() {
        String userInfo = url.getUserInfo();
        if (userInfo != null) {
            String userInfoParts[] = url.getUserInfo().split(":");
            if (userInfoParts.length > 1) {
                return userInfoParts[1];
            }
        }
        return null;
    }
}
