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

import com.stabilit.sc.handler.ServiceResponseHandler;
import com.stabilit.sc.handler.ServiceTimeoutHandler;
import com.stabilit.sc.service.Service;

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
	public ClientScConnection(String scHost, int scPort, String scProtocol, int numOfConnections) {
		super(scHost, scPort, scProtocol, numOfConnections);
	}

	/**
	 * creates new sendService, holds the service in the ScConnection which
	 * handles communication layer.
	 * 
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 * @return service
	 */
	public Service newSendService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		return serviceFactory.createSendService(serviceName, responseHandler, timeoutHandler);
	}
	
	/**
	 * creates new publishService, holds the service in the ScConnection which
	 * handles communication layer.
	 * 
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 * @return service
	 */
	public Service newPublishService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		return serviceFactory.createSubscribeService(serviceName, responseHandler, timeoutHandler);
	}

	/*
	 * Attaches a client to Sc
	 * 
	 * @see com.stabilit.sc.ScConnection#attach(int, int, int)
	 */
	@Override
	public void attach(int timeout, int keepAliveInterval, int keepAliveTimeout) {
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
