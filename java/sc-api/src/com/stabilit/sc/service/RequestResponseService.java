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

import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.pool.IPoolConnection;

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
	 * @param serviceHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 */
	protected RequestResponseService(String serviceName, Class<? extends IClientListener> clientListenerClass, ClientApplicationContext ctx) {
		super(serviceName, clientListenerClass, ctx);
	}

	/** {@inheritDoc} */
	@Override
	public void send(SCMP scmp, int timeout, boolean compression) {
		log.debug("RequestResponseService sends to SC.");
		IPoolConnection conn = pool.borrowConnection(ctx, clientListenerClass);
	
		try {
			conn.send(scmp);
			conn.releaseConnection();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** {@inheritDoc} */
	@Override
	public SCMP sendAndReceive(SCMP scmp, int timeout, boolean compression) {
		log.debug("RequestResponseService sendAndReceive to/from SC.");
		IPoolConnection conn = pool.borrowConnection(ctx, clientListenerClass);
		SCMP ret = null;
		try {
			ret = conn.sendAndReceive(scmp);
			conn.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}
}
