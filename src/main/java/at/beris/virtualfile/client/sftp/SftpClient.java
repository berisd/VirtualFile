/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.sftp;

import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.AbstractClient;
import at.beris.virtualfile.config.Configuration;
import at.beris.virtualfile.config.value.AuthenticationType;
import com.jcraft.jsch.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.AccessDeniedException;
import java.nio.file.NoSuchFileException;
import java.nio.file.attribute.FileTime;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Vector;

public class SftpClient extends AbstractClient<SftpFile> {
    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SftpClient.class);

    private JSch jsch;
    private Session session;
    private ChannelSftp sftpChannel;
    private boolean isInitialized = false;

    public SftpClient(URL url, Configuration config) throws IOException {
        super(url, config);
    }

    private void init() throws IOException {
        LOGGER.debug("init");
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
            isInitialized = true;
        } catch (JSchException e) {
            handleJSchException(e, null);
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
    public void connect() throws IOException {
        LOGGER.info("Connecting to " + username() + "@" + host() + ":" + String.valueOf(port()));
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
        LOGGER.info("Disconnecting from " + username() + "@" + host() + ":" + String.valueOf(port()));
        if (sftpChannel != null)
            sftpChannel.disconnect();
        if (session != null)
            session.disconnect();
    }

    @Override
    public void deleteFile(String path) throws IOException {
        LOGGER.debug("deleteFile (path : {})", path);
        try {
            checkChannel();
            sftpChannel.rm(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void createFile(String path) throws IOException {
        LOGGER.debug("createFile (path : {})", path);
        try {
            checkChannel();
            sftpChannel.put(new ByteArrayInputStream(new byte[]{}), path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public boolean exists(String path) throws IOException {
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
    public void createDirectory(String path) throws IOException {
        LOGGER.debug("createDirectory (path : {})", path);
        try {
            checkChannel();
            sftpChannel.mkdir(path);
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void deleteDirectory(String path) throws IOException {
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
    public InputStream getInputStream(String path) throws IOException {
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
    public OutputStream getOutputStream(String path) throws IOException {
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
    public SftpFile getFileInfo(String path) throws IOException {
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
    public List<SftpFile> list(String path) throws IOException {
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
    public void setLastModifiedTime(String path, FileTime time) throws IOException {
        LOGGER.debug("setLastModifiedTime (path : {}, time: {})", path, time);
        try {
            checkChannel();
            sftpChannel.setMtime(path, (int) (time.toMillis() / 1000));
        } catch (SftpException e) {
            handleSftpException(e);
        }
    }

    @Override
    public void setAttributes(String path, Set<FileAttribute> attributes) throws IOException {
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
    public void setOwner(String path, UserPrincipal owner) throws IOException {
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
    public void setGroup(String path, GroupPrincipal group) throws IOException {
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
    public void dispose() throws IOException {
        disconnect();
        sftpChannel = null;
        session = null;
        jsch = null;
    }

    private void checkChannel() throws IOException {
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

    private void handleSftpException(SftpException e) throws IOException {
        if (e.id == ChannelSftp.SSH_FX_PERMISSION_DENIED)
            throw new AccessDeniedException(e.getMessage());
        else if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE)
            throw new NoSuchFileException(e.getMessage());
        else
            throw new IOException(e);
    }

    private void handleJSchException(JSchException e, String path) throws IOException {
        if (e.getMessage().equals("Auth fail"))
            throw new AccessDeniedException(path, null, e.getMessage());
        else
            throw new IOException(e);
    }

    private boolean isDir(String path) throws SftpException {
        return sftpChannel.stat(path).isDir();
    }

    @Override
    protected int defaultPort() {
        return 22;
    }
}
