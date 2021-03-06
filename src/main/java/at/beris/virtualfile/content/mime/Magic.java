/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.mime;

/**
 * Defines a magic for a MimeType. A magic is made of one or several
 * MagicClause.
 * 
 * 
 */
class Magic implements Clause, Comparable<Magic> {

    private final MimeType type;

    private final int priority;

    private final Clause clause;

    private final String string;

    Magic(MimeType type, int priority, Clause clause) {
        this.type = type;
        this.priority = priority;
        this.clause = clause;
        this.string = "[" + priority + "/" + clause + "]";
    }

    MimeType getType() {
        return type;
    }

    int getPriority() {
        return priority;
    }

    public boolean eval(byte[] data) {
        return clause.eval(data);
    }

    public int size() {
        return clause.size();
    }

    public String toString() {
        return string;
    }

    public int compareTo(Magic o) {
        int diff = o.priority - priority;
        if (diff == 0) {
            diff = o.size() - size();
        }
        if (diff == 0) {
            diff = o.type.compareTo(type);
        }
        if (diff == 0) {
            diff = o.string.compareTo(string);
        }
        return diff;
    }

    public boolean equals(Object o) {
        if (o instanceof Magic) {
            Magic that = (Magic) o;
            return type.equals(that.type) && string.equals(that.string);
        }
        return false;
    }

    public int hashCode() {
        return type.hashCode() ^ string.hashCode();
    }

}
