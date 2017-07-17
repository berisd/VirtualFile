/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.sftp;

import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.Client;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.util.StringUtils;
import com.jcraft.jsch.*;
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

public class SftpClient implements Client<SftpFile, SftpClientConfiguration> {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpClient.class);

    private JSch jsch;
    private Session session;
    private ChannelSftp sftpChannel;
    private boolean isInitialized = false;

    private SftpClientConfiguration configuration;

    public SftpClient(SftpClientConfiguration configuration) {
        this.configuration = configuration;
    }

    private void init() {
        LOGGER.debug("init");
        java.util.Properties sessionConfig = new java.util.Properties();
        sessionConfig.put("StrictHostKeyChecking", configuration.isStrictHostKeyChecking() ? "yes" : "no");
        sessionConfig.put("PreferredAuthentications", configuration.getAuthenticationType().getValue());

        jsch = new JSch();

        try {
            if (configuration.isStrictHostKeyChecking() && !StringUtils.isBlank(configuration.getKnownHostsFile()))
                jsch.setKnownHosts(configuration.getKnownHostsFile());

            if (configuration.getAuthenticationType() == AuthenticationType.PUBLIC_KEY && !StringUtils.isBlank(String.valueOf(configuration.getPrivateKeyFile())))
                jsch.addIdentity(configuration.getPrivateKeyFile());

            session = jsch.getSession(configuration.getUsername(), configuration.getHostname(), configuration.getPort());
            session.setConfig(sessionConfig);
            if (configuration.getAuthenticationType() == AuthenticationType.PASSWORD) {
//                session.setPassword(CharUtils.charArrayToByteArray(configuration.getPassword()));
                //TODO session.setPassword(byte[]) doesn'T seem to work
                //TODO not sure if CharUtils.charArrayToByteArray is working correct (contains additional zeros)
                session.setPassword(new String(configuration.getPassword()));
            }
            session.setTimeout(configuration.getTimeout() * 1000);
            isInitialized = true;
        } catch (JSchException e) {
            handleJSchException(e, null);
        }
    }

    @Override
    public void connect() {
        LOGGER.info("Connecting to " + configuration.getUsername() + "@" + configuration.getHostname() + ":" + String.valueOf(configuration.getPort()));
        try {
            if (!isInitialized)
                init();
            session.connect();
            HostKey hostkey = session.getHostKey();
            LOGGER.info("HostKey: " + hostkey.getHost() + " " + hostkey.getType() + " " + hostkey.getFingerPrint(jsch));
            sftpChannel = (ChannelSftp) session.openChannel("sftp");
            sftpChannel.connect();
        } catch (JSchException e) {
            handleJSchException(e, null);
        }
    }

    @Override
    public void disconnect() {
        LOGGER.info("Disconnecting from " + configuration.getUsername() + "@" + configuration.getHostname() + ":" + String.valueOf(configuration.getPort()));
        if (sftpChannel != null)
            sftpChannel.disconnect();
        if (session != null)
            session.disconnect();
    }

    @Override
    public void deleteFile(String path) {
        LOGGER.debug("deleteFile (path : {})", path);
        try {
            checkChannel();
            sftpChannel.rm(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void createFile(String path) {
        LOGGER.debug("createFile (path : {})", path);
        try {
            checkChannel();
            sftpChannel.put(new ByteArrayInputStream(new byte[]{}), path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public boolean exists(String path) {
        LOGGER.debug("exists (path : {})", path);
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
        LOGGER.debug("createDirectory (path : {})", path);
        try {
            checkChannel();
            sftpChannel.mkdir(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void deleteDirectory(String path) {
        LOGGER.debug("deleteDirectory (path : {})", path);
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
        LOGGER.debug("getInputStream (path : {})", path);
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
        LOGGER.debug("getOutputStream (path : {})", path);
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
    public SftpFile getFileInfo(String path) {
        LOGGER.debug("getFileInfo (path : {})", path);
        SftpFile fileInfo = new SftpFile();
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
    public List<SftpFile> list(String path) {
        LOGGER.debug("list (path : {})", path);
        List<SftpFile> IFileInfoList = new ArrayList<>();

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
        LOGGER.debug("setLastModifiedTime (path : {}, time: {})", path, time);
        try {
            checkChannel();
            sftpChannel.setMtime(path, (int) (time.toMillis() / 1000));
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void setAttributes(String path, Set<FileAttribute> attributes) {
        LOGGER.debug("setAttributes (path : {}, attributes: {})", path, attributes);
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
        LOGGER.debug("setOwner (path : {}, owner: {})", path, owner);
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
        LOGGER.debug("setGroup (path : {}, group: {})", path, group);
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

    @Override
    public void dispose() {
        disconnect();
        sftpChannel = null;
        session = null;
        jsch = null;
    }

    @Override
    public SftpClientConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public String getCurrentDirectory() {
        LOGGER.debug("Get current directory");
        String currentDirectory = StringUtils.EMPTY_STRING;
        try {
            checkChannel();
            currentDirectory = sftpChannel.pwd();

        } catch (SftpException e) {
            handleSftpException(e);
        }
        LOGGER.debug("Returns: '{}'", currentDirectory);
        return currentDirectory;
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
            handleJSchException(e, null);
        }
    }

    private void handleSftpException(SftpException e) {
        if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED)
            throw new VirtualFileException(Message.ACCESS_DENIED(), e);
        else if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
            throw new VirtualFileException(Message.FILE_NOT_FOUND(e.getMessage()), e);
    }

    private void handleJSchException(JSchException e, String path) {
        if (e.getMessage().equals("Auth fail"))
            throw new VirtualFileException(Message.ACCESS_DENIED(), e);
        throw new VirtualFileException(e);
    }

    private boolean isDir(String path) throws SftpException {
        return sftpChannel.stat(path).isDir();
    }

}
