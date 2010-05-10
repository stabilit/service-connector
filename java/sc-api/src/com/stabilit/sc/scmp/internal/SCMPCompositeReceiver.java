/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 *-----------------------------------------------------------------------------*/
package com.stabilit.sc.scmp.internal;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.WarningListenerSupport;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;

/**
 * The Class SCMPCompositeReceiver. Used to handle incoming large request/response. Stores parts and put them together to
 * complete request/response.
 * 
 * @author JTraber
 */
public class SCMPCompositeReceiver extends SCMP {

	/** The scmp list, list of parts. */
	private List<SCMP> scmpList;
	/** The part request, request to pull. */
	private SCMP partRequest;
	/** The scmp fault. */
	private SCMPFault scmpFault;
	/** The scmp offset. */
	private int scmpOffset;
	/** The os. */
	private ByteArrayOutputStream os;
	/** The w. */
	private StringWriter w;

	/**
	 * Instantiates a new SCMPCompositeReceiver.
	 * 
	 * @param request
	 *            the request
	 * @param scmpPart
	 *            the scmp part
	 */
	public SCMPCompositeReceiver(SCMP request, SCMP scmpPart) {
		this.os = null;
		this.w = null;
		this.scmpOffset = 0;
		this.scmpFault = null;
		scmpList = new ArrayList<SCMP>();
		// builds up request to pull later
		partRequest = new SCMPPart();
		partRequest.setMessageType(request.getMessageType());
		partRequest.setSessionId(request.getSessionId());
		partRequest.setHeader(request, SCMPHeaderAttributeKey.SERVICE_NAME); // tries to set service name
		partRequest.setHeader(request, SCMPHeaderAttributeKey.MAX_NODES); // tries to set maxNodes
		partRequest.setHeader(scmpPart, SCMPHeaderAttributeKey.BODY_TYPE); // tries to set bodyType
		this.add(scmpPart);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getHeader()
	 */
	@Override
	public Map<String, String> getHeader() {
		partRequest.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
		return partRequest.getHeader();
	}

	/**
	 * Adds the part.
	 * 
	 * @param scmp
	 *            the scmp
	 */
	public void add(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		if (scmp.isFault()) {
			// stop pulling in case of exception
			this.scmpList.clear();
			this.scmpFault = (SCMPFault) scmp;
			reset();
		}
		int bodyLength = scmp.getBodyLength();
		this.scmpOffset += bodyLength;
		this.scmpList.add(scmp);
		if (scmp.isPart() == false) {
			// last scmp arrived, correct body length and store header
			this.setHeader(scmp.getHeader());
			this.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, getBodyLength());
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#isFault()
	 */
	@Override
	public boolean isFault() {
		if (this.scmpFault != null) {
			return true;
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#isComposite()
	 */
	@Override
	public boolean isComposite() {
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getBodyLength()
	 */
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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getBody()
	 */
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
		// put all parts together to get complete body
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
						if (body == null) {
							WarningListenerSupport.getInstance().fireWarning(this,
									"bodyLength > 0 but body == null");
						}
						this.os.write((byte[]) body);
					}
				}
				this.os.flush();
			} catch (Exception e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
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
				ExceptionListenerSupport.getInstance().fireException(this, e);
				return null;
			}
			return this.w.toString();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getMessageType()
	 */
	@Override
	public String getMessageType() {
		return partRequest.getMessageType();
	}

	/**
	 * Gets the part request.
	 * 
	 * @return the part request
	 */
	public SCMP getPartRequest() {
		return partRequest;
	}

	/**
	 * Gets the offset.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return this.scmpOffset;
	}

	/**
	 * Reset composite.
	 */
	private void reset() {
		this.partRequest = null;
		this.scmpList.clear();
		this.scmpOffset = 0;
		this.os = null;
		this.w = null;
	}
}
