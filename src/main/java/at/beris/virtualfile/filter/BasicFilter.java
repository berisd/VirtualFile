/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import at.beris.virtualfile.IFile;

import java.util.*;

public abstract class BasicFilter<T> {
    private Map<Operation, DefaultFilter> combiningOperators;
    private Map<Operation, Collection<T>> operationValuesMap;

    public BasicFilter() {
        combiningOperators = new HashMap<Operation, DefaultFilter>();
        operationValuesMap = new HashMap<Operation, Collection<T>>();
    }

    protected void putOperationValues(Operation operation, Collection<T> values) {
        operationValuesMap.put(operation, values);
    }

    protected void putCombiningOperator(Operation operation, DefaultFilter defaultFilter) {
        combiningOperators.put(operation, defaultFilter);
    }

    public BasicFilter not(DefaultFilter defaultFilter) {
        putCombiningOperator(Operation.NOT, defaultFilter);
        return this;
    }

    public BasicFilter and(DefaultFilter defaultFilter) {
        putCombiningOperator(Operation.AND, defaultFilter);
        return this;
    }

    public BasicFilter or(DefaultFilter defaultFilter) {
        putCombiningOperator(Operation.OR, defaultFilter);
        return this;
    }

    public BasicFilter equal(T value) {
        operationValuesMap.put(Operation.EQUAL, Collections.singletonList(value));
        return this;
    }

    public boolean filter(IFile file) {
        boolean valid = true;
        T value = getValue(file);

        if (value != null) {
            for (Map.Entry<Operation, Collection<T>> entry : operationValuesMap.entrySet()) {
                if (!valid)
                    break;
                valid = valid && matchFilter(value, entry.getKey(), entry.getValue());
            }

            for (Map.Entry<Operation, DefaultFilter> entry : combiningOperators.entrySet()) {
                if (!valid)
                    break;
                combineFilter(valid, file, entry.getKey(), entry.getValue());
            }
        }

        return valid;
    }

    private void combineFilter(boolean valid, IFile file, Operation operation, DefaultFilter defaultFilter) {
        switch (operation) {
            case NOT:
                valid = valid && (!defaultFilter.filter(file));
                break;
            case AND:
                valid = valid && defaultFilter.filter(file);
                break;
            case OR:
                valid = valid || defaultFilter.filter(file);
                break;
        }
    }

    private boolean matchFilter(T value, Operation operation, Collection<T> filterValues) {
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
        switch (operation) {
            case IN:
                for (T filterValue : filterValues) {
                    matchSingleValue(value, filterValue, Operation.EQUAL);
                }
        }
        return false;
    }

    abstract protected T getValue(IFile file);
}