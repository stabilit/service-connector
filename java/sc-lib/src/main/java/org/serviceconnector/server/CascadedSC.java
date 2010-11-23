package org.serviceconnector.server;

import java.net.InetSocketAddress;

import org.serviceconnector.service.AbstractSession;

public class CascadedSC extends Server {

	// TODO JOT must have a requester for session service forward
	// TODO JOT must have a polling client for publish service needs

	public CascadedSC(ServerType type, InetSocketAddress socketAddress, String serviceName, int portNr, int maxConnections,
			String connectionType, int keepAliveInterval) {
		super(type, socketAddress, serviceName, portNr, maxConnections, connectionType, keepAliveInterval);
	}

	public void forwardSyncron() {
	}

	public void forwardAsynchron() {
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
	public void abortSession(AbstractSession session) {
		// TODO JOT subscription timeout what todo??? JAN
		// delete subscription, unsubscribe local and on cascadeSC, nothing more
	}

}
