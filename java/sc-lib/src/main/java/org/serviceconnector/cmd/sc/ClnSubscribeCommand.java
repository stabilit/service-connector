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
import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPCommandException;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMsgType;
import org.serviceconnector.service.NoFreeServerException;
import org.serviceconnector.service.PublishService;
import org.serviceconnector.service.Subscription;
import org.serviceconnector.service.SubscriptionMask;
import org.serviceconnector.util.ValidatorUtility;

/**
 * The Class ClnSubscribeCommand. Responsible for validation and execution of subscribe command. Allows subscribing to a publish
 * service.
 * 
 * @author JTraber
 */
public class ClnSubscribeCommand extends CommandAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ClnSubscribeCommand.class);

	/**
	 * Instantiates a ClnSubscribeCommand.
	 */
	public ClnSubscribeCommand() {
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return SCMPMsgType.CLN_SUBSCRIBE;
	}

	/** {@inheritDoc} */
	@Override
	public void run(IRequest request, IResponse response, IResponderCallback responderCallback) throws Exception {
		SCMPMessage reqMessage = request.getMessage();
		String serviceName = reqMessage.getServiceName();
		String mask = reqMessage.getHeader(SCMPHeaderAttributeKey.MASK);
		// check service is present
		PublishService service = this.validatePublishService(serviceName);

		SubscriptionMask subscriptionMask = new SubscriptionMask(mask);
		// enhance ipAddressList
		String ipAddressList = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
		ipAddressList = ipAddressList + request.getRemoteSocketAddress().getAddress();
		reqMessage.setHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST, ipAddressList);
		String sessionInfo = (String) reqMessage.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
		int noi = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
		reqMessage.removeHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
		// create subscription
		Subscription subscription = new Subscription(subscriptionMask, sessionInfo, ipAddressList, noi);
		reqMessage.setSessionId(subscription.getId());


		ClnSubscribeCommandCallback callback = null;
		int oti = reqMessage.getHeaderInt(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);

		int tries = (int) ((oti * basicConf.getOperationTimeoutMultiplier()) / Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
		// Following loop implements the wait mechanism in case of a busy connection pool
		int i = 0;
		int otiOnServerMillis = 0;
		do {
			callback = new ClnSubscribeCommandCallback(request, response, responderCallback, subscription);
			try {
				otiOnServerMillis = oti - (i * Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
				service.allocateServerAndSubscribe(reqMessage, callback, subscription, otiOnServerMillis);
				// no exception has been thrown - get out of wait loop
				break;
			} catch (NoFreeServerException ex) {
				logger.warn("NoFreeServerException caught in wait mec of subscribe");
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					throw ex;
				}
			} catch (ConnectionPoolBusyException ex) {
				logger.warn("ConnectionPoolBusyException caught in wait mec of subscribe");
				if (i >= (tries - 1)) {
					// only one loop outstanding - don't continue throw current exception
					logger.warn(SCMPError.NO_FREE_CONNECTION.getErrorText("service=" + reqMessage.getServiceName()));
					SCMPCommandException scmpCommandException = new SCMPCommandException(SCMPError.NO_FREE_CONNECTION, "service="
							+ reqMessage.getServiceName());
					scmpCommandException.setMessageType(this.getKey());
					throw scmpCommandException;
				}
			}
			// sleep for a while and then try again
			Thread.sleep(Constants.WAIT_FOR_BUSY_CONNECTION_INTERVAL_MILLIS);
		} while (++i < tries);
	}

	/** {@inheritDoc} */
	@Override
	public void validate(IRequest request) throws Exception {
		SCMPMessage message = request.getMessage();

		try {
			// msgSequenceNr mandatory
			String msgSequenceNr = message.getMessageSequenceNr();
			ValidatorUtility.validateLong(1, msgSequenceNr, SCMPError.HV_WRONG_MESSAGE_SEQUENCE_NR);
			// serviceName mandatory
			String serviceName = message.getServiceName();
			ValidatorUtility.validateStringLength(1, serviceName, 32, SCMPError.HV_WRONG_SERVICE_NAME);
			// operation timeout mandatory
			String otiValue = message.getHeader(SCMPHeaderAttributeKey.OPERATION_TIMEOUT);
			ValidatorUtility.validateInt(1000, otiValue, 3600000, SCMPError.HV_WRONG_OPERATION_TIMEOUT);
			// ipAddressList mandatory
			String ipAddressList = (String) message.getHeader(SCMPHeaderAttributeKey.IP_ADDRESS_LIST);
			ValidatorUtility.validateIpAddressList(ipAddressList);
			// mask mandatory
			String mask = (String) message.getHeader(SCMPHeaderAttributeKey.MASK);
			ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
			// noDataInterval mandatory
			String noDataIntervalValue = message.getHeader(SCMPHeaderAttributeKey.NO_DATA_INTERVAL);
			ValidatorUtility.validateInt(10, noDataIntervalValue, 3600, SCMPError.HV_WRONG_NODATA_INTERVAL);
			// sessionInfo is optional
			String sessionInfo = (String) message.getHeader(SCMPHeaderAttributeKey.SESSION_INFO);
			ValidatorUtility.validateStringLengthIgnoreNull(1, sessionInfo, 256, SCMPError.HV_WRONG_SESSION_INFO);
			// cascadedMask & cascadedSubscriptionId will not be validated, sent from another SC
		} catch (HasFaultResponseException ex) {
			// needs to set message type at this point
			ex.setMessageType(getKey());
			throw ex;
		} catch (Throwable th) {
			logger.error("validation error", th);
			SCMPValidatorException validatorException = new SCMPValidatorException();
			validatorException.setMessageType(getKey());
			throw validatorException;
		}
	}
}