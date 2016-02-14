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
    public static List<String> createFileNameList(String rootDir) {
        List<String> fileNameList = new ArrayList<>();
        fileNameList.add(rootDir);
        fileNameList.add(rootDir + "testfile1.txt");
        fileNameList.add(rootDir + "testfile2.txt");
        fileNameList.add(rootDir + "subdir/");
        fileNameList.add(rootDir + "subdir/goodmovie.avi");
        return fileNameList;
    }

    public static List<IFile> createFiles(List<String> fileNamesList) throws Exception {
        List<IFile> fileList = new ArrayList<>();
        String dataString = "0123456789ABCDEF";

        IFile file = FileManager.newLocalFile(fileNamesList.get(0));
        file.create();
        fileList.add(file);

        file = FileManager.newLocalFile(fileNamesList.get(1));
        file.addAttributes(Attribute.OWNER_READ, Attribute.GROUP_READ, Attribute.OTHERS_READ);
        file.create();
        Files.write(new File(file.getUrl().toURI()).toPath(), StringUtils.repeat(dataString, 40).getBytes());
        file.updateModel();
        fileList.add(file);

        file = FileManager.newLocalFile(fileNamesList.get(2));
        file.addAttributes(Attribute.OWNER_READ, Attribute.OWNER_EXECUTE, Attribute.GROUP_READ);
        file.create();
        Files.write(new File(file.getUrl().toURI()).toPath(), StringUtils.repeat(dataString, 50).getBytes());
        file.updateModel();
        fileList.add(file);

        file = FileManager.newLocalFile(fileNamesList.get(3));
        file.create();
        fileList.add(file);

        file = FileManager.newLocalFile(fileNamesList.get(4));
        file.addAttributes(Attribute.OWNER_READ, Attribute.OWNER_EXECUTE, Attribute.GROUP_READ, Attribute.GROUP_EXECUTE);
        file.create();
        Files.write(new File(file.getUrl().toURI()).toPath(), StringUtils.repeat(dataString, 200).getBytes());
        file.updateModel();
        fileList.add(file);

        return fileList;
    }

    public static List<String> getNameListFromFileList(List<IFile> fileList) {
        List<String> nameList = new ArrayList<>();
        for (IFile file : fileList)
            nameList.add(file.getName());
        return nameList;
    }
}
