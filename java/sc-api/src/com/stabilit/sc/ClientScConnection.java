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
import com.stabilit.sc.msg.ISCListener;
import com.stabilit.sc.service.IRequestResponseService;
import com.stabilit.sc.service.ISubscribePublishService;
import com.stabilit.sc.service.ServiceFactory;

/**
 * The Class ClientScConnection, represents a connection between client and Sc.
 * 
 * @author JTraber
 */
public class ClientScConnection extends ScConnection {

	/** The service factory. */
	private ServiceFactory serviceFactory;

	/**
	 * Instantiates a new client sc connection.
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
	public ClientScConnection(String scHost, int scPort, String connectionType, int numOfConnections) {
		super(scHost, scPort, connectionType, numOfConnections);
		serviceFactory = ServiceFactory.getInstance();
	}

	/**
	 * New request response service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param responseHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 * 
	 * @return the requestResponse service
	 */
	public IRequestResponseService newRequestResponseService(String serviceName,
			Class<? extends ISCListener> scListenerClass) {
		return serviceFactory.createRequestResponseService(serviceName, scListenerClass);
	}

	/**
	 * New subscribe publish service.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param responseHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 * 
	 * @return the subscribePublish service
	 */
	public ISubscribePublishService newSubscribePublishService(String serviceName,
			Class<? extends ISCListener> scListenerClass) {
		return serviceFactory.createSubscribePublishService(serviceName, scListenerClass);
	}

	/** {@inheritDoc} */
	@Override
	public void detach(int timeout) throws ScConnectionException {
	}
}
