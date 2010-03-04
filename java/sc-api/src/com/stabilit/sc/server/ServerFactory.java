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
package com.stabilit.sc.server;

import java.util.Properties;

import com.stabilit.sc.context.ClientApplicationContext;
import com.stabilit.sc.msg.IClientListener;

/**
 * The ServiceFactory creates Services for clients.
 * 
 * @author JTraber
 */
public final class ServerFactory {

	/** The singleton instance. */
	private static ServerFactory factory = new ServerFactory();

	/**
	 * Instantiates a new service factory.
	 */
	private ServerFactory() {
	}

	/**
	 * Gets the single instance of ServiceFactory.
	 * 
	 * @return single instance of ServiceFactory
	 */
	public static ServerFactory getInstance() {
		return factory;
	}

	/**
	 * Creates a new RequestResponseService object.
	 * 
	 * @param serviceName
	 *            the service name
	 * @param responseHandler
	 *            the response handler
	 * @param timeoutHandler
	 *            the timeout handler
	 * 
	 * @return the requestresponse service
	 */
	public HttpRRServer createHttpRRServer(String serviceName,
			Class<? extends IClientListener> scListenerClass, Properties props) {
		ClientApplicationContext appCtx = new ClientApplicationContext();
		appCtx.setProps(props);
//		try {
//			appCtx.setArgs(new String[] { "-app", "echo.client", "-con", "netty.http", "-prot", "http",
//					"-url", "http://localhost:80" });
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return new HttpRRServer(serviceName, scListenerClass, appCtx);
	}
	
	public TcpRRServer createTCPRRServer(String serviceName, Class<? extends IClientListener> scListenerClass, Properties props) {
		ClientApplicationContext appCtx = new ClientApplicationContext();
		appCtx.setIdentifier("TcpRRServer");
		appCtx.setProps(props);
		return new TcpRRServer(serviceName, scListenerClass, appCtx);
	}
}
