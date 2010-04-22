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

/**
 * @author JTraber
 * 
 */
public class SCMPResponsePart extends SCMPPart {

	private int offset;
	private int size;
	private int callLength;

	public SCMPResponsePart(SCMP scmp, int offset) {
		this.offset = offset;
		this.callLength = scmp.getBodyLength();
		this.size = this.callLength - this.offset < SCMP.LARGE_MESSAGE_LIMIT ? this.callLength - this.offset
				: SCMP.LARGE_MESSAGE_LIMIT;
		this.setHeader(scmp);
		this.setHeader(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH, scmp.getBodyLength());
		this.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, offset);
		String partIdString = scmp.getHeader(SCMPHeaderAttributeKey.PART_ID);
		if (partIdString != null) {
			this.setPartId(partIdString);
		} else {
			this.setPartId(SCMPPartID.getNextAsString());
		}
		this.setBody(scmp.getBody());
	}

	@Override
	public boolean isPart() {
		return offset + size < callLength;
	}

	@Override
	public boolean isReply() {
		return true;
	}

	@Override
	public boolean isBodyOffset() {
		return true;
	}

	public void setPartId(String messageId) {
		this.setHeader(SCMPHeaderAttributeKey.PART_ID, messageId);
	}

	public String getPartId() {
		return this.getHeader(SCMPHeaderAttributeKey.PART_ID);
	}

	@Override
	public int getBodyLength() {
		return this.size;
	}
}
