/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
package com.stabilit.sc;

import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.handler.ServerResponseHandler;
import com.stabilit.sc.handler.ServerTimeoutHandler;
import com.stabilit.sc.msg.IMessage;

/**
 * The Class ServerScConnection, represents a connection between a Server and a Sc.
 * 
 * @author JTraber
 */
public class ServerScConnection extends ScConnection {

	/**
	 * The Constructor.
	 * 
	 * @param scHost
	 *            the sc host
	 * @param scPort
	 *            the sc port
	 * @param scProtocol
	 *            used protocol
	 * @param numOfConnections
	 *            the number of connections used by Sc
	 */
	public ServerScConnection(String scHost, int scPort, ProtocolType scProtocol, int numOfConnections) {
		super(scHost, scPort, scProtocol, numOfConnections);
	}

	/**
	 * register new publishService, holds the service in the ScConnection which handles communication layer.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param responseHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 */
	public void register(String serviceName, ServerResponseHandler responseHandler,
			ServerTimeoutHandler timeoutHandler) {
	}

	/**
	 * Publish message.
	 * 
	 * @param responseMessage
	 *            the response message
	 * @param compression
	 *            the compression
	 * 
	 * @throws ScConnectionException
	 *             connection exception in publish process
	 */
	public void publish(IMessage responseMessage, boolean compression) throws ScConnectionException {
	}

	/** {@inheritDoc} */
	@Override
	public void attach(int timeout, int keepAliveInterval, int keepAliveTimeout) throws ScConnectionException {
	}

	/** {@inheritDoc} */
	@Override
	public void detach(int timeout) {
	}
}
