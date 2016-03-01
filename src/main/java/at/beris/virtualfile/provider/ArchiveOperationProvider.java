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
import at.beris.virtualfile.File;
import at.beris.virtualfile.client.Client;

import java.util.List;

public interface ArchiveOperationProvider {
    List<File> extract(Client client, FileModel model, File target);
}