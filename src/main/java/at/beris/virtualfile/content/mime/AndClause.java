/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.mime;

import java.util.Arrays;

class AndClause implements Clause {

    private final Clause[] clauses;

    AndClause(Clause... clauses) {
        this.clauses = clauses;
    }

    public boolean eval(byte[] data) {
        for (Clause clause : clauses) {
            if (!clause.eval(data)) {
                return false;
            }
        }
        return true;
    }

    public int size() {
        int size = 0;
        for (Clause clause : clauses) {
            size += clause.size();
        }
        return size;
    }

    public String toString() {
        return "and" + Arrays.toString(clauses);
    }

}
