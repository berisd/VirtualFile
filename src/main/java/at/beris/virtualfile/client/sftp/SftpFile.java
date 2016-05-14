/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.sftp;

import com.jcraft.jsch.SftpATTRS;

public class SftpFile {

    private SftpATTRS sftpATTRS;
    private String path;

    public void setSftpATTRS(SftpATTRS sftpATTRS) {
        this.sftpATTRS = sftpATTRS;
    }

    public String getPath() {
        return this.path;
    }

    public SftpATTRS getFile() {
        return sftpATTRS;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
