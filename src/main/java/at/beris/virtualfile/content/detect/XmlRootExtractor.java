/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.detect;

import at.beris.virtualfile.io.CloseShieldInputStream;
import at.beris.virtualfile.sax.OfflineContentHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

/**
 * Utility class that uses a {@link javax.xml.parsers.SAXParser} to determine
 * the namespace URI and local name of the root element of an XML file.
 *
 * @since Apache Tika 0.4
 */
public class XmlRootExtractor {

    public QName extractRootElement(byte[] data) {
        return extractRootElement(new ByteArrayInputStream(data));
    }

    /**
     * @since Apache Tika 0.9
     */
    public QName extractRootElement(InputStream stream) {
        ExtractorHandler handler = new ExtractorHandler();
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(false);
            try {
                factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
            } catch (SAXNotRecognizedException e) {
                // TIKA-271 and TIKA-1000: Some XML parsers do not support the secure-processing
                // feature, even though it's required by JAXP in Java 5. Ignoring
                // the exception is fine here, deployments without this feature
                // are inherently vulnerable to XML denial-of-service attacks.
            }
            factory.newSAXParser().parse(
                    new CloseShieldInputStream(stream),
                    new OfflineContentHandler(handler));
        } catch (Exception ignore) {
        }
        return handler.rootElement;
    }

    private static class ExtractorHandler extends DefaultHandler {

        private QName rootElement = null;

        @Override
        public void startElement(
                String uri, String local, String name, Attributes attributes)
                throws SAXException {
            this.rootElement = new QName(uri, local);
            throw new SAXException("Aborting: root element received");
        }

    }

}
