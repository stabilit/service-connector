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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author JTraber
 * 
 */
public class SCMPComposite extends SCMP {

	private List<SCMP> scmpList;
	private SCMPPart partRequest;
	private int scmpOffset;

	public SCMPComposite(SCMP request, SCMPPart scmpPart) {
		this.scmpOffset = 0;
		scmpList = new ArrayList<SCMP>();
		scmpList.add(scmpPart);		
		String messageId = scmpPart.getMessageId();
		partRequest = new SCMPPart();
		partRequest.setMessageType(request.getMessageType());
		partRequest.setMessageId(messageId);
		partRequest.setSessionId(request.getSessionId());
		String serviceName = request.getHeader(SCMPHeaderType.SERVICE_NAME.getName());
		partRequest.setHeader(SCMPHeaderType.SERVICE_NAME.getName(), serviceName);
		this.scmpOffset = scmpPart.getHeaderInt(SCMPHeaderType.SCMP_OFFSET.getName());
		this.add(scmpPart);
		partRequest.setHeader(SCMPHeaderType.SCMP_OFFSET.getName(),scmpOffset);
	}
	
	@Override
	public Map<String, String> getHeader() {
		return partRequest.getHeader();
	}
	
	public void add (SCMP scmp) {
		if (scmp == null) {
			return;
		}
		int bodyLength = scmp.getBodyLength();
		this.scmpOffset += bodyLength;
		this.scmpList.add(scmp);
		partRequest.setHeader(SCMPHeaderType.SCMP_OFFSET.getName(), String.valueOf(this.scmpOffset));
	}
	
	@Override
	public boolean isComposite() {
        return true;		
	}
	
	@Override
	public String getMessageType() {
	   return partRequest.getMessageType();
	}
	
	public SCMPPart getPartRequest() {
		return partRequest;
	}

	public int getOffset() {
		return this.scmpOffset;
	}

}
