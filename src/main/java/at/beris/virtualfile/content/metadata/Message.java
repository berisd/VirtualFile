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
 * A collection of Message related property names.
 */
public interface Message {
    String MESSAGE_PREFIX = "Message"+ Metadata.NAMESPACE_PREFIX_DELIMITER;

    String MESSAGE_RAW_HEADER_PREFIX = MESSAGE_PREFIX+"Raw-Header"+ Metadata.NAMESPACE_PREFIX_DELIMITER;

    String MESSAGE_RECIPIENT_ADDRESS = "Message-Recipient-Address";
    
    String MESSAGE_FROM = "Message-From";
    
    String MESSAGE_TO = "Message-To";
    
    String MESSAGE_CC = "Message-Cc";
    
    String MESSAGE_BCC = "Message-Bcc";
}
