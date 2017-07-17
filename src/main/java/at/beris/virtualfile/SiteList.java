/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.*;

@XmlRootElement(name = "sites")
@XmlAccessorType(XmlAccessType.FIELD)
public class SiteList implements List<Site> {
    @XmlElement(name = "site")
    private List<Site> siteList = new ArrayList<>();


    @Override
    public int size() {
        return siteList.size();
    }

    @Override
    public boolean isEmpty() {
        return siteList.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return siteList.contains(o);
    }

    @Override
    public Iterator<Site> iterator() {
        return siteList.iterator();
    }

    @Override
    public Object[] toArray() {
        return siteList.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return siteList.toArray(a);
    }

    @Override
    public boolean add(Site site) {
        return siteList.add(site);
    }

    @Override
    public boolean remove(Object o) {
        return siteList.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return siteList.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Site> c) {
        return siteList.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends Site> c) {
        return siteList.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return siteList.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return siteList.retainAll(c);
    }

    @Override
    public void clear() {
        siteList.clear();
    }

    @Override
    public Site get(int index) {
        return siteList.get(index);
    }

    @Override
    public Site set(int index, Site element) {
        return siteList.set(index, element);
    }

    @Override
    public void add(int index, Site element) {
        siteList.add(index, element);
    }

    @Override
    public Site remove(int index) {
        return siteList.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return siteList.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return siteList.lastIndexOf(o);
    }

    @Override
    public ListIterator<Site> listIterator() {
        return siteList.listIterator();
    }

    @Override
    public ListIterator<Site> listIterator(int index) {
        return siteList.listIterator(index);
    }

    @Override
    public List<Site> subList(int fromIndex, int toIndex) {
        return siteList.subList(fromIndex, toIndex);
    }
}
