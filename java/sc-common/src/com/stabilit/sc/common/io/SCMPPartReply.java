/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.sc.common.io;

import java.util.Map;

/**
 * @author JTraber
 * 
 */
public class SCMPPartReply extends SCMPPart {

	public SCMPPartReply() {
		super();
	}

	public SCMPPartReply(Map<String, String> map) {
		this.header = map;
	}

	public boolean isReply() {
		return true;
	}
	
	public void setMessageId(String messageId) {
		this.setHeader(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName(), messageId);
	}
	
	public String getMessageId() {
		return this.getHeader(SCMPHeaderAttributeType.SCMP_MESSAGE_ID.getName());		
	}


}