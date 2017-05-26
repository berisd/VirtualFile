/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.util;

import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class UrlUtils {

    public static final String PROPERTY_KEY_PROTOCOL_HANDLER_PKGS = "java.protocol.handler.pkgs";

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

    public static URL normalizeUrl(URL url) {
        URI uri = URI.create(url.toString());
        try {
            return uri.normalize().toURL();
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public static URL newUrl(String urlString) {
        try {
            return new URL(urlString);
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

    public static URL newUrlReplacePath(URL context, String path) {
        String contextUrlString = context.toString();
        String newUrlString = contextUrlString.substring(0, contextUrlString.length() - context.getPath().length());
        newUrlString += path;
        try {
            return new URL(newUrlString);
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
    }

    public static URL getUrlForLocalPath(String path) {
        try {
            return new URL(new File(path).toURI().toURL().toString() + (path.endsWith(File.separator) ? "/" : ""));
        } catch (MalformedURLException e) {
            throw new VirtualFileException(e);
        }
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

    public static URL getParentUrl(URL url) {
        String path = url.getPath();
        int indexPathBegin = path.indexOf("/", path.indexOf("//") + 2);
        if (indexPathBegin == -1)
            return null;

        String parentPath = UrlUtils.getParentPath(url.toString());
        return newUrl(newUrl(getSiteUrlString(url.toString())), parentPath);
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
        String propertyValue = System.getProperties().getProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS);
        Set<String> partSet = new HashSet<>();
        if (!StringUtils.isEmpty(propertyValue))
            partSet.addAll(Arrays.asList(StringUtils.split(propertyValue, '|')));
        partSet.add(at.beris.virtualfile.protocol.Protocol.class.getPackage().getName());
        System.getProperties().setProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS, StringUtils.join(partSet, '|'));
    }

    public static void unregisterProtocolURLStreamHandlers() {
        String propertyValue = System.getProperties().getProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS);
        if (!StringUtils.isEmpty(propertyValue)) {
            Set<String> partSet = new HashSet<>();
            partSet.addAll(Arrays.asList(StringUtils.split(propertyValue, '|')));
            partSet.remove(at.beris.virtualfile.protocol.Protocol.class.getPackage().getName());
            System.getProperties().setProperty(PROPERTY_KEY_PROTOCOL_HANDLER_PKGS, StringUtils.join(partSet, '|'));
        }
    }
}
