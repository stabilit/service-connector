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
package com.stabilit.sc.server;

import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.IClientListener;
import com.stabilit.sc.msg.impl.RegisterMessage;
import com.stabilit.sc.pool.IPoolConnection;
import com.stabilit.sc.service.ConnectionCtx;

/**
 * @author JTraber
 * 
 */
class TcpServer extends Server {

	/**
	 * @param serviceName
	 * @param serviceHandler
	 * @param ctx
	 */
	protected TcpServer(String serviceName, Class<? extends IClientListener> serviceHandler,
			ClientApplicationContext ctx) {
		super(serviceName, serviceHandler, ctx);
	}

	@Override
	public void connect(int timeout, ConnectionCtx connectionCtx) throws ScConnectionException {
		super.connect(timeout, connectionCtx);
	}

	@Override
	public void publish(SCMP scmp, int timeout, boolean compression) {

		IPoolConnection conn = pool.borrowConnection(ctx, scListenerClass);

		try {
			conn.send(scmp);
			conn.releaseConnection();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void registerServer(int readTimeout, int writeTimeout) throws ServiceException {
		IPoolConnection conn = pool.borrowConnection(ctx, scListenerClass);
		// Register handshake
		SCMP scmpRequest = new SCMP();
		RegisterMessage regMsg = new RegisterMessage();
		regMsg.setServiceName(serviceCtx.getServiceName());
		scmpRequest.setBody(regMsg);
		try {
			SCMP scmpResponse = conn.sendAndReceive(scmpRequest);
		} catch (Exception e) {
			throw new ServiceException("Error occured when registering Service to SC.");
		}
		conn.releaseConnection();
	}

	@Override
	public void disconnect(int timeout, ConnectionCtx connectionCtx) throws ScConnectionException {
		// TODO Auto-generated method stub

	}

	@Override
	public void unregisterServer(int readTimeout, int writeTimeout) throws ServiceException {
		// TODO do unregister server		
	}
}
