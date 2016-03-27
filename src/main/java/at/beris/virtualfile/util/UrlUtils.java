/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.FileType;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

public class UrlUtils {
    public static Protocol getProtocol(URL url) {
        String protocolString = url.getProtocol();

        try {
            return Protocol.valueOf(url.getProtocol().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new VirtualFileException("Unknown protocol: " + protocolString);
        }
    }

    public static String getSiteUrlString(URL url) {
        String urlString = url.toString();
        return urlString.substring(0, urlString.indexOf("/", urlString.indexOf("//") + 2));
    }

    public static FileType getFileTypeForUrl(URL url) {
        FileType fileType = FileType.DEFAULT;
        String[] pathParts = url.toString().split("/");

        if (FileUtils.isArchive(pathParts[pathParts.length - 1]))
            fileType = FileType.ARCHIVE;
        else if (FileUtils.isArchived(url))
            fileType = FileType.ARCHIVED;
        return fileType;
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
