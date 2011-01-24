package org.serviceconnector.server;

import java.net.InetSocketAddress;

import org.serviceconnector.call.SCMPCallFactory;
import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.scmp.ISCMPMessageCallback;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.AbstractSession;

public class CascadedSC extends Server {

	// TODO JOT must have a requester for session service forward
	// TODO JOT must have a polling client for publish service needs

	public CascadedSC(InetSocketAddress socketAddress, String serverName, int portNr, int maxConnections, String connectionType,
			int keepAliveInterval) {
		super(ServerType.CASCADED_SC, socketAddress, serverName, portNr, maxConnections, connectionType, keepAliveInterval);
		this.serverKey = serverName;
	}

	public void createSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL
				.newInstance(requester, msgToForward);
		try {
			createSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	public void deleteSession(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
				.newInstance(requester, message);
		try {
			deleteSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// delete session failed
			callback.receive(e);
		}
	}

	public void execute(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnExecuteCall executeCall = (SCMPClnExecuteCall) SCMPCallFactory.CLN_EXECUTE_CALL.newInstance(requester, message);
		try {
			executeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception th) {
			// send data failed
			callback.receive(th);
		}
	}

	public void subscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnSubscribeCall subscribeCall = (SCMPClnSubscribeCall) SCMPCallFactory.CLN_SUBSCRIBE_CALL.newInstance(requester,
				msgToForward);
		try {
			subscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// subscribe failed
			callback.receive(e);
		}
	}

	public void unsubscribe(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnUnsubscribeCall unsubscribeCall = (SCMPClnUnsubscribeCall) SCMPCallFactory.CLN_UNSUBSCRIBE_CALL.newInstance(
				requester, message);

		try {
			unsubscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// unsubscribe failed
			callback.receive(e);
		}
	}

	public void changeSubscription(SCMPMessage message, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = (SCMPClnChangeSubscriptionCall) SCMPCallFactory.CLN_CHANGE_SUBSCRIPTION_CALL
				.newInstance(requester, message);

		try {
			changeSubscriptionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
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
