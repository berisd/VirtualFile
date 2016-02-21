/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public abstract class CollectionFilter<C extends Collection, E> extends BasicFilter<C> {
    public CollectionFilter contains(E value) {
        putOperationValues(Operation.CONTAINS, (C) Collections.singletonList(new HashSet<>(Arrays.asList(value))));
        return this;
    }

    public CollectionFilter containsAll(C value) {
        putOperationValues(Operation.CONTAINS_ALL, value);
        return this;
    }

    @Override
    protected boolean matchMultipleValues(C value, Collection<C> filterValues, Operation operation) {
        switch (operation) {
            case CONTAINS_ALL:
                return value.containsAll(filterValues);
            default:
                return super.matchMultipleValues(value, filterValues, operation);
        }
    }

    @Override
    protected boolean matchSingleValue(C value, C filterValue, Operation operation) {
        switch (operation) {
            case EQUAL:
                return value.containsAll(filterValue) && value.size() == filterValue.size();
            case CONTAINS:
                return filterValue.iterator().hasNext() ? value.contains(filterValue.iterator().next()) : false;
            default:
                return super.matchSingleValue(value, filterValue, operation);
        }
    }
}
