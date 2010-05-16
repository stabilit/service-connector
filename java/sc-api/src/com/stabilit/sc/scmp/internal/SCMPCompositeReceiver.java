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
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;

/**
 * The Class SCMPCompositeReceiver. Used to handle incoming large request/response. Stores parts and put them together to
 * complete request/response.
 * 
 * @author JTraber
 */
public class SCMPCompositeReceiver extends SCMPMessage {

	/** The list of message parts. */
	private List<SCMPMessage> scmpList;
	/** The part request, request to pull. */
	private SCMPMessage currentPart;			// TODO currentPart should be SCMPPart
	/** The scmp fault. */
	private SCMPFault scmpFault;
	/** The scmp offset. */
	private int offest;
	/** The output stream. */
	private ByteArrayOutputStream outputStream;
	/** The string writer */
	private StringWriter writer;

	/**
	 * Instantiates a new SCMPCompositeReceiver.
	 * 
	 * @param request
	 *            the request message
	 * @param messagePart
	 *            the message part
	 */
	public SCMPCompositeReceiver(SCMPMessage request, SCMPMessage messagePart) { // TODO messagePart should be SCMPPart
		this.outputStream = null;
		this.writer = null;
		this.offest = 0;
		this.scmpFault = null;
		scmpList = new ArrayList<SCMPMessage>();
		// builds up request to pull later
		currentPart = new SCMPPart();
		currentPart.setMessageType(request.getMessageType());
		currentPart.setSessionId(request.getSessionId());
		currentPart.setHeader(request, SCMPHeaderAttributeKey.SERVICE_NAME); // tries to set service name
		currentPart.setHeader(request, SCMPHeaderAttributeKey.MAX_NODES); // tries to set maxNodes
		currentPart.setHeader(messagePart, SCMPHeaderAttributeKey.BODY_TYPE); // tries to set bodyType
		this.add(messagePart);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getHeader()
	 */
	@Override
	public Map<String, String> getHeader() {
		currentPart.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
		return currentPart.getHeader();
	}

	/**
	 * Adds the part.
	 * 
	 * @param message
	 *            the scmp message
	 */
	public void add(SCMPMessage message) {
		if (message == null) {
			return;
		}
		if (message.isFault()) {
			// stop pulling in case of exception
			this.scmpList.clear();
			this.scmpFault = (SCMPFault) message;
			reset();
		}
		int bodyLength = message.getBodyLength();
		this.offest += bodyLength;
		this.scmpList.add(message);
		if (message.isPart() == false) {
			// last message arrived, correct body length and store header
			this.setHeader(message.getHeader());
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
		if (this.outputStream != null) {
			return this.outputStream.toByteArray().length;
		}
		if (this.writer != null) {
			return this.writer.toString().length();
		}
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getBody()
	 */
	@Override
	public Object getBody() {
		if (this.outputStream != null) {
			return this.outputStream.toByteArray();
		}
		if (this.writer != null) {
			return this.writer.toString();
		}
		if (this.scmpFault != null) {
			return scmpFault.getBody();
		}
		// put all parts together to get complete body
		SCMPMessage firstScmp = this.scmpList.get(0);
		if (firstScmp == null) {
			return 0;
		}
		if (firstScmp.isByteArray()) {
			this.outputStream = new ByteArrayOutputStream();
			try {
				for (SCMPMessage message : this.scmpList) {
					int bodyLength = message.getBodyLength();
					if (bodyLength > 0) {
						Object body = message.getBody();
						if (body == null) {
							WarningListenerSupport.getInstance().fireWarning(this,
									"bodyLength > 0 but body == null");
						}
						this.outputStream.write((byte[]) body);
					}
				}
				this.outputStream.flush();
			} catch (Exception e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				return null;
			}
			this.outputStream.toByteArray();
		}
		if (firstScmp.isString()) {
			this.writer = new StringWriter();
			try {
				for (SCMPMessage message : this.scmpList) {
					int bodyLength = message.getBodyLength();
					if (bodyLength > 0) {
						Object body = message.getBody();
						this.writer.write((String) body);
					}
				}
				this.writer.flush();
			} catch (Exception e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				return null;
			}
			return this.writer.toString();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.SCMP#getMessageType()
	 */
	@Override
	public String getMessageType() {
		return currentPart.getMessageType();
	}

	/**
	 * Gets the part request.
	 * 
	 * @return the part request
	 */
	public SCMPMessage getPart() {
		return currentPart;
	}

	/**
	 * Gets the offset.
	 * 
	 * @return the offset
	 */
	public int getOffset() {
		return this.offest;
	}

	/**
	 * Reset composite.
	 */
	private void reset() {
		this.currentPart = null;
		this.scmpList.clear();
		this.offest = 0;
		this.outputStream = null;
		this.writer = null;
	}
}
