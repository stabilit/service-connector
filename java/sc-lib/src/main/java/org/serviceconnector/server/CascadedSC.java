package org.serviceconnector.server;

import java.net.InetSocketAddress;

import org.serviceconnector.call.SCMPClnChangeSubscriptionCall;
import org.serviceconnector.call.SCMPClnCreateSessionCall;
import org.serviceconnector.call.SCMPClnDeleteSessionCall;
import org.serviceconnector.call.SCMPClnExecuteCall;
import org.serviceconnector.call.SCMPClnSubscribeCall;
import org.serviceconnector.call.SCMPClnUnsubscribeCall;
import org.serviceconnector.call.SCMPEchoCall;
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
		SCMPClnCreateSessionCall createSessionCall = new SCMPClnCreateSessionCall(requester, msgToForward);
		try {
			createSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	public void deleteSession(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnDeleteSessionCall deleteSessionCall = new SCMPClnDeleteSessionCall(requester, msgToForward);
		try {
			deleteSessionCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// delete session failed
			callback.receive(e);
		}
	}

	public void execute(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnExecuteCall executeCall = new SCMPClnExecuteCall(requester, msgToForward);
		try {
			executeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception th) {
			// send data failed
			callback.receive(th);
		}
	}
	
	public void echo(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPEchoCall echoCall = new SCMPEchoCall(requester, msgToForward);
		try {
			echoCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// create session failed
			callback.receive(e);
		}
	}

	public void subscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnSubscribeCall subscribeCall = new SCMPClnSubscribeCall(requester, msgToForward);
		try {
			subscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// subscribe failed
			callback.receive(e);
		}
	}

	public void unsubscribe(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnUnsubscribeCall unsubscribeCall = new SCMPClnUnsubscribeCall(requester, msgToForward);

		try {
			unsubscribeCall.invoke(callback, (int) (this.operationTimeoutMultiplier * timeoutMillis));
		} catch (Exception e) {
			// unsubscribe failed
			callback.receive(e);
		}
	}

	public void changeSubscription(SCMPMessage msgToForward, ISCMPMessageCallback callback, int timeoutMillis) {
		SCMPClnChangeSubscriptionCall changeSubscriptionCall = new SCMPClnChangeSubscriptionCall(requester, msgToForward);

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
