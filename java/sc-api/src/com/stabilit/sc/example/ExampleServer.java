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
package com.stabilit.sc.example;

import com.stabilit.sc.ServerScConnection;
import com.stabilit.sc.app.client.IConnection;
import com.stabilit.sc.app.server.IServiceServerConnection;
import com.stabilit.sc.serviceserver.ServiceServerException;
import com.stabilit.sc.serviceserver.handler.IResponseHandler;
import com.stabilit.sc.serviceserver.handler.ITimeoutHandler;

/**
 * ExampleServer.
 * 
 * @author JTraber
 */
public class ExampleServer {
	/** The Constant KEEP_ALIVE_TIMEOUT. */
	private static final int KEEP_ALIVE_TIMEOUT = 12;

	/** The Constant KEEP_ALIVE_INTERVAL. */
	private static final int KEEP_ALIVE_INTERVAL = 2;

	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 10;

	/** The Constant NUM_OF_CON. */
	private static final int NUM_OF_CON = 3;

	/** The Constant PORT. */
	private static final int PORT = 80;

	/** The Constant HOST. */
	private static final String HOST = "localhost";

	public static void main(String[] args) throws ServiceServerException {
		ExampleServer service = new ExampleServer();
		service.runRequestResponseServiceOverHttp();
	}

	public void runRequestResponseServiceOverHttp() throws ServiceServerException {
		ServerScConnection scConnection = new ServerScConnection("localhost", 8080,
				"nettyServiceServer.reqRes.http");
		scConnection.register("ServiceA", MyResponseHandler.class, MyTimeoutHandler.class, 30, 30, 30);
	}

	class MyResponseHandler implements IResponseHandler<IServiceServerConnection> {

		@Override
		public Object getMessageSync() {
			return null;
		}

		@Override
		public void messageReceived(IServiceServerConnection con, Object obj) throws Exception {
			// TODO Auto-generated method stub			
		}
	}

	class MyTimeoutHandler implements ITimeoutHandler {

		@Override
		public void readTimedOut(IConnection conn) {			
		}

		@Override
		public void writeTimedOut(IConnection conn) {			
		}
	}
}
