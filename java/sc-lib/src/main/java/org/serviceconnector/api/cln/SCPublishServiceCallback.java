package org.serviceconnector.api.cln;

import org.serviceconnector.api.SCPublishMessage;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.SCServiceException;

/**
 * The Class SCPublishServiceCallback. Responsible for handling the right communication sequence for publish subscribe protocol.
 */
class SCPublishServiceCallback extends SCServiceCallback {

	/**
	 * Instantiates a new publish service callback.
	 * 
	 * @param messageCallback
	 *            the message callback
	 */
	public SCPublishServiceCallback(SCPublishService service, SCMessageCallback messageCallback) {
		super(service, messageCallback);
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		// 3. receiving reply and error handling
		if (this.service.isActive() == false) {
			// client is not subscribed anymore - stop continuing
			return;
		}
		if (reply.isFault()) {
			// operation failed
			SCServiceException ex = new SCServiceException("SCPublishService operation failed");
			ex.setSCMPError(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_CODE));
			ex.setSCMPDetailErrorText(reply.getHeader(SCMPHeaderAttributeKey.SC_ERROR_TEXT));
			super.receive(ex);
			return;
		}
		// 4. post process, reply to client
		boolean noData = reply.getHeaderFlag(SCMPHeaderAttributeKey.NO_DATA);
		if (noData == false) {
			// data reply received - give to application
			SCPublishMessage replyToClient = new SCPublishMessage();
			replyToClient.setData(reply.getBody());
			replyToClient.setCompressed(reply.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION));
			replyToClient.setSessionId(reply.getSessionId());
			try {
				replyToClient.setMask(reply.getHeader(SCMPHeaderAttributeKey.MASK));
				replyToClient.setMessageInfo(reply.getHeader(SCMPHeaderAttributeKey.MSG_INFO));
				replyToClient.setAppErrorCode(reply.getHeaderInt(SCMPHeaderAttributeKey.APP_ERROR_CODE));
				replyToClient.setAppErrorText(reply.getHeader(SCMPHeaderAttributeKey.APP_ERROR_TEXT));
			} catch (SCMPValidatorException ex) {
				logger.warn("attributes invalid when setting in scmessage");
			}
			// inform service request is completed
			this.service.setRequestComplete();
			this.messageCallback.receive(replyToClient);
		}
		((SCPublishService)this.service).receivePublication();
	}
}