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
import com.stabilit.sc.msg.IRequestMessage;
import com.stabilit.sc.msg.IResponseMessage;

/**
 * @author JTraber
 * 
 */
public class SendService extends Service implements ISendService {

	/**
	 * @param serviceName
	 * @param responseHandler
	 * @param timeoutHandler
	 */
	protected SendService(String serviceName, ServiceResponseHandler responseHandler,
			ServiceTimeoutHandler timeoutHandler) {
		super(serviceName, responseHandler, timeoutHandler);
	}

	/*
	 * sends msg
	 * 
	 * @see
	 * com.stabilit.sc.service.ISendService#send(com.stabilit.sc.msg.IRequestMessage
	 * , int)
	 */
	@Override
	public void send(IRequestMessage requestMessage, int timeout) {

	}

	/*
	 * sends and recieves msg
	 * 
	 * @see
	 * com.stabilit.sc.service.ISendService#sendAndReceive(com.stabilit.sc.msg
	 * .IRequestMessage, int)
	 */
	@Override
	public IResponseMessage sendAndReceive(IRequestMessage requestMessage, int timeout) {
		return null;
	}
}
