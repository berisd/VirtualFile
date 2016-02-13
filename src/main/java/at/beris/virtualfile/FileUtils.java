/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class FileUtils {
    public static boolean isArchive(String pathName) {
        for (String extension : getArchiveExtensions()) {
            if (pathName.toUpperCase().endsWith("." + extension))
                return true;
        }
        return false;
    }

    public static List<String> getArchiveExtensions() {
        return Arrays.asList(new String[]{"ZIP", "JAR", "TAR", "7Z", "ARJ"});
    }

    public static URL normalizeUrl(URL url) {
        try {
            URI uri = URI.create(url.toString());
            return uri.normalize().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL newUrl(String url) {
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL newUrl(URL context, String spec) {
        try {
            return new URL(context, spec);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static URL getUrlForLocalPath(String path) {
        try {
            return new URL(new java.io.File(path).toURI().toURL().toString() + (path.endsWith(java.io.File.separator) ? "/" : ""));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
