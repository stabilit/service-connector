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

import com.stabilit.sc.handler.ServiceResponseHandler;
import com.stabilit.sc.handler.ServiceTimeoutHandler;

/**
 * @author JTraber
 * 
 */
public class ServiceFactory {

	public static ServiceFactory factory = new ServiceFactory();

	private ServiceFactory() {
	}
	
	public static ServiceFactory getInstance() {
		return factory;
	}

	public ISendService createSendService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		return new SendService(serviceName, responseHandler, timeoutHandler);
	}

	public IPublishService createPublishService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		return new PublishService(serviceName, responseHandler, timeoutHandler);
	}
	
	public ISubscribeService createSubscribeService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		return new SubscribeService(serviceName, responseHandler, timeoutHandler);
	}
}
