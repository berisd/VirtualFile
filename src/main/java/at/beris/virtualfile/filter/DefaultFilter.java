/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public abstract class DefaultFilter<T> extends BasicFilter<T> {
    public DefaultFilter greaterThan(T value) {
        putOperationValues(Operation.GREATER_THAN, Collections.singletonList(value));
        return this;
    }

    public DefaultFilter greaterThanOrEqualTo(T value) {
        putOperationValues(Operation.GREATER_THAN_OR_EQUAL, Collections.singletonList(value));
        return this;
    }

    public DefaultFilter lessThan(T value) {
        putOperationValues(Operation.LESS_THAN, Collections.singletonList(value));
        return this;
    }

    public DefaultFilter lessThanOrEqualTo(T value) {
        putOperationValues(Operation.GREATER_THAN_OR_EQUAL, Collections.singletonList(value));
        return this;
    }

    public DefaultFilter in(T... values) {
        Collection<T> collection = new ArrayList<>();
        Collections.addAll(collection, values);
        putOperationValues(Operation.IN, collection);
        return this;
    }

    public DefaultFilter between(T fromValue, T toValue) {
        putOperationValues(Operation.GREATER_THAN_OR_EQUAL, Collections.singletonList(fromValue));
        putOperationValues(Operation.LESS_THAN_OR_EQUAL, Collections.singletonList(toValue));
        return this;
    }
}
