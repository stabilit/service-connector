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
package org.serviceconnector.web;

import org.serviceconnector.Constants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.srv.ISCSessionServerCallback;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.util.DateTimeUtility;

/**
 * The Class SCBaseSessionServlet. Base servlet for session service servlets.
 */
public abstract class SCBaseSessionServlet extends SCBaseServlet implements ISCSessionServerCallback {

	private static final long serialVersionUID = -4306065693203937304L;

	/**
	 * Instantiates a new sC base session servlet.
	 * 
	 * @param urlPath
	 *            the URL path
	 */
	protected SCBaseSessionServlet(String urlPath) {
		super(urlPath);
	}

	@Override
	public SCMessage createSession(SCMessage message, int operationTimeoutMillis) {
		return message;
	}

	@Override
	public void deleteSession(SCMessage message, int operationTimeoutMillis) {
	}

	@Override
	public void abortSession(SCMessage message, int operationTimeoutMillis) {
	}

	@Override
	public SCMessage execute(SCMessage message, int operationTimeoutMillis) {
		return message;
	}

	protected SCMPMessage baseCreateSession(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		scMessage.setServiceName(reqMessage.getServiceName());

		// call servlet service implementation
		SCMessage scReply = ((ISCSessionServerCallback) this).createSession(scMessage, operationTimeoutMillis);

		// set up reply
		SCMPMessage reply = new SCMPMessage();
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);

		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			if (scReply.isReject()) {
				// session rejected
				reply.setHeaderFlag(SCMPHeaderAttributeKey.REJECT_SESSION);
			}
			if (scReply.getSessionInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.SESSION_INFO, scReply.getSessionInfo());
			}
		}
		reply.setSessionId(reqMessage.getSessionId());
		reply.setServiceName(serviceName);
		reply.setMessageType(reqMessage.getMessageType());
		return reply;
	}

	protected SCMPMessage baseDeleteSession(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO));
		// call servlet service implementation
		((ISCSessionServerCallback) this).deleteSession(scMessage, operationTimeoutMillis);
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(reqMessage.getMessageType());
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		return reply;
	}

	protected SCMPMessage baseAbortSession(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setSessionId(reqMessage.getSessionId());
		scMessage.setServiceName(reqMessage.getServiceName());
		// call servlet service implementation
		((ISCSessionServerCallback) this).abortSession(scMessage, operationTimeoutMillis);
		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		reply.setMessageType(reqMessage.getMessageType());
		return reply;
	}

	protected SCMPMessage baseExecute(SCMPMessage reqMessage, int operationTimeoutMillis) {
		// create scMessage
		SCMessage scMessage = new SCMessage();
		scMessage.setData(reqMessage.getBody());
		scMessage.setDataLength(reqMessage.getBodyLength());
		scMessage.setCompressed(reqMessage.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
		scMessage.setMessageInfo(reqMessage.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
		scMessage.setCacheId(reqMessage.getCacheId());
		scMessage.setCachePartNr(reqMessage.getCachePartNr());
		scMessage.setServiceName(reqMessage.getServiceName());
		scMessage.setSessionId(reqMessage.getSessionId());
		// call servlet service implementation
		SCMessage scReply = ((ISCSessionServerCallback) this).execute(scMessage, operationTimeoutMillis);

		// set up reply
		SCMPMessage reply = new SCMPMessage();
		reply.setIsReply(true);
		reply.setServiceName(serviceName);
		reply.setSessionId(reqMessage.getSessionId());
		long msgSequenceNr = this.requester.getSCMPMsgSequenceNr().incrementAndGetMsgSequenceNr();
		reply.setHeader(SCMPHeaderAttributeKey.MESSAGE_SEQUENCE_NR, msgSequenceNr);
		reply.setMessageType(reqMessage.getMessageType());
		if (scReply != null) {
			reply.setBody(scReply.getData());
			if (scReply.getCacheExpirationDateTime() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.CACHE_EXPIRATION_DATETIME,
						DateTimeUtility.getDateTimeAsString(scReply.getCacheExpirationDateTime()));
			}
			reply.setCacheId(scReply.getCacheId());
			if (scReply.getMessageInfo() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.MSG_INFO, scReply.getMessageInfo());
			}
			if (scReply.isCompressed()) {
				reply.setHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION);
			}
			if (scReply.getAppErrorCode() != Constants.EMPTY_APP_ERROR_CODE) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_CODE, scReply.getAppErrorCode());
			}
			if (scReply.getAppErrorText() != null) {
				reply.setHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT, scReply.getAppErrorText());
			}
			reply.setPartSize(scReply.getPartSize());
		}
		return reply;
	}
}
