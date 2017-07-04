/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.sftp;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.ClientFileTranslator;
import at.beris.virtualfile.client.ftp.FtpClient;
import com.jcraft.jsch.SftpATTRS;
import org.apache.commons.net.ftp.FTPFile;

import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SftpFileTranslator implements ClientFileTranslator<SftpClient, SftpFile> {

    public void fillModel(FileModel model, SftpFile fileInfo, SftpClient client) {
        SftpATTRS sftpATTRS = fileInfo.getFile();
        model.setFileExists(true);
        model.setSize(sftpATTRS.getSize());
        model.setCreationTime(null);
        model.setLastModifiedTime(FileTime.fromMillis(sftpATTRS.getMTime() * 1000L));
        model.setLastAccessTime(FileTime.fromMillis(sftpATTRS.getATime() * 1000L));
        model.setAttributes(createAttributes(sftpATTRS));
        model.setOwner(new UnixUserPrincipal(sftpATTRS.getUId(), sftpATTRS.getGId()));
        model.setGroup(new UnixGroupPrincipal(sftpATTRS.getGId()));
        model.setDirectory(sftpATTRS.isDir());
        model.setSymbolicLink(sftpATTRS.isLink());
    }

    private static Set<FileAttribute> createAttributes(SftpATTRS sftpATTRS) {
        Set<FileAttribute> attributeSet = new HashSet<>();
        int permissions = sftpATTRS.getPermissions();

        for (Map.Entry<Integer, FileAttribute> entry : Sftp.permissionToAttributeMap.entrySet()) {
            if ((permissions & entry.getKey()) != 0) {
                attributeSet.add(entry.getValue());
            }
        }

        return attributeSet;
    }
}
