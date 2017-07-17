/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */

package at.beris.virtualfile;

import at.beris.virtualfile.exception.VirtualFileException;
import at.beris.virtualfile.protocol.Protocol;
import at.beris.virtualfile.util.CharUtils;
import at.beris.virtualfile.util.DisposableObject;
import at.beris.virtualfile.util.StringUtils;
import org.slf4j.LoggerFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class SiteManager implements DisposableObject {

    private final static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SiteManager.class);

    private KeyStoreManager keyStoreManager;

    private SiteList siteList;
    private Map<String, Site> siteByUrlMap;

    private Path siteFilePath;

    private SiteManager(Configuration configuration, KeyStoreManager keyStoreManager) {
        this.keyStoreManager = keyStoreManager;
        this.siteList = new SiteList();
        this.siteByUrlMap = new HashMap<>();
        siteFilePath = Paths.get(configuration.getHomeDirectory(), "sites.xml");
    }

    public static SiteManager create(Configuration configuration, KeyStoreManager keyStoreManager) {
        return new SiteManager(configuration, keyStoreManager);
    }

    public Optional<Site> findSiteByShortName(String shortName) {
        return findSiteByCriteria(s -> s.getShortName().equals(shortName));
    }

    public Optional<Site> findSiteByName(String name) {
        return findSiteByCriteria(s -> s.getName().equals(name));
    }

    public Optional<Site> findSiteById(String id) {
        return findSiteByCriteria(s -> s.getId().equals(id));
    }

    private Optional<Site> findSiteByCriteria(Predicate<Site> predicate) {
        for (Site site : siteList) {
            if (predicate.test(site))
                return Optional.of(site);
        }
        return Optional.empty();
    }

    public List<Site> getSites() {
        return siteList;
    }

    public Site getSiteForUrl(URL url) {
        String siteUrlString = getSiteUrlString(url);
        return siteByUrlMap.get(siteUrlString);
    }

    public SiteManager addSite(Site site) {
        String passwordReference = keyStoreManager.addPassword(site.getPassword());
        site.setPasswordReference(passwordReference);

        siteList.add(site);
        siteByUrlMap.put(getSiteUrlString(site), site);
        return this;
    }

    public void removeSite(Site site) {
        keyStoreManager.removePassword(site.getPasswordReference());
        siteByUrlMap.remove(getSiteUrlString(site));
        siteList.remove(site);
    }

    public void clearSites() {
        for (Site site : siteList) {
            keyStoreManager.removePassword(site.getPasswordReference());
        }

        siteByUrlMap.clear();
        siteList.clear();
    }

    public void load() {
        File sitesFile = siteFilePath.toFile();
        LOGGER.info("Loading sites from '{}'", sitesFile.toString());
        JAXBContext jaxbContext = null;
        try {
            jaxbContext = JAXBContext.newInstance(SiteList.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            clearSites();
            this.siteList = (SiteList) jaxbUnmarshaller.unmarshal(sitesFile);
        } catch (JAXBException e) {
            throw new VirtualFileException(e);
        }
    }

    public void save() {
        File sitesFile = siteFilePath.toFile();
        LOGGER.info("Saving sites to '{}'", sitesFile.toString());
        if (sitesFile.exists())
            sitesFile.delete();

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(SiteList.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            jaxbMarshaller.marshal(this.siteList, sitesFile);
        } catch (JAXBException e) {
            throw new VirtualFileException(e);
        }
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
    }

    @Override
    public void dispose() {
        siteByUrlMap.clear();
        siteList.clear();
    }
}
