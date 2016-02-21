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
import at.beris.virtualfile.exception.VirtualFileException;

import java.util.*;

public abstract class BasicFilter<T> implements IFilter<T>, Cloneable {
    private Map<Operation, IFilter> combiningOperators;
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

    protected void putCombiningOperator(Operation operation, IFilter filter) {
        combiningOperators.put(operation, filter);
    }

    @Override
    public IFilter not() {
        inverseValue = true;
        return this;
    }

    @Override
    public IFilter and(IFilter filter) {
        putCombiningOperator(Operation.AND, filter);
        return this;
    }

    @Override
    public IFilter andNot(IFilter filter) {
        putCombiningOperator(Operation.AND_NOT, filter);
        return this;
    }

    @Override
    public IFilter or(IFilter filter) {
        putCombiningOperator(Operation.OR, filter);
        return this;
    }

    @Override
    public IFilter orNot(IFilter filter) {
        putCombiningOperator(Operation.OR_NOT, filter);
        return this;
    }

    @Override
    public IFilter equalTo(T value) {
        operationValuesMap.put(Operation.EQUAL, Collections.singletonList(value));
        return this;
    }

    @Override
    public boolean filter(IFile file) {
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

        for (Map.Entry<Operation, IFilter> entry : combiningOperators.entrySet()) {
            valid = combineFilter(valid, file, entry.getKey(), entry.getValue());
        }

        return valid;
    }

    private boolean combineFilter(boolean valid, IFile file, Operation operation, IFilter filter) {
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

    abstract protected T getValue(IFile file);
}
