/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.FileType;
import at.beris.virtualfile.protocol.Protocol;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

public class UrlUtils {
    public static Protocol getProtocol(URL url) {
        return Protocol.valueOf(url.getProtocol().toUpperCase());
    }

    public static String getSiteUrlString(String urlString) {
        int indexPathBegin = urlString.indexOf("/", urlString.indexOf("//") + 2);
        if (indexPathBegin == -1)
            return urlString;
        else
            return urlString.substring(0, indexPathBegin);
    }

    public static FileType getFileTypeForUrl(String urlString) {
        FileType fileType = FileType.DEFAULT;
        String[] pathParts = urlString.split("/");

        if (FileUtils.isArchive(pathParts[pathParts.length - 1]))
            fileType = FileType.ARCHIVE;
        else if (FileUtils.isArchived(urlString))
            fileType = FileType.ARCHIVED;
        return fileType;
    }

    public static URL normalizeUrl(URL url) throws IOException {
        URI uri = URI.create(url.toString());
        return uri.normalize().toURL();
    }

    public static URL newUrl(String urlString) throws IOException {
        return new URL(urlString);
    }

    public static URL newUrl(URL context, String spec) throws IOException {
        return new URL(context, spec);
    }

    public static URL newUrlReplacePath(URL context, String path) throws IOException {
        String contextUrlString = context.toString();
        String newUrlString = contextUrlString.substring(0, contextUrlString.length() - context.getPath().length());
        newUrlString += path;
        return new URL(newUrlString);
    }

    public static URL getUrlForLocalPath(String path) throws IOException {
        return new URL(new File(path).toURI().toURL().toString() + (path.endsWith(File.separator) ? "/" : ""));
    }

    /**
     * Masks sensitive information in an url (e.g. for logging)
     *
     * @param url URL
     * @return Masked URL String
     */
    public static String maskedUrlString(URL url) {
        StringBuilder stringBuilder = new StringBuilder("");

        stringBuilder.append(url.getProtocol());
        stringBuilder.append(':');
        if (!url.getProtocol().toLowerCase().equals("file"))
            stringBuilder.append("//");

        String authority = url.getAuthority();
        if (!StringUtils.isEmpty(authority)) {
            String[] authorityParts = authority.split("@");

            if (authorityParts.length > 1) {
                String[] userInfoParts = authorityParts[0].split(":");
                stringBuilder.append(userInfoParts[0]);

                if (userInfoParts.length > 1) {
                    stringBuilder.append(":***");
                }
                stringBuilder.append('@');
                stringBuilder.append(authorityParts[1]);
            } else {
                stringBuilder.append(authorityParts[0]);
            }
        }

        stringBuilder.append(url.getPath());

        return stringBuilder.toString();
    }

    public static String getParentPath(String urlPath) {
        if (urlPath.endsWith("/"))
            urlPath = urlPath.substring(0, urlPath.lastIndexOf('/'));
        return urlPath.substring(0, urlPath.lastIndexOf('/') + 1);
    }

    public static String getLastPathPart(String urlPath) {
        if (urlPath.endsWith("/"))
            urlPath = urlPath.substring(0, urlPath.lastIndexOf('/'));
        return urlPath.substring(urlPath.lastIndexOf('/') + 1);
    }

    /**
     * Set property so that URL class will find custom handlers
     */
    public static void registerProtocolURLStreamHandlers() {
        String propertyKey = "java.protocol.handler.pkgs";
        String propertyValue = System.getProperties().getProperty(propertyKey);
        if (StringUtils.isEmpty(propertyValue))
            propertyValue = "";
        else
            propertyValue += "|";
        propertyValue += at.beris.virtualfile.protocol.Protocol.class.getPackage().getName();
        System.getProperties().setProperty(propertyKey, propertyValue);
    }
}
