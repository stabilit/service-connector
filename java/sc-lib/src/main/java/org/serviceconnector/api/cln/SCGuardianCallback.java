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
package org.serviceconnector.api.cln;

import org.serviceconnector.api.SCAppendMessage;
import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.api.SCRemovedMessage;
import org.serviceconnector.api.SCServiceException;
import org.serviceconnector.cache.SC_CACHING_METHOD;
import org.serviceconnector.log.PerformanceLogger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class SCGuardianServiceCallback. Responsible for handling the right communication sequence for publish subscribe protocol.
 */
class SCGuardianCallback extends SCServiceCallback {

	private SCGuardianMessageCallback guardianCallback;

	/**
	 * Instantiates a new guardian service callback.
	 * 
	 * @param service
	 *            the service
	 * @param messageCallback
	 *            the message callback
	 */
	public SCGuardianCallback(SCPublishService service, SCMessageCallback messageCallback) {
		super(service, messageCallback);
		this.guardianCallback = (SCGuardianMessageCallback) this.messageCallback;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		PerformanceLogger.end(this.service.sessionId);
		// 3. receiving reply and error handling
		if (this.service.isActive() == false) {
			// client is not subscribed anymore - stop continuing
			return;
		}
		if (reply.isFault()) {
			this.service.sessionActive = false;
			// operation failed
			SCServiceException ex = new SCServiceException("SCGuardian operation failed sid=" + this.service.sessionId);
			ex.setSCErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			super.receive(ex);
			return;
		}
		// 4. post process, reply to client
		boolean noData = reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
		if (noData == false) {
			// data reply received - pass to application
			SC_CACHING_METHOD cachingMethod = SC_CACHING_METHOD.getCachingMethod(reply
					.getHeader(SCMPHeaderAttributeKey.CACHING_METHOD));

			SCPublishMessage replyToClient = null;

			switch (cachingMethod) {
			case INITIAL:
			case NOT_MANAGED:
				replyToClient = new SCPublishMessage();
				break;
			case APPEND:
				replyToClient = new SCAppendMessage();
				break;
			case REMOVE:
				replyToClient = new SCRemovedMessage();
				break;
			}
			replyToClient.setData(reply.getBody());
			replyToClient.setDataLength(reply.getBodyLength());
			replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
			replyToClient.setSessionId(reply.getSessionId());
			replyToClient.setMask(reply.getHeader(SCMPHeaderAttributeKey.MASK));
			replyToClient.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
			replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
			replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			replyToClient.setCachingMethod(cachingMethod);
			replyToClient.setCacheId(reply.getHeader(SCMPHeaderAttributeKey.CACHE_ID));

			// inform service request is completed
			this.service.setRequestComplete();
			switch (cachingMethod) {
			case INITIAL:
			case NOT_MANAGED:
				this.guardianCallback.receive(replyToClient);
				break;
			case APPEND:
				this.guardianCallback.receiveAppendix((SCAppendMessage) replyToClient);
				break;
			case REMOVE:
				this.guardianCallback.receiveRemove((SCRemovedMessage) replyToClient);
				break;
			}
		}
		((SCPublishService) this.service).receivePublication();
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		PerformanceLogger.end(this.service.sessionId);
		if (this.service.isActive() == false) {
			// client is not subscribed anymore - stop continuing
			return;
		}
		this.service.sessionActive = false;
		super.receive(ex);
	}
}