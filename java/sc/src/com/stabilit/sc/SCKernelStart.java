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
package com.stabilit.sc;

import com.stabilit.sc.app.server.Server;

/**
 * @author JTraber
 * 
 */
public class SCKernelStart {

	public static void main(String[] args) {
		SCKernelStart.startHTTPSCServer();
		SCKernelStart.startTCPSCServer();
	}

	public static void startHTTPSCServer() {
		Server server = new Server();

		String[] argss = new String[] { "-app", "netty.http", "-port", "80" };
		server.setArgs(argss);
		Thread thread = new Thread(server);
		thread.start();
	}

	public static void startTCPSCServer() {
		Server server = new Server();

		String[] argss = new String[] { "-app", "netty.tcp", "-port", "81" };
		server.setArgs(argss);
		Thread thread = new Thread(server);
		thread.start();
	}
}
