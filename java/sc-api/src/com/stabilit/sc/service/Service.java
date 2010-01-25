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
package com.stabilit.sc.service;

import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.handler.ClientResponseHandler;
import com.stabilit.sc.handler.ClientTimeoutHandler;

/**
 * The Class Service.
 * 
 * @author JTraber
 */
public abstract class Service implements IService {

	/** The service name. */
	private String serviceName;
	
	/** The response handler. */
	private ClientResponseHandler responseHandler;
	
	/** The timeout handler. */
	private ClientTimeoutHandler timeoutHandler;
	
	/** The connection information. */
	private ConnectionInformation connectionInformation;

	/**
	 * Instantiates a new service.
	 * 
	 * @param serviceName the service name
	 * @param responseHandler the response handler
	 * @param timeoutHandler the timeout handler
	 */
	protected Service(String serviceName, ClientResponseHandler responseHandler,
			ClientTimeoutHandler timeoutHandler) {
		this.serviceName = serviceName;
		this.responseHandler = responseHandler;
		this.timeoutHandler = timeoutHandler;
	}

	/** {@inheritDoc} */
	@Override
	public void connect(int timeout, ConnectionInformation connectionInformation)
			throws ScConnectionException {
		this.connectionInformation = connectionInformation;
	}

	/** {@inheritDoc} */
	@Override
	public void disconnect(int timeout) throws ScConnectionException {
	}

	/**
	 * Gets the service name.
	 * 
	 * @return the service name
	 */
	public String getServiceName() {
		return serviceName;
	}

	/**
	 * Gets the response handler.
	 * 
	 * @return the response handler
	 */
	public ClientResponseHandler getResponseHandler() {
		return responseHandler;
	}

	/**
	 * Gets the timeout handler.
	 * 
	 * @return the timeout handler
	 */
	public ClientTimeoutHandler getTimeoutHandler() {
		return timeoutHandler;
	}

	/**
	 * Gets the connection information.
	 * 
	 * @return the connection information
	 */
	public ConnectionInformation getConnectionInformation() {
		return connectionInformation;
	}

	/**
	 * Sets the connection information.
	 * 
	 * @param connectionInformation the new connection information
	 */
	public void setConnectionInformation(ConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
	}
}
