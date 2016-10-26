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

abstract class FileIterationLogic<L extends Listener> {
    protected VirtualFile source;
    protected VirtualFile target;
    protected L listener;
    protected Integer filesProcessed = 0;

    private boolean createFile;

    public FileIterationLogic(VirtualFile source, VirtualFile target, L listener) {
        this.source = source;
        this.target = target;
        this.listener = listener;
    }

    public void executeIteration() {
        createFile = true;
        if (target.exists()) {
            if (listener != null)
                createFile = listener.fileExists(target);
        }

        if (createFile) {
            if (source.isDirectory()) {
                if (!target.exists()) {
                    target.create();
                }
            } else {
                executeOperation();
            }
        }
        filesProcessed++;
    }

    public abstract void executeOperation();

    public VirtualFile getSource() {
        return source;
    }

    public void setSource(VirtualFile source) {
        this.source = source;
    }

    public VirtualFile getTarget() {
        return target;
    }

    public void setTarget(VirtualFile target) {
        this.target = target;
    }

    public Integer getFilesProcessed() {
        return filesProcessed;
    }
}