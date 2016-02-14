/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import java.util.Collection;
import java.util.Collections;

public abstract class CollectionFilter<C extends Collection, E> extends BasicFilter<C> {
    public CollectionFilter contains(E value) {
        putOperationValues(Operation.CONTAINS, (C) Collections.singletonList(value));
        return this;
    }

    public CollectionFilter containsAll(C value) {
        putOperationValues(Operation.CONTAINS_ALL, value);
        return this;
    }

    @Override
    protected boolean matchSingleValue(C value, C filterValue, Operation operation) {
        switch (operation) {
            case EQUAL:
                return value.containsAll(filterValue) && value.size() == filterValue.size();
            case CONTAINS:
                return value.contains(filterValue);
            case CONTAINS_ALL:
                return value.containsAll(filterValue);
            default:
                return super.matchSingleValue(value, filterValue, operation);
        }
    }
}
