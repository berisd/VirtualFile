/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.config;

public abstract class ConfigValue<T> implements Cloneable {
    protected T value;

    public ConfigValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    @Override
    public abstract ConfigValue clone();

    protected ConfigValue clone(T newValue) {
        ConfigValue clone = null;
        try {
            clone = (ConfigValue) super.clone();
            clone.setValue(newValue);
        } catch (CloneNotSupportedException e) {
        }
        return clone;
    }
}
