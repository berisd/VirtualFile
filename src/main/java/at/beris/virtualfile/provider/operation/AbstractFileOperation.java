/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider.operation;

import at.beris.virtualfile.VirtualFile;
import at.beris.virtualfile.VirtualFileContext;
import at.beris.virtualfile.exception.Message;
import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.provider.FileOperationProvider;

import java.net.MalformedURLException;
import java.net.URL;

public abstract class AbstractFileOperation<R, L extends Listener> {

    public final static int COPY_BUFFER_SIZE = 1024 * 16;

    protected VirtualFileContext fileContext;
    protected FileOperationProvider fileOperationProvider;

    public AbstractFileOperation(VirtualFileContext fileContext, FileOperationProvider fileOperationProvider) {
        super();
        this.fileContext = fileContext;
        this.fileOperationProvider = fileOperationProvider;
    }

    public R execute(VirtualFile source, VirtualFile target, L listener) {
        if (!source.exists())
            throw new VirtualFileException(Message.NO_SUCH_FILE(source.getPath()));
        return null;
    }

    protected void iterateRecursively(FileIterationLogic iterationLogic) {
        iterationLogic.executeIteration();
        if (iterationLogic.getSource().isDirectory()) {
            for (VirtualFile sourceChildFile : iterationLogic.getSource().list()) {
                iterationLogic.setSource(sourceChildFile);
                URL targetUrl = iterationLogic.getTarget().getUrl();
                URL targetChildUrl;
                try {
                    targetChildUrl = new URL(targetUrl, targetUrl.getFile() + sourceChildFile.getName() + (sourceChildFile.isDirectory() ? "/" : ""));
                } catch (MalformedURLException e) {
                    throw new VirtualFileException(e);
                }
                VirtualFile parentTarget = iterationLogic.getTarget();
                iterationLogic.setTarget(fileContext.newFile(targetChildUrl));
                iterateRecursively(iterationLogic);
                iterationLogic.setTarget(parentTarget);
            }
        }
        iterationLogic.getTarget().refresh();
    }
}
