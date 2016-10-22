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
import java.io.InputStream;

public class FtpInputStream extends InputStream {
    private InputStream inputStream;
    private FTPClient ftpClient;

    public FtpInputStream(InputStream inputStream, FTPClient ftpClient) {
        this.inputStream = inputStream;
        this.ftpClient = ftpClient;
    }

    @Override
    public int read() {
        try {
            return inputStream.read();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public int read(byte[] b) {
        try {
            return inputStream.read(b);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public int read(byte[] b, int off, int len) {
        try {
            return inputStream.read(b, off, len);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public long skip(long n) {

        try {
            return inputStream.skip(n);
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public int available() {
        try {
            return inputStream.available();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void close() {
        try {
            inputStream.close();
            ftpClient.completePendingCommand();
            ftpClient = null;
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public void mark(int readlimit) {
        inputStream.mark(readlimit);
    }

    @Override
    public void reset() {
        try {
            inputStream.reset();
        } catch (IOException e) {
            throw new VirtualFileException(e);
        }
    }

    @Override
    public boolean markSupported() {
        return inputStream.markSupported();
    }
}
