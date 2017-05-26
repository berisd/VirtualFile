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
 * Met Keys used by the {@link MimeTypesReader}.
 */
public interface MimeTypesReaderMetKeys {

    String MIME_INFO_TAG = "mime-info";

    String MIME_TYPE_TAG = "mime-type";

    String MIME_TYPE_TYPE_ATTR = "type";

    String ACRONYM_TAG = "acronym";

    String COMMENT_TAG = "_comment";

    String GLOB_TAG = "glob";

    String ISREGEX_ATTR = "isregex";

    String PATTERN_ATTR = "pattern";

    String MAGIC_TAG = "magic";

    String ALIAS_TAG = "alias";

    String ALIAS_TYPE_ATTR = "type";

    String ROOT_XML_TAG = "root-XML";

    String SUB_CLASS_OF_TAG = "sub-class-of";

    String SUB_CLASS_TYPE_ATTR = "type";

    String MAGIC_PRIORITY_ATTR = "priority";

    String MATCH_TAG = "match";

    String MATCH_OFFSET_ATTR = "offset";

    String MATCH_TYPE_ATTR = "type";

    String MATCH_VALUE_ATTR = "value";

    String MATCH_MASK_ATTR = "mask";

    String NS_URI_ATTR = "namespaceURI";

    String LOCAL_NAME_ATTR = "localName";

    String TIKA_LINK_TAG = "tika:link";
    
    String TIKA_UTI_TAG = "tika:uti";
}
