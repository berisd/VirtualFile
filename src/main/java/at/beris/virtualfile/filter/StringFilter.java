/*
 * This file is part of VirtualFile.
 *
 * Copyright 2016 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile.filter;

import java.util.Collections;

public abstract class StringFilter extends BasicFilter<String> {
    public StringFilter() {
        super();
    }

    public StringFilter contains(String s) {
        putOperationValues(Operation.CONTAINS, Collections.singletonList(s));
        return this;
    }

    public StringFilter startsWith(String prefix) {
        putOperationValues(Operation.STARTS_WITH, Collections.singletonList(prefix));
        return this;
    }

    public StringFilter endsWith(String suffix) {
        putOperationValues(Operation.ENDS_WITH, Collections.singletonList(suffix));
        return this;
    }

    public StringFilter matches(String regex) {
        putOperationValues(Operation.MATCH_REGEX, Collections.singletonList(regex));
        return this;
    }

    @Override
    protected boolean matchSingleValue(String value, String filterValue, Operation operation) {
        switch (operation) {
            case CONTAINS:
                return value.contains(filterValue);
            case STARTS_WITH:
                return value.startsWith(filterValue);
            case ENDS_WITH:
                return value.endsWith(filterValue);
            case MATCH_REGEX:
                return value.matches(filterValue);
            default:
                return super.matchSingleValue(value, filterValue, operation);
        }
    }
}
