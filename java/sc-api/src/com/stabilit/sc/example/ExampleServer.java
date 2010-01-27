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

import com.stabilit.sc.MessageTransportType;
import com.stabilit.sc.ServerScConnection;
import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.handler.ServerResponseHandler;
import com.stabilit.sc.handler.ServerTimeoutHandler;
import com.stabilit.sc.msg.IMessage;

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

	/**
	 * Run publish server.
	 */
	public void runPublishServer() {

		ServerScConnection con = new ServerScConnection(HOST, PORT, MessageTransportType.HTTP, NUM_OF_CON);
		try {
			con.attach(TIMEOUT, KEEP_ALIVE_INTERVAL, KEEP_ALIVE_TIMEOUT);
			con.register("serviceName", new ServerResponseHandler() {

				@Override
				public void controlMessageReceived(ServerScConnection connection, IMessage message) {
				}

				@Override
				public void exceptionCaught(ServerScConnection connection, ScConnectionException exception) {
				}

				@Override
				public void executionMessageReceived(ServerScConnection connection, IMessage message) {
				}

			}, new ServerTimeoutHandler() {

				@Override
				public void connectTimedOut(ServerScConnection connection) {
				}

				@Override
				public void readTimedOut(ServerScConnection connection) {
				}

				@Override
				public void writeTimedOut(ServerScConnection connection) {
				}

			});
			
			con.detach(TIMEOUT);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		}
	}
}
