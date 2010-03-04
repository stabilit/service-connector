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
import com.stabilit.sc.example.client.ExampleTcpRRClient;
import com.stabilit.sc.example.server.ExampleTcpRRServer;

/**
 * @author JTraber
 */
public class TcpClientServerRRTest {	
	
	@Test
	public void runTcpClientServerRRTest() {
		SCKernel scKernel = new SCKernel();
		Thread scKernelTh = new Thread(scKernel);
		scKernelTh.start();
		
		ExampleTcpRRServer tcpRRServer = new ExampleTcpRRServer();
		Thread tcpRRServerTh = new Thread(tcpRRServer);
		tcpRRServerTh.start();
		
		ExampleTcpRRClient tcpRRClient = new ExampleTcpRRClient();
		Thread tcpRRClientTh = new Thread(tcpRRClient);
		tcpRRClientTh.start();
		try {
			Thread.currentThread().join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
