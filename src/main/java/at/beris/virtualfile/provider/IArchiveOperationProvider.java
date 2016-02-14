/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.provider;

import at.beris.virtualfile.FileModel;
import at.beris.virtualfile.IFile;
import at.beris.virtualfile.client.IClient;

import java.util.List;

public interface IArchiveOperationProvider {
    List<IFile> extract(IClient client, FileModel model, IFile target);
}
