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
package com.stabilit.sc.example.client;

import java.io.IOException;
import java.util.Properties;

import com.stabilit.sc.exception.ScConnectionException;
import com.stabilit.sc.exception.ServiceException;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.msg.impl.GetDataMessage;
import com.stabilit.sc.service.IRequestResponseService;
import com.stabilit.sc.service.ServiceFactory;

/**
 * Example Client.
 * 
 * @author JTraber
 */
public class ExampleTcpRRClient implements Runnable {

	/** The Constant KEEP_ALIVE_TIMEOUT. */
	private static final int KEEP_ALIVE_TIMEOUT = 12;

	/** The Constant KEEP_ALIVE_INTERVAL. */
	private static final int KEEP_ALIVE_INTERVAL = 2;

	/** The Constant TIMEOUT. */
	private static final int TIMEOUT = 10;


	public static void main(String args[]) {
		ExampleTcpRRClient client = new ExampleTcpRRClient();
		client.runTcpRequestResponseService();
	}

	@Override
	public void run() {
		runTcpRequestResponseService();
	}
	
	public void runTcpRequestResponseService() {

		Properties props = new Properties();
		try {
			props.load(ExampleTcpRRClient.class.getResourceAsStream("exampleTcpRRGui.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		IRequestResponseService rrService = ServiceFactory.getInstance().createRequestResponseService(
				"Service A", ClientCallback.class, props);
		try {
			rrService.connect(10, null);
			SCMP scmp = new SCMP();
			GetDataMessage getData = new GetDataMessage();
			getData.setServiceName("Service A");
			scmp.setBody(getData);
			rrService.send(scmp, 10, false);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}

	}
}
