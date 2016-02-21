/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.Attribute;
import at.beris.virtualfile.FileManager;
import at.beris.virtualfile.IFile;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class TestFilterHelper {

    public static List<IFile> createFiles(String rootDir) throws Exception {
        List<IFile> fileList = new ArrayList<>();
        String dataString = "0123456789ABCDEF";

        List<FileData> fileDataList = new ArrayList<>();
        fileDataList.add(new FileData(rootDir, 0, null));
        fileDataList.add(new FileData(rootDir + "testfile1.txt", 640, Attribute.OWNER_READ, Attribute.GROUP_READ, Attribute.OTHERS_READ));
        fileDataList.add(new FileData(rootDir + "testfile2.txt", 800, Attribute.OWNER_READ, Attribute.OWNER_EXECUTE, Attribute.GROUP_READ));
        fileDataList.add(new FileData(rootDir + "subdir/", 0, null));
        fileDataList.add(new FileData(rootDir + "subdir/goodmovie.avi", 3200, Attribute.OWNER_READ, Attribute.OWNER_EXECUTE, Attribute.GROUP_READ, Attribute.GROUP_EXECUTE));

        for (FileData fileData : fileDataList) {
            IFile file = FileManager.newLocalFile(fileData.name);
            file.create();
            if (!file.isDirectory()) {
                file.addAttributes(fileData.attributes);
                Files.write(new File(file.getUrl().toURI()).toPath(), StringUtils.repeat(dataString, fileData.size / dataString.length()).getBytes());
            }
            file.refresh();
            fileList.add(file);
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
        public Attribute[] attributes;

        public FileData(String name, int size, Attribute... attributes) {
            this.name = name;
            this.size = size;
            this.attributes = attributes;
        }
    }
}
