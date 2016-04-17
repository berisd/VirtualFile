/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.sftp;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.client.FileInfo;
import com.jcraft.jsch.SftpATTRS;

import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SftpFileInfo implements FileInfo<SftpATTRS> {

    private SftpATTRS sftpATTRS;
    private String path;

    public void setSftpATTRS(SftpATTRS sftpATTRS) {
        this.sftpATTRS = sftpATTRS;
    }

    public String getPath() {
        return this.path;
    }

    @Override
    public SftpATTRS getFile() {
        return sftpATTRS;
    }

    @Override
    public void fillModel(FileModel model) {
        model.setFileExists(true);
        model.setSize(sftpATTRS.getSize());
        model.setCreationTime(null);
        model.setLastModifiedTime(FileTime.fromMillis(sftpATTRS.getMTime() * 1000L));
        model.setLastAccessTime(FileTime.fromMillis(sftpATTRS.getATime() * 1000L));
        model.setAttributes(createAttributes());
        model.setOwner(new UnixUserPrincipal(sftpATTRS.getUId(), sftpATTRS.getGId()));
        model.setGroup(new UnixGroupPrincipal(sftpATTRS.getGId()));
        model.setDirectory(sftpATTRS.isDir());
        model.setSymbolicLink(sftpATTRS.isLink());
    }

    public void setPath(String path) {
        this.path = path;
    }

    private Set<FileAttribute> createAttributes() {
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
