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

import com.stabilit.sc.msg.ISCListener;

/**
 * The ServiceFactory creates Services for clients.
 * 
 * @author JTraber
 */
public final class ServiceFactory {

	/** The singleton instance. */
	private static ServiceFactory factory = new ServiceFactory();

	/**
	 * Instantiates a new service factory.
	 */
	private ServiceFactory() {
	}

	/**
	 * Gets the single instance of ServiceFactory.
	 * 
	 * @return single instance of ServiceFactory
	 */
	public static ServiceFactory getInstance() {
		return factory;
	}

	/**
	 * Creates a new RequestResponseService object.
	 * 
	 * @param serviceName the service name
	 * @param responseHandler the response handler
	 * @param timeoutHandler the timeout handler
	 * 
	 * @return the requestresponse service
	 */
	public IRequestResponseService createRequestResponseService(String serviceName,
			Class<? extends ISCListener> scListenerClass) {
		return new RequestResponseService(serviceName, scListenerClass);
	}

	/**
	 * Creates a new SubscribePublishService object.
	 * 
	 * @param serviceName the service name
	 * @param responseHandler the response handler
	 * @param timeoutHandler the timeout handler
	 * 
	 * @return the subscribepublish service
	 */
	public ISubscribePublishService createSubscribePublishService(String serviceName,
			Class<? extends ISCListener> scListenerClass) {
		return new SubscribePublishService(serviceName, scListenerClass);
	}
}
