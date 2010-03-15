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

import com.stabilit.sc.context.ServerConnectionContext;

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

	public IServer createHttpServer(String serviceName) {
		ServerConnectionContext serverCtx = new ServerConnectionContext("localhost", 8001, "localhost", 9000, "netty.http");

		return new HttpServer(serviceName, serverCtx);
	}
	
	public IServer createTcpServer(String serviceName) {
		ServerConnectionContext serverCtx = new ServerConnectionContext("localhost", 9000, "localhost", 7777, "nettyAPI.tcp");
		return new TcpServer(serviceName, serverCtx);
	}
}
