/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.File;
import at.beris.virtualfile.attribute.FileAttribute;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.filter.Filter;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.*;

public class FileUtils {
    public static boolean isArchive(String pathName) {
        for (String extension : getArchiveExtensions()) {
            if (pathName.toUpperCase().endsWith("." + extension))
                return true;
        }
        return false;
    }

    public static boolean isArchived(URL url) {
        String[] pathParts = url.toString().split("/");

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
        return Arrays.asList(new String[]{"ZIP", "JAR", "TAR", "7Z", "ARJ"});
    }

    public static Map<Filter, List<File>> groupFileListByFilters(List<File> fileList, List<Filter> filterList) {
        Map<Filter, List<File>> partitionedFileList = new HashMap<>();

        for (Filter filter : filterList) {
            partitionedFileList.put(filter, new ArrayList<File>());
        }

        for (File file : fileList) {
            for (Filter filter : filterList) {
                if (filter.filter(file))
                    partitionedFileList.get(filter).add(file);
            }
        }

        return partitionedFileList;
    }

    public static String getAttributesString(FileAttribute[] attributes) {
        String attributesString = "";
        if (attributes.length < 1)
            attributesString = "<none>";
        else {
            for (FileAttribute attribute : attributes)
                attributesString = (attributesString != "" ? ", " : "") + attribute.toString();
        }
        return attributesString;
    }

    public static URL normalizeUrl(URL url) {
        try {
            URI uri = URI.create(url.toString());
            return uri.normalize().toURL();
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public static URL newUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public static URL newUrl(URL context, String spec) {
        try {
            return new URL(context, spec);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public static URL getUrlForLocalPath(String path) {
        try {
            return new URL(new java.io.File(path).toURI().toURL().toString() + (path.endsWith(java.io.File.separator) ? "/" : ""));
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    /**
     * Masks sensitive information in an url (e.g. for logging)
     *
     * @param url
     * @return
     */
    public static String maskedUrlString(URL url) {
        StringBuilder stringBuilder = new StringBuilder("");

        stringBuilder.append(url.getProtocol());
        stringBuilder.append(':');

        String authority = url.getAuthority();
        if (!StringUtils.isEmpty(authority)) {
            String[] authorityParts = authority.split("@");
            String[] userInfoParts = authorityParts[0].split(":");

            stringBuilder.append("//");
            stringBuilder.append(userInfoParts[0]);

            if (userInfoParts.length > 1) {
                stringBuilder.append(":***");
            }
            stringBuilder.append('@');
            stringBuilder.append(authorityParts[1]);
        }

        stringBuilder.append(url.getPath());

        return stringBuilder.toString();
    }
}
