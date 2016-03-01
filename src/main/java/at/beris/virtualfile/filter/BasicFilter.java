/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.File;
import at.beris.virtualfile.exception.VirtualFileException;

import java.util.*;

public abstract class BasicFilter<T> implements Filter<T>, Cloneable {
    private Map<Operation, Filter> combiningOperators;
    private Map<Operation, Collection<T>> operationValuesMap;
    private boolean inverseValue;

    public BasicFilter() {
        combiningOperators = new HashMap<>();
        operationValuesMap = new HashMap<>();
        inverseValue = false;
    }

    @Override
    public Object clone() {
        BasicFilter cloned = null;
        try {
            cloned = (BasicFilter) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new VirtualFileException(e);
        }
        cloned.combiningOperators = new HashMap<>(combiningOperators);
        cloned.operationValuesMap = new HashMap<>(operationValuesMap);
        return cloned;
    }

    protected void putOperationValues(Operation operation, Collection<T> values) {
        operationValuesMap.put(operation, values);
    }

    protected void putCombiningOperator(Operation operation, Filter filter) {
        combiningOperators.put(operation, filter);
    }

    @Override
    public Filter not() {
        inverseValue = true;
        return this;
    }

    @Override
    public Filter and(Filter filter) {
        putCombiningOperator(Operation.AND, filter);
        return this;
    }

    @Override
    public Filter andNot(Filter filter) {
        putCombiningOperator(Operation.AND_NOT, filter);
        return this;
    }

    @Override
    public Filter or(Filter filter) {
        putCombiningOperator(Operation.OR, filter);
        return this;
    }

    @Override
    public Filter orNot(Filter filter) {
        putCombiningOperator(Operation.OR_NOT, filter);
        return this;
    }

    @Override
    public Filter equalTo(T value) {
        operationValuesMap.put(Operation.EQUAL, Collections.singletonList(value));
        return this;
    }

    @Override
    public boolean filter(File file) {
        boolean valid = true;
        T value = getValue(file);

        if (value != null) {
            for (Map.Entry<Operation, Collection<T>> entry : operationValuesMap.entrySet()) {
                if (!valid)
                    break;
                valid = valid && matchFilter(value, entry.getKey(), entry.getValue());
            }
        }

        if (inverseValue)
            valid = !valid;

        for (Map.Entry<Operation, Filter> entry : combiningOperators.entrySet()) {
            valid = combineFilter(valid, file, entry.getKey(), entry.getValue());
        }

        return valid;
    }

    private boolean combineFilter(boolean valid, File file, Operation operation, Filter filter) {
        switch (operation) {
            case NOT:
                return valid && (!filter.filter(file));
            case AND:
                return valid && filter.filter(file);
            case AND_NOT:
                return valid && !filter.filter(file);
            case OR:
                return valid || filter.filter(file);
            case OR_NOT:
                return valid || !filter.filter(file);
            default:
                return false;
        }
    }

    protected boolean matchFilter(T value, Operation operation, Collection<T> filterValues) {
        boolean valid = true;
        if (filterValues.size() < 1)
            return false;
        else if (filterValues.size() == 1) {
            Iterator<T> iterator = filterValues.iterator();
            if (iterator.hasNext()) {
                T filterValue = iterator.next();
                valid = valid && matchSingleValue(value, filterValue, operation);
            }
        } else
            valid = valid && matchMultipleValues(value, filterValues, operation);
        return valid;
    }

    protected boolean matchSingleValue(T value, T filterValue, Operation operation) {
        Comparable<T> comparator = (Comparable<T>) value;
        switch (operation) {
            case EQUAL:
                return comparator.compareTo(filterValue) == 0;
            case GREATER_THAN:
                return comparator.compareTo(filterValue) == 1;
            case GREATER_THAN_OR_EQUAL:
                return comparator.compareTo(filterValue) >= 0;
            case LESS_THAN:
                return comparator.compareTo(filterValue) == -1;
            case LESS_THAN_OR_EQUAL:
                return comparator.compareTo(filterValue) <= 0;
        }
        return false;
    }

    protected boolean matchMultipleValues(T value, Collection<T> filterValues, Operation operation) {
        boolean valid = false;
        switch (operation) {
            case IN:
                for (T filterValue : filterValues) {
                    valid = valid || matchSingleValue(value, filterValue, Operation.EQUAL);
                }
        }
        return valid;
    }

    abstract protected T getValue(File file);
}
