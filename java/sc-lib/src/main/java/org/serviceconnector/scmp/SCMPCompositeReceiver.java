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
package org.serviceconnector.scmp;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * The Class SCMPCompositeReceiver. Used to handle incoming large request/response. Stores parts and put them together to complete
 * request/response.
 * 
 * @author JTraber
 */
public class SCMPCompositeReceiver extends SCMPMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1735822027625330541L;
	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(SCMPCompositeReceiver.class);

	/** The list of message parts. */
	private List<SCMPMessage> scmpList;
	/** The part request, request to pull. */
	private SCMPMessage pollMsg;
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
		// SCMP Version request
		super(request.getSCMPVersion());
		this.outputStream = null;
		this.writer = null;
		this.offest = 0;
		// default compositeReceiver is not complete
		this.complete = false;
		this.header = messagePart.getHeader();
		scmpList = new ArrayList<SCMPMessage>();
		// builds up request to poll later - SCMP Version request
		pollMsg = new SCMPPart(request.getSCMPVersion(), true, messagePart.getHeader());
		pollMsg.setMessageType(request.getMessageType());
		pollMsg.setSessionId(request.getSessionId());
		pollMsg.setCacheId(request.getCacheId());
		pollMsg.setHeader(request, SCMPHeaderAttributeKey.APPENDIX_NR);
		pollMsg.setHeader(request, SCMPHeaderAttributeKey.OPERATION_TIMEOUT); // tries to set operation timeout
		pollMsg.setHeader(request, SCMPHeaderAttributeKey.SERVICE_NAME); // tries to set service name
		// necessary to download file
		pollMsg.setHeader(request, SCMPHeaderAttributeKey.REMOTE_FILE_NAME); // tries to set remote file name
		this.add(messagePart);
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, String> getHeader() {
		return pollMsg.getHeader();
	}

	/**
	 * Adds the part.
	 * 
	 * @param message
	 *            the SCMP message
	 */
	public void add(SCMPMessage message) {
		if (message == null) {
			return;
		}
		int bodyLength = message.getBodyLength();
		this.offest += bodyLength;
		this.scmpList.add(message);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isComposite() {
		return true;
	}

	/** {@inheritDoc} */
	@Override
	public int getBodyLength() {
		Object body = this.getBody();
		if (body == null) {
			return 0;
		}
		if (this.outputStream != null) {
			return this.outputStream.toByteArray().length;
		}
		if (this.writer != null) {
			return this.writer.getBuffer().length();
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
		if (this.scmpList == null || this.scmpList.size() <= 0) {
			return 0;
		}
		return this.mergePartBodies();
	}

	/**
	 * Merge part bodies.
	 * 
	 * @return the object
	 */
	private Object mergePartBodies() {
		// put all parts together to get complete body
		SCMPMessage firstScmp = scmpList.get(0);
		for (int i = 0; i < scmpList.size(); i++) {
			firstScmp = scmpList.get(i);
			if (firstScmp.getBodyLength() != 0) {
				// first scmp with body found continue
				break;
			}
		}
		if (firstScmp.isByteArray()) {
			this.outputStream = new ByteArrayOutputStream();
			try {
				for (SCMPMessage message : this.scmpList) {
					int bodyLength = message.getBodyLength();
					if (bodyLength > 0) {
						Object body = message.getBody();
						if (body == null) {
							LOGGER.warn("bodyLength > 0 but body == null");
						}
						this.outputStream.write((byte[]) body);
						this.outputStream.flush();
					}
				}
			} catch (Exception ex) {
				LOGGER.error("getBody " + ex.toString());
				return null;
			}
			return this.outputStream.toByteArray();
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
				LOGGER.error("getBody " + ex.toString());
				return null;
			}
			return this.writer.toString();
		}
		return null;
	}

	/**
	 * Write body out to given stream instance.
	 * 
	 * @param outStream
	 *            the out stream
	 */
	public void writeBodyAsStream(OutputStream outStream) {
		// put all parts together to get complete body
		SCMPMessage firstScmp = scmpList.get(0);
		if (firstScmp.isByteArray()) {
			try {
				for (SCMPMessage message : this.scmpList) {
					int bodyLength = message.getBodyLength();
					if (bodyLength > 0) {
						Object body = message.getBody();
						if (body == null) {
							LOGGER.warn("bodyLength > 0 but body == null");
						}
						outStream.write((byte[]) body);
						outStream.flush();
					}
				}
			} catch (Exception ex) {
				LOGGER.error("getBodyAsStream " + ex.toString());
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getMessageType() {
		return this.pollMsg.getMessageType();
	}

	/**
	 * Gets the poll message. Poll message to send in order to receive the next part of large message.
	 * 
	 * @return the poll message
	 */
	public SCMPMessage getPollMessage() {
		return this.pollMsg;
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
		return this.complete;
	}

	/**
	 * Incomplete.
	 */
	public void incomplete() {
		this.complete = false;
	}

	/**
	 * Complete.
	 */
	public void complete() {
		this.complete = true;
	}
}