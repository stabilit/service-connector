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
package com.stabilit.scm.common.scmp.internal;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.LoggerPoint;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class SCMPCompositeReceiver. Used to handle incoming large request/response. Stores parts and put them together
 * to complete request/response.
 * 
 * @author JTraber
 */
public class SCMPCompositeReceiver extends SCMPMessage {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPCompositeReceiver.class);
	
	/** The list of message parts. */
	private List<SCMPMessage> scmpList;
	/** The part request, request to pull. */
	private SCMPMessage currentPart;
	/** The scmp fault. */
	private SCMPFault scmpFault;
	/** The scmp offset. */
	private int offest;
	/** The output stream. */
	private ByteArrayOutputStream outputStream;
	/** The string writer. */
	private StringWriter writer;
	/** The complete flag. */
	private boolean complete;

	/**
	 * Instantiates a new SCMPCompositeReceiver.
	 * 
	 * @param request
	 *            the request message
	 * @param messagePart
	 *            the message part
	 */
	public SCMPCompositeReceiver(SCMPMessage request, SCMPMessage messagePart) {
		this.outputStream = null;
		this.writer = null;
		this.offest = 0;
		this.scmpFault = null;
		// default compositeReceiver is not complete
		this.complete = false;
		scmpList = new ArrayList<SCMPMessage>();
		// builds up request to pull later
		currentPart = new SCMPPart();
		currentPart.setMessageType(request.getMessageType());
		currentPart.setSessionId(request.getSessionId());
		currentPart.setHeader(request, SCMPHeaderAttributeKey.OP_TIMEOUT); // tries to set operation timeout
		currentPart.setHeader(request, SCMPHeaderAttributeKey.SERVICE_NAME); // tries to set service name
		currentPart.setHeader(messagePart, SCMPHeaderAttributeKey.BODY_TYPE); // tries to set bodyType
		this.add(messagePart);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, String> getHeader() {
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
			// last message arrived
			this.setHeader(message.getHeader());
		}
	}

	/** {@inheritDoc} */
	@Override
	public boolean isFault() {
		if (this.scmpFault != null) {
			return true;
		}
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isComposite() {
		return true;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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
		if (this.scmpList == null || this.scmpList.size() <= 0) {
			return 0;
		}
		// put all parts together to get complete body
		SCMPMessage firstScmp = scmpList.get(0);
		if (firstScmp.isByteArray()) {
			this.outputStream = new ByteArrayOutputStream();
			try {
				for (SCMPMessage message : this.scmpList) {
					int bodyLength = message.getBodyLength();
					if (bodyLength > 0) {
						Object body = message.getBody();
						if (body == null) {
							logger.warn("bodyLength > 0 but body == null");
						}
						this.outputStream.write((byte[]) body);
					}
				}
				this.outputStream.flush();
			} catch (Exception ex) {
				logger.error("getBody "+ex.getMessage(), ex);
				ExceptionPoint.getInstance().fireException(this, ex);
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
			} catch (Exception ex) {
				logger.error("getBody "+ex.getMessage(), ex);
				ExceptionPoint.getInstance().fireException(this, ex);
				return null;
			}
			return this.writer.toString();
		}
		return null;
	}

	/** {@inheritDoc} */
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
	 * @return the complete
	 */
	public boolean isComplete() {
		return complete;
	}

	public void uncomplete() {
		this.complete = false;
	}

	public void complete() {
		this.complete = true;
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
