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

abstract class FileIterationLogic<L> {
    protected VirtualFile source;
    protected VirtualFile target;
    protected L listener;
    protected Long filesProcessed = 0L;

    public FileIterationLogic(VirtualFile source, VirtualFile target, L listener) {
        this.source = source;
        this.target = target;
        this.listener = listener;
    }

    public abstract void before();

    public abstract void execute();

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

    public long getFilesProcessed() {
        return filesProcessed;
    }
}