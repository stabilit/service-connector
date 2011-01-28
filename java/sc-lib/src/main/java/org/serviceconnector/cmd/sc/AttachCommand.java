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
package org.serviceconnector.cmd.sc;

import org.apache.log4j.Logger;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.util.DateTimeUtility;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class AttachCommand. Responsible for validation and execution of attach command. Allows attaching (virtual attach) to SC.
 * 
 * @author JTraber
 */
public class AttachCommand extends CommandAdapter {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(AttachCommand.class);

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.ATTACH;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) {
		// set up response
		SCMPMessage scmpReply = new SCMPMessage();
		scmpReply.setIsReply(true);
		scmpReply.setMessageType(getKey());
		scmpReply.setHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
		response.setSCMP(scmpReply);
		// initiate responder to send reply
		responderCallback.responseCallback(request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// scVersion mandatory
			String scVersion = message.getHeader(SCMPHeaderAttributeKey.SC_VERSION);
			SCMPMessage.SC_VERSION.isSupported(scVersion);
			// localDateTime mandatory
			ValidatorUtility.validateDateTime(message.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME), SCMPError.HV_WRONG_LDT);
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException valExc = new SCMPValidatorException();
			valExc.setMessageType(getKey());
			valExc.setAttribute(SCMPHeaderAttributeKey.LOCAL_DATE_TIME, DateTimeUtility.getCurrentTimeZoneMillis());
			throw valExc;
		}
	}
}