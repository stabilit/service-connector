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
import com.stabilit.sc.msg.IResponseMessage;

/**
 * @author JTraber
 * 
 */
public class PublishService extends Service implements IPublishService {

	/**
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 */
	protected PublishService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		super(serviceName, responseHandler, timeoutHandler);
	}

	/*
	 * publish
	 * 
	 * @see
	 * com.stabilit.sc.IPublishService#publish(com.stabilit.sc.SubscriptionMask,
	 * int)
	 */
	@Override
	public void publish(IResponseMessage responseMessage, SubscriptionMask subscriptionMask,
			int timeout) {
	}
}
