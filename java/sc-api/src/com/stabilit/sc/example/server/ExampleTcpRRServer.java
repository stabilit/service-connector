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
package com.stabilit.sc.example.server;

import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.ServerFactory;

/**
 * ExampleServer.
 * 
 * @author JTraber
 */
public class ExampleTcpRRServer implements Runnable {

	public static void main(String[] args) throws Exception {
		ExampleTcpRRServer server = new ExampleTcpRRServer();
		server.runRequestResponseServer();
	}

	@Override
	public void run() {
		try {
			runRequestResponseServer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void runRequestResponseServer() throws Exception {
		IServer tcpRRServer = ServerFactory.getInstance().createTcpServer("Service A");
		tcpRRServer.connect();	
	}	
}
