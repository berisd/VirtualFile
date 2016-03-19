/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

public class IntegerConfigValue extends ConfigValue<Integer> {

    public IntegerConfigValue(Integer value) {
        super(value);
    }

    @Override
    public ConfigValue clone() {
        return super.clone(new Integer(value));
    }
}