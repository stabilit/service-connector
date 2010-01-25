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
/**
 * 
 */
package com.stabilit.sc;

import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.handler.ServerResponseHandler;
import com.stabilit.sc.handler.ServerTimeoutHandler;

/**
 * @author JTraber
 * 
 */
public class ServerScConnection extends ScConnection {

	/**
	 * @param scHost
	 * @param scPort
	 * @param scProtocol
	 * @param numOfConnections
	 */
	public ServerScConnection(String scHost, int scPort, ProtocolType scProtocol, int numOfConnections) {
		super(scHost, scPort, scProtocol, numOfConnections);
	}

	/**
	 * register new publishService, holds the service in the ScConnection which
	 * handles communication layer.
	 * 
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 * @return service
	 */
	public void register(String serviceName,
			ServerResponseHandler responseHandler, ServerTimeoutHandler timeoutHandler) {
	}

	/*
	 * Attaches a server to Sc
	 * 
	 * @see com.stabilit.sc.ScConnection#attach(int, int, int)
	 */
	@Override
	public void attach(int timeout, int keepAliveInterval, int keepAliveTimeout) throws ScConnectionException {
	}

	/*
	 * Detaches server from Sc
	 * 
	 * @see com.stabilit.sc.ScConnection#detach(int)
	 */
	@Override
	public void detach(int timeout) {
	}
}
