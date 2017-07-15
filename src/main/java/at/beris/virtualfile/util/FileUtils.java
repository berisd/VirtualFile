/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.filter.Filter;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

public class FileUtils {
    public static boolean isArchive(String pathName) {
        for (String extension : getArchiveExtensions()) {
            if (pathName.toUpperCase().endsWith("." + extension))
                return true;
        }
        return false;
    }

    public static boolean isArchived(String urlString) {
        String[] pathParts = urlString.split("/");

        for (int i = 0; i < pathParts.length - 1; i++) {
            if (isArchive(pathParts[i]))
                return true;
        }
        return false;
    }

    public static boolean isDirectory(String urlString) {
        return urlString.endsWith("/");
    }

    public static List<String> getArchiveExtensions() {
        return Arrays.asList("ZIP", "JAR", "TAR", "7Z", "ARJ");
    }

    public static Map<Filter, List<VirtualFile>> groupFileListByFilters(List<VirtualFile> fileList, List<Filter> filterList) {
        Map<Filter, List<VirtualFile>> partitionedFileList = new HashMap<>();

        for (Filter filter : filterList) {
            partitionedFileList.put(filter, new ArrayList<VirtualFile>());
        }

        for (VirtualFile file : fileList) {
            for (Filter filter : filterList) {
                if (filter.filter(file))
                    partitionedFileList.get(filter).add(file);
            }
        }

        return partitionedFileList;
    }

    public static String getAttributesString(FileAttribute[] attributes) {
        StringBuilder sb = new StringBuilder();

        if (attributes.length < 1)
            sb.append("<none>");
        else {
            for (FileAttribute attribute : attributes) {
                if (sb.length() > 0)
                    sb.append(", ");
                sb.append(attribute.toString());
            }
        }
        return sb.toString();
    }

    public static String getName(String path) {
        if (path.endsWith("/"))
            path = path.substring(0, path.lastIndexOf('/'));
        return path.substring(path.lastIndexOf('/') + 1);
    }

    /**
     * Delete a directory recursively.
     *
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(String directory) throws IOException {
        deleteDirectory(Paths.get(directory));
    }

    /**
     * Delete a directory recursively.
     *
     * @param directory
     * @throws IOException
     */
    public static void deleteDirectory(Path directory) throws IOException {
        Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }

        });
    }
}
