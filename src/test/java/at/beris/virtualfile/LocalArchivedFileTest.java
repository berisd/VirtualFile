/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.util.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;

import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class LocalArchivedFileTest {

    private String directoryPath;
    private String filePath;
    private int fileSize;

    @Parameterized.Parameters
    public static Collection<Object[]> data() throws Exception {
        UrlUtils.registerProtocolURLStreamHandlers();

        String directoryUrlPattern = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.??/TreeDb/";
        String fileNameUrlPattern = "src" + File.separator + "test" + File.separator + "resources" + File.separator + "testarchive.??/TreeDb/file.xml";
        //TODO Extension 7z StreamingNotSupportedException: The 7z doesn't support streaming
        String[] archiveExtensions = {"zip", "tar"};


        Object[][] data = new Object[archiveExtensions.length][3];
        for (int i = 0; i < archiveExtensions.length; i++) {
            String archiveExtension = archiveExtensions[i];
            data[i] = new Object[]{directoryUrlPattern.replace("??", archiveExtension), fileNameUrlPattern.replace("??", archiveExtension), 0};
        }

        return Arrays.asList(data);
    }

    public LocalArchivedFileTest(String directoryPath, String filePath, int fileSize) {
        this.filePath = filePath;
        this.directoryPath = directoryPath;
        this.fileSize = fileSize;
    }

    @Test
    public void directoryInfo() {
        VirtualFile file = FileManager.newLocalFile(directoryPath);
        assertEquals(extractName(directoryPath), file.getName());
        assertTrue(file.isDirectory());
    }

    @Test
    public void fileInfo() {
        VirtualFile file = FileManager.newLocalFile(filePath);
        assertEquals(extractName(filePath), file.getName());
        assertEquals(fileSize, file.getSize());
        assertFalse(file.isDirectory());
    }

    private String extractName(String path) {
        String[] pathParts = StringUtils.split(path, File.separator);
        return pathParts[pathParts.length - 1];

    }
}
