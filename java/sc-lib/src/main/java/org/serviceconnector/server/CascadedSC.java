package org.serviceconnector.server;

import java.net.InetSocketAddress;

import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPSrvChangeSubscriptionCall;
import org.serviceconnector.call.SCMPSrvCreateSessionCall;
import org.serviceconnector.call.SCMPSrvDeleteSessionCall;
import org.serviceconnector.call.SCMPSrvExecuteCall;
import org.serviceconnector.call.SCMPSrvSubscribeCall;
import org.serviceconnector.call.SCMPSrvUnsubscribeCall;
import org.serviceconnector.net.connection.ConnectionPoolBusyException;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;

public class CascadedSC extends Server {

	// TODO JOT must have a requester for session service forward
	// TODO JOT must have a polling client for publish service needs

	public CascadedSC(InetSocketAddress socketAddress, String serverName, int portNr, int maxConnections,
			String connectionType, int keepAliveInterval) {
		super(ServerType.CASCADED_SC, socketAddress, serverName, portNr, maxConnections, connectionType, keepAliveInterval);
		this.serverKey = serverName;
	}

	/**
	 * Creates the session.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @throws ConnectionPoolBusyException
	 * @throws Exception
	 *             the exception
	 */
	public void createSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvCreateSessionCall createSessionCall = (SCMPSrvCreateSessionCall) SCMPCallFactory.SRV_CREATE_SESSION_CALL
				.newInstance(requester, msgToForward);
		try {
			createSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	/**
	 * Delete session.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void deleteSession(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvDeleteSessionCall deleteSessionCall = (SCMPSrvDeleteSessionCall) SCMPCallFactory.SRV_DELETE_SESSION_CALL
				.newInstance(requester, message);

		try {
			deleteSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// delete session failed
			callback.receive(e);
		}
	}

	/**
	 * Send data. Tries sending data to server asynchronous.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void execute(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) throws ConnectionPoolBusyException {
		SCMPSrvExecuteCall srvExecuteCall = (SCMPSrvExecuteCall) SCMPCallFactory.SRV_EXECUTE_CALL.newInstance(requester, message);
		try {
			srvExecuteCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception th) {
			// send data failed
			callback.receive(th);
		}
	}

	/**
	 * Subscribe.
	 * 
	 * @param msgToForward
	 *            the message to forward
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void subscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvSubscribeCall subscribeCall = (SCMPSrvSubscribeCall) SCMPCallFactory.SRV_SUBSCRIBE_CALL.newInstance(requester,
				msgToForward);
		try {
			subscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// subscribe failed
			callback.receive(e);
		}
	}

	/**
	 * Unsubscribe.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @throws ConnectionPoolBusyException
	 */
	public void unsubscribe(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvUnsubscribeCall unsubscribeCall = (SCMPSrvUnsubscribeCall) SCMPCallFactory.SRV_UNSUBSCRIBE_CALL.newInstance(
				requester, message);

		try {
			unsubscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// unsubscribe failed
			callback.receive(e);
		}
	}

	/**
	 * Change subscription.
	 * 
	 * @param message
	 *            the message
	 * @param callback
	 *            the callback
	 * @param timeoutMillis
	 *            the timeout milliseconds
	 * @throws ConnectionPoolBusyException
	 */
	public void changeSubscription(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis)
			throws ConnectionPoolBusyException {
		SCMPSrvChangeSubscriptionCall changeSubscriptionCall = (SCMPSrvChangeSubscriptionCall) SCMPCallFactory.SRV_CHANGE_SUBSCRIPTION_CALL
				.newInstance(requester, message);

		try {
			changeSubscriptionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (ConnectionPoolBusyException ex) {
			throw ex;
		} catch (Exception e) {
			// changeSubscription failed
			callback.receive(e);
		}
	}

	public void subscribe() {
		// if first subscribe & start polling else
		// change subscription to cascadedSC
	}

	public void unsubscribe() {
		// if last mask .. unsubscribe&stop polling else
		// change subscription to cascadedSC
	}

	@Override
	public void abortSession(AbstractSession session, String reason) {
		// TODO JOT subscription timeout what todo??? JAN
		// delete subscription, unsubscribe local and on cascadeSC, nothing more
	}

}
