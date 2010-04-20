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
public class SCMPPart extends SCMP {
	
	private static final long serialVersionUID = -3379254138164380850L;

	public SCMPPart() {
		super();
	}

	public SCMPPart(Map<String, String> map) {
		this.header = map;
	}

	public boolean isPart() {
		return true;
	}
	
	public void setMessageId(String messageId) {
		this.setHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID, messageId);
	}
	
	public String getMessageId() {
		return this.getHeader(SCMPHeaderAttributeKey.SCMP_MESSAGE_ID);		
	}


}