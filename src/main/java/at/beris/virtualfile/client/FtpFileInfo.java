/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import org.apache.commons.net.ftp.FTPFile;

import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;

public class FtpFileInfo implements FileInfo {
    private FTPFile ftpFile;
    private String path;

    public FtpFileInfo(String path, FTPFile ftpFile) {
        this.ftpFile = ftpFile;
        this.path = path;
    }

    public void setFtpFile(FTPFile ftpFile) {
        this.ftpFile = ftpFile;
    }

    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void fillModel(FileModel model) {
        model.setFileExists(true);
        model.setSize(ftpFile.getSize());
        model.setCreationTime(null);
        model.setLastModifiedTime(FileTime.fromMillis(ftpFile.getTimestamp().getTime().getTime()));
        model.setLastAccessTime(null);
        model.setAttributes(createAttributes());
        model.setOwner(new UnixUserPrincipal(ftpFile.getName(), ftpFile.getGroup()));
        model.setGroup(new UnixGroupPrincipal(ftpFile.getGroup()));
        model.setDirectory(ftpFile.isDirectory());
        model.setSymbolicLink(ftpFile.isSymbolicLink());
    }

    private Set<FileAttribute> createAttributes() {
        Set<FileAttribute> attributeSet = new HashSet<>();

        for (int accessType : new int[]{FTPFile.USER_ACCESS, FTPFile.GROUP_ACCESS, FTPFile.WORLD_ACCESS}) {
            for (int permissionType : new int[]{FTPFile.READ_PERMISSION, FTPFile.WRITE_PERMISSION, FTPFile.EXECUTE_PERMISSION}) {
                if (ftpFile.hasPermission(accessType, permissionType)) {
                    FileAttribute fileAttribute = calculateAttribute(accessType, permissionType);
                    if (fileAttribute != null)
                        attributeSet.add(fileAttribute);
                }
            }
        }

        return attributeSet;
    }

    private FileAttribute calculateAttribute(int accessType, int permissionType) {
        if (accessType == FTPFile.USER_ACCESS) {
            if (permissionType == FTPFile.READ_PERMISSION)
                return PosixFilePermission.OWNER_READ;
            else if (permissionType == FTPFile.WRITE_PERMISSION)
                return PosixFilePermission.OWNER_WRITE;
            else if (permissionType == FTPFile.EXECUTE_PERMISSION)
                return PosixFilePermission.OWNER_EXECUTE;
        } else if (accessType == FTPFile.GROUP_ACCESS) {
            if (permissionType == FTPFile.READ_PERMISSION)
                return PosixFilePermission.GROUP_READ;
            else if (permissionType == FTPFile.WRITE_PERMISSION)
                return PosixFilePermission.GROUP_WRITE;
            else if (permissionType == FTPFile.EXECUTE_PERMISSION)
                return PosixFilePermission.GROUP_EXECUTE;
        } else if (accessType == FTPFile.WORLD_ACCESS) {
            if (permissionType == FTPFile.READ_PERMISSION)
                return PosixFilePermission.OTHERS_READ;
            else if (permissionType == FTPFile.WRITE_PERMISSION)
                return PosixFilePermission.OTHERS_WRITE;
            else if (permissionType == FTPFile.EXECUTE_PERMISSION)
                return PosixFilePermission.OTHERS_EXECUTE;
        }
        return null;
    }
}
