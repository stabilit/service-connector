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

import com.stabilit.sc.handler.ClientResponseHandler;
import com.stabilit.sc.handler.ClientTimeoutHandler;
import com.stabilit.sc.msg.IMessage;

/**
 * RequestResponseService.
 * 
 * @author JTraber
 */
public class RequestResponseService extends Service implements IRequestResponseService {

	/**
	 * Instantiates a RequestResponseService.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param responseHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 */
	protected RequestResponseService(String serviceName, ClientResponseHandler responseHandler,
			ClientTimeoutHandler timeoutHandler) {
		super(serviceName, responseHandler, timeoutHandler);
	}

	/** {@inheritDoc} */
	@Override
	public void send(IMessage message, int timeout, boolean compression) {
	}

	/** {@inheritDoc} */
	@Override
	public IMessage sendAndReceive(IMessage message, int timeout, boolean compression) {
		return null;
	}
}
