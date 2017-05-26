/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.metadata;

/**
 * Geographic schema. This is a collection of constants for geographic
 * information, as defined in the W3C Geo Vocabularies.
 *
 * @since Apache Tika 0.8
 * @see <a href="http://www.w3.org/2003/01/geo/"
 *        >W3C Basic Geo Vocabulary</a>
 */
public interface Geographic {

    /**
     * The WGS84 Latitude of the Point
     */
    Property LATITUDE =
        Property.internalReal("geo:lat");

    /**
     * The WGS84 Longitude of the Point
     */
    Property LONGITUDE =
        Property.internalReal("geo:long");

    /**
     * The WGS84 Altitude of the Point
     */
    Property ALTITUDE =
        Property.internalReal("geo:alt");

}
