/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.client.ftp;

import at.beris.virtualfile.exception.VirtualFileException;
import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.io.OutputStream;

public class FtpOutputStream extends OutputStream {
    private OutputStream outputStream;
    private FTPClient ftpClient;

    public FtpOutputStream(OutputStream outputStream, FTPClient ftpClient) {
        this.outputStream = outputStream;
        this.ftpClient = ftpClient;
    }

    @Override
    public void write(int b) {
        try {
            outputStream.write(b);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void write(byte[] b) {
        try {
            outputStream.write(b);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void write(byte[] b, int off, int len) {
        try {
            outputStream.write(b, off, len);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void flush() {
        try {
            outputStream.flush();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void close() {
        try {
            outputStream.close();
            ftpClient.completePendingCommand();
            ftpClient = null;
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }
}
