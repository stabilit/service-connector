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
package com.stabilit.sc.test;

import org.junit.Test;

import com.stabilit.sc.SCKernel;
import com.stabilit.sc.example.client.ExampleTcpSPClient;
import com.stabilit.sc.example.server.ExampleTcpSPServer;

/**
 * @author JTraber
 */
public class TcpClientServerSPTest {

	@Test
	public void runTcpClientServerSPTest() {
		SCKernel scKernel = new SCKernel();
		Thread scKernelTh = new Thread(scKernel);
		scKernelTh.start();

		ExampleTcpSPServer tcpSPServer = new ExampleTcpSPServer();
		Thread tcpSPServerTh = new Thread(tcpSPServer);
		tcpSPServerTh.start();

		 ExampleTcpSPClient tcpSPClient = new ExampleTcpSPClient();
		 Thread tcpSPClientTh = new Thread(tcpSPClient);
		 tcpSPClientTh.start();
		 
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
