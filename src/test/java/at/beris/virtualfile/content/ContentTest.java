/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.content;

import at.beris.virtualfile.content.metadata.Metadata;
import at.beris.virtualfile.content.mime.MimeTypes;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class ContentTest {

    @Test
    public void detectContent() {
        Map<String, String> fileNameToTypeMap = new HashMap<>();
        fileNameToTypeMap.put("text_utf8.txt", "text/plain");
        fileNameToTypeMap.put("test.pdf", "application/pdf");
        fileNameToTypeMap.put("image.gif", "image/gif");
        fileNameToTypeMap.put("image.png", "image/png");
        fileNameToTypeMap.put("image.jpg", "image/jpeg");
        fileNameToTypeMap.put("testarchive.zip", "application/zip");
        fileNameToTypeMap.put("testarchive.7z", "application/x-7z-compressed");
        fileNameToTypeMap.put("testarchive.tar.gz", "application/gzip");

        MimeTypes detector = MimeTypes.getDefaultMimeTypes();

        for (Map.Entry<String, String> entry : fileNameToTypeMap.entrySet()) {
            String fileName = entry.getKey();
            String expectedType = entry.getValue();

            try (InputStream is = new BufferedInputStream(Files.newInputStream(new File("src/test/resources/" + fileName).toPath()))) {
                Assert.assertEquals(expectedType, detector.detect(is, new Metadata()).toString());
            }
            catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }

}
