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
import com.stabilit.sc.handler.ClientResponseHandler;
import com.stabilit.sc.handler.ClientTimeoutHandler;
import com.stabilit.sc.service.IRequestResponseService;
import com.stabilit.sc.service.ISubscribePublishService;

/**
 * @author JTraber
 * 
 */
public class ClientScConnection extends ScConnection {

	/**
	 * @param scHost
	 * @param scPort
	 * @param scProtocol
	 * @param numOfConnections
	 */
	public ClientScConnection(String scHost, int scPort, ProtocolType scProtocol,
			int numOfConnections) {
		super(scHost, scPort, scProtocol, numOfConnections);
	}

	/**
	 * creates new RequestResponseService, holds the service in the ScConnection which
	 * handles communication layer.
	 * 
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 * @return service
	 */
	public IRequestResponseService newRequestResponseService(String serviceName, ClientResponseHandler responseHandler,
			ClientTimeoutHandler timeoutHandler) {
		return serviceFactory.createRequestResponseService(serviceName, responseHandler, timeoutHandler);
	}

	/**
	 * creates new SubscribePublishService, holds the service in the ScConnection which
	 * handles communication layer.
	 * 
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 * @return service
	 */
	public ISubscribePublishService newSubscribePublishService(String serviceName, ClientResponseHandler responseHandler,
			ClientTimeoutHandler timeoutHandler) {
		return serviceFactory.createSubscribePublishService(serviceName, responseHandler, timeoutHandler);
	}

	/*
	 * Attaches a client to Sc
	 * 
	 * @see com.stabilit.sc.ScConnection#attach(int, int, int)
	 */
	@Override
	public void attach(int timeout, int keepAliveInterval, int keepAliveTimeout)
			throws ScConnectionException {

	}

	/*
	 * Detaches client from Sc
	 * 
	 * @see com.stabilit.sc.ScConnection#detach(int)
	 */
	@Override
	public void detach(int timeout) {
	}

}
