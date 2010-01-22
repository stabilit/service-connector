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
package com.stabilit.sc.service;

import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.handler.ServiceResponseHandler;
import com.stabilit.sc.handler.ServiceTimeoutHandler;

/**
 * @author JTraber
 *
 */
public abstract class Service {
	
	private String serviceName;
	private ServiceResponseHandler responseHandler;
	private ServiceTimeoutHandler timeoutHandler;
	private ConnectionInformation connectionInformation;

	protected Service(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		this.serviceName = serviceName;
		this.responseHandler = responseHandler;
		this.timeoutHandler = timeoutHandler;
	}	
	
	/*
	 * connect to service
	 */
	public void connect(int timeout, ConnectionInformation connectionInformation) throws ServiceException {
		this.connectionInformation = connectionInformation;
	}

	/*
	 * disconnect to service
	 */
	public void disconnect(int timeout) throws ServiceException {
	}
	
	public String getServiceName() {
		return serviceName;
	}

	public ServiceResponseHandler getResponseHandler() {
		return responseHandler;
	}

	public ServiceTimeoutHandler getTimeoutHandler() {
		return timeoutHandler;
	}

	public ConnectionInformation getConnectionInformation() {
		return connectionInformation;
	}

	public void setConnectionInformation(ConnectionInformation connectionInformation) {
		this.connectionInformation = connectionInformation;
	}	
}
