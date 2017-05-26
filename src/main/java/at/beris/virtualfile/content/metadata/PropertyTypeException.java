/*
 * This file is part of VirtualFile.
 *
 * Copyright 2017 by Bernd Riedl <bernd.riedl@gmail.com>
 *
 * Licensed under GNU Lesser General Public License 3.0 or later.
 * Some rights reserved. See COPYING, AUTHORS.
 */
package at.beris.virtualfile.content.metadata;

import at.beris.virtualfile.content.metadata.Property.PropertyType;
import at.beris.virtualfile.content.metadata.Property.ValueType;


/**
 * XMP property definition violation exception. This is thrown when
 * you try to set a {@link Property} value with an incorrect type,
 * such as storing an Integer when the property is of type Date.
 *
 * @since Apache Tika 0.8
 */
public final class PropertyTypeException extends IllegalArgumentException {

    public PropertyTypeException(String msg) {
        super(msg);
    }

    public PropertyTypeException(PropertyType expected, PropertyType found) {
        super("Expected a property of type " + expected + ", but received " + found);
    }

    public PropertyTypeException(ValueType expected, ValueType found) {
        super("Expected a property with a " + expected + " value, but received a " + found);
    }

    public PropertyTypeException(PropertyType unsupportedPropertyType) {
        super((unsupportedPropertyType != PropertyType.COMPOSITE)
                ? unsupportedPropertyType + " is not supported"
                : "Composite Properties must not include other Composite"
                   + " Properties as either Primary or Secondary");
    }
}
