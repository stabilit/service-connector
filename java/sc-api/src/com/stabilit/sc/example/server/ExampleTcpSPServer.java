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

import java.io.IOException;
import java.util.Properties;

import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.PublishMessage;
import com.stabilit.sc.server.IServer;
import com.stabilit.sc.server.ServerFactory;

/**
 * ExampleServer.
 * 
 * @author JTraber
 */
public class ExampleTcpSPServer implements Runnable {
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

	public static void main(String[] args) {
		ExampleTcpSPServer server = new ExampleTcpSPServer();
		server.runRequestResponseServer();
	}

	public void runRequestResponseServer() {

		Properties props = new Properties();
		try {
			props.load(ExampleTcpSPServer.class.getResourceAsStream("exampleTcpSPServer.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		IServer tcpRRServer = ServerFactory.getInstance().createTcpServer("Service B",
				ServerSPListener.class, props);

		try {
			tcpRRServer.connect(30, null);
			tcpRRServer.registerServer(10, 15);
			SCMP scmp = new SCMP();
			PublishMessage publishMsg = new PublishMessage();
			publishMsg.setAttribute("msg", "publish msg");
			scmp.setBody(publishMsg);
			tcpRRServer.publish(scmp, 10, false);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		runRequestResponseServer();
	}
}
