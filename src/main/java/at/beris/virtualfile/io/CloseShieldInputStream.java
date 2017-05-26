/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.io;

import java.io.InputStream;

/**
 * Proxy stream that prevents the underlying input stream from being closed.
 * <p>
 * This class is typically used in cases where an input stream needs to be
 * passed to a component that wants to explicitly close the stream even if
 * more input would still be available to other components.
 *
 * @since Apache Tika 0.4, copied from Commons IO 1.4
 */
public class CloseShieldInputStream extends ProxyInputStream {

    /**
     * Creates a proxy that shields the given input stream from being
     * closed.
     *
     * @param in underlying input stream
     */
    public CloseShieldInputStream(InputStream in) {
        super(in);
    }

    /**
     * Replaces the underlying input stream with a {@link ClosedInputStream}
     * sentinel. The original input stream will remain open, but this proxy
     * will appear closed.
     */
    @Override
    public void close() {
        in = new ClosedInputStream();
    }

}
