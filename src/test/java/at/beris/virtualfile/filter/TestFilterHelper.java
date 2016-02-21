/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.attribute.IAttribute;
import at.beris.virtualfile.attribute.PosixFilePermission;
import at.beris.virtualfile.exception.PermissionDeniedException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TestFilterHelper {
    private final static Logger LOGGER = LoggerFactory.getLogger(File.class);

    public static List<IFile> createFiles(String rootDir) throws Exception {
        List<IFile> fileList = new ArrayList<>();
        String dataString = "0123456789ABCDEF";

        List<FileData> fileDataList = new ArrayList<>();
        fileDataList.add(new FileData(rootDir, 0, new IAttribute[]{PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE}));
        fileDataList.add(new FileData(rootDir + "testfile1.txt", 640, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OTHERS_READ));
        fileDataList.add(new FileData(rootDir + "testfile2.txt", 800, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ));
        fileDataList.add(new FileData(rootDir + "subdir/", 0, new PosixFilePermission[]{PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE}));
        fileDataList.add(new FileData(rootDir + "subdir/goodmovie.avi", 3200, PosixFilePermission.OWNER_READ, PosixFilePermission.OWNER_WRITE, PosixFilePermission.OWNER_EXECUTE, PosixFilePermission.GROUP_READ, PosixFilePermission.GROUP_EXECUTE));

        for (FileData fileData : fileDataList) {
            IFile file = FileManager.newLocalFile(fileData.name);
            try {

                file.create();
                file.setAttributes(fileData.attributes);
                if (!file.isDirectory()) {
                    Files.write(new File(file.getUrl().toURI()).toPath(), StringUtils.repeat(dataString, fileData.size / dataString.length()).getBytes());
                }
                file.refresh();
                fileList.add(file);
            } catch (PermissionDeniedException e) {
                LOGGER.warn("Permission denied - " + file.toString());
            }
        }

        return fileList;
    }

    public static List<String> getNameListFromFileList(List<IFile> fileList) {
        List<String> nameList = new ArrayList<>();
        for (IFile file : fileList)
            nameList.add(file.getName());
        return nameList;
    }

    private static class FileData {
        public String name;
        public int size;
        public IAttribute[] attributes;

        public FileData(String name, int size, IAttribute... attributes) {
            this.name = name;
            this.size = size;
            this.attributes = attributes;
        }
    }
}
