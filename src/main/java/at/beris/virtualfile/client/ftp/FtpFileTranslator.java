/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.ftp;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.UnixGroupPrincipal;
import at.beris.virtualfile.UnixUserPrincipal;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.net.ftp.FTPFile;

import java.io.IOException;
import java.net.URL;
import java.nio.file.attribute.FileTime;
import java.util.HashSet;
import java.util.Set;

public final class FtpFileTranslator {

    public static void fillModel(FileModel model, FTPFile ftpFile, FtpClient client) throws IOException {
        String physicalRootPath = client.getPhysicalRootPath();
        String parentPath = model.getParent() != null ? model.getParent().getUrl().getPath() : "";

        if (!"".equals(physicalRootPath)) {
            if (parentPath.length() >= physicalRootPath.length()
                    && parentPath.substring(0, physicalRootPath.length()).equals(physicalRootPath))
                parentPath = "/" + parentPath.substring(physicalRootPath.length());
        }

        if (ftpFile.isSymbolicLink()) {
            String linkPath = ftpFile.getLink() + (ftpFile.getLink().endsWith("/") ? "" : "/");

            if (!"".equals(physicalRootPath)) {
                if (linkPath.length() >= physicalRootPath.length()
                        && linkPath.substring(0, physicalRootPath.length()).equals(physicalRootPath))
                    linkPath = "/" + linkPath.substring(physicalRootPath.length());
            }
            String filePath = parentPath + linkPath;
            URL linkTargetUrl = UrlUtils.normalizeUrl(UrlUtils.newUrlReplacePath(model.getParent().getUrl(), filePath));
            model.setLinkTarget(linkTargetUrl);
        }

        model.setFileExists(true);
        model.setSize(ftpFile.getSize());
        model.setCreationTime(null);
        model.setLastModifiedTime(FileTime.fromMillis(ftpFile.getTimestamp().getTime().getTime()));
        model.setLastAccessTime(null);
        model.setAttributes(createAttributes(ftpFile));
        model.setOwner(new UnixUserPrincipal(ftpFile.getUser(), ftpFile.getGroup()));
        model.setGroup(new UnixGroupPrincipal(ftpFile.getGroup()));
        model.setDirectory(ftpFile.isDirectory());
        model.setSymbolicLink(ftpFile.isSymbolicLink());
    }

    private static Set<FileAttribute> createAttributes(FTPFile ftpFile) {
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

    private static FileAttribute calculateAttribute(int accessType, int permissionType) {
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
