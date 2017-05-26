/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.content;

import at.beris.virtualfile.content.charset.CharsetDetector;
import at.beris.virtualfile.content.charset.CharsetMatch;
import org.junit.Assert;
import org.junit.Test;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

public class CharsetTest {
    @Test
    public void detectCharset() {
        Map<String, String> fileNameToCharsetMap = new HashMap<>();
        fileNameToCharsetMap.put("text_utf8.txt", "UTF-8");
        fileNameToCharsetMap.put("text_iso8859-1.txt", "ISO-8859-1");
        fileNameToCharsetMap.put("text_big5.txt", "Big5");

        CharsetDetector detector = new CharsetDetector();

        for (Map.Entry<String, String> entry : fileNameToCharsetMap.entrySet()) {
            String fileName = entry.getKey();
            String expectedCharset = entry.getValue();

            try (InputStream is = new BufferedInputStream(Files.newInputStream(new File("src/test/resources/" + fileName).toPath()))) {
                detector.setText(is);
                CharsetMatch match = detector.detect();
                Assert.assertEquals(expectedCharset, match.getName());
            }
            catch (Exception e) {
                fail(e.getMessage());
            }
        }
    }
}
