/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.CharUtils;
import at.beris.virtualfile.util.DisposableObject;
import at.beris.virtualfile.util.StringUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SiteManager implements DisposableObject {
    private List<Site> siteList;
    private Map<String, Site> siteByUrlMap;

    private SiteManager() {
        this.siteList = new ArrayList<>();
        this.siteByUrlMap = new HashMap<>();
    }

    public static SiteManager create() {
        return new SiteManager();
    }

    public Site getSiteForUrl(URL url) {
        String siteUrlString = getSiteUrlString(url);
        return siteByUrlMap.get(siteUrlString);
    }

    public void addSite(Site site) {
        siteList.add(site);
        siteByUrlMap.put(getSiteUrlString(site), site);
    }

    public void removeSite(Site site) {
        siteByUrlMap.remove(getSiteUrlString(site));
        siteList.remove(site);
    }

    public void clearSites() {
        siteByUrlMap.clear();
        siteList.clear();
    }

    public Site getSiteForUrlString(URL url) {
        String siteUrlString = getSiteUrlString(url);
        return siteByUrlMap.get(siteUrlString);
    }

    public static String getSiteUrlString(URL url) {
        return getSiteUrlString(Site.create().fillFromUrl(url));
    }

    public static String getSiteUrlString(Site site) {
        if (Protocol.FILE == site.getProtocol())
            return "file://";

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(site.getProtocol().toString().toLowerCase());
        stringBuilder.append("://");

        String username = site.getUsername();
        if (!StringUtils.isEmpty(username))
            stringBuilder.append(username);

        char[] password = site.getPassword();
        if (!CharUtils.isEmpty(password)) {
            stringBuilder.append(':');
            stringBuilder.append(password);
        }

        if (!StringUtils.isEmpty(username)) {
            stringBuilder.append('@');
        }

        stringBuilder.append(site.getHostname().toLowerCase());
        stringBuilder.append(':').append(site.getPort());

        return stringBuilder.toString();

//        return String.format("%s://%s:%d", site.getProtocol().toString().toLowerCase(), site.getHostname().toLowerCase(), site.getPort());
    }

    @Override
    public void dispose() {
        siteByUrlMap.clear();
        siteList.clear();
    }
}
