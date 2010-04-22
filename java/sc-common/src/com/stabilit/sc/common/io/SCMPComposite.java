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

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
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
	private SCMPFault scmpFault;
	private int scmpOffset;
	private ByteArrayOutputStream os;
	private StringWriter w;
	
	public SCMPComposite(SCMP request, SCMPPart scmpPart) {
		this.os = null;
		this.w = null;
		this.scmpOffset = 0;
		this.scmpFault = null;
		scmpList = new ArrayList<SCMP>();		
		partRequest = new SCMPPart();		
		String messageId = scmpPart.getPartId();		
		partRequest.setMessageType(request.getMessageType());
		partRequest.setPartId(messageId);
		partRequest.setSessionId(request.getSessionId());		
		partRequest.setHeader(request, SCMPHeaderAttributeKey.SCMP_BODY_TYPE);  // tries to set service name if any
		partRequest.setHeader(request, SCMPHeaderAttributeKey.SERVICE_NAME);  // tries to set service name if any
		partRequest.setHeader(request, SCMPHeaderAttributeKey.SCMP_OFFSET);  // tries to set sequence number if any
		this.add(scmpPart);
	}

	@Override
	public Map<String, String> getHeader() {
		partRequest.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
		return partRequest.getHeader();
	}
			
	public void add(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		if (scmp.isFault()) {
			this.scmpList.clear();
			this.scmpFault = (SCMPFault) scmp;
			reset();
		}
		int bodyLength = scmp.getBodyLength();
		this.scmpOffset += bodyLength;
		this.scmpList.add(scmp);
		partRequest.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, String.valueOf(this.scmpOffset));
		if (scmp.isPart() == false) {
			this.setHeader(scmp.getHeader());
			this.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, getBodyLength());
		}
	}

	@Override
	public boolean isFault() {
		if (this.scmpFault != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isComposite() {
		return true;
	}

	@Override
	public int getBodyLength() {
		if (this.scmpFault != null) {
			return scmpFault.getBodyLength();
		}
		Object body = this.getBody();
		if (body == null) {
			return 0;
		}
		if (this.os != null) {
			return this.os.toByteArray().length;
		}
		if (this.w != null) {
			return this.w.toString().length();
		}
		return 0;
	}

	@Override
	public Object getBody() {
		if (this.os != null) {
			return this.os.toByteArray();
		}
		if (this.w != null) {
			return this.w.toString();
		}
		if (this.scmpFault != null) {
			return scmpFault.getBody();
		}
		SCMP firstScmp = this.scmpList.get(0);
		if (firstScmp == null) {
			return 0;
		}
		if (firstScmp.isByteArray()) {
			this.os = new ByteArrayOutputStream();
			try {
				for (SCMP scmp : this.scmpList) {
					int bodyLength = scmp.getBodyLength();
					if (bodyLength > 0) {
						Object body = scmp.getBody();
						this.os.write((byte[]) body);
					}
				}
				this.os.flush();
			} catch (Exception e) {
				return null;
			}
			this.os.toByteArray();
		}
		if (firstScmp.isString()) {
			this.w = new StringWriter();
			try {
				for (SCMP scmp : this.scmpList) {
					int bodyLength = scmp.getBodyLength();
					if (bodyLength > 0) {
						Object body = scmp.getBody();
						this.w.write((String) body);
					}
				}
				this.w.flush();
			} catch (Exception e) {
				return null;
			}
			return this.w.toString();
		}
		return null;
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

	private void reset() {
		this.partRequest = null;
		this.scmpList.clear();
		this.scmpOffset = 0;
		this.os = null;
		this.w = null;
	}

}
