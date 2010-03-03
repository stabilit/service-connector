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
public class ExampleHTTPClient {

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

	public static void main(String args[]) {
		ExampleHTTPClient client = new ExampleHTTPClient();
		client.runRequestResponseService();
	}

	public void runRequestResponseService() {
		IRequestResponseService rrService = ServiceFactory.getInstance().createRequestResponseService(
				"Service A TCP", ServiceRRListener.class);
		try {
			rrService.connect(10, null);
			SCMP scmp = new SCMP();
			GetDataMessage getData = new GetDataMessage();
			getData.setServiceName("Service A TCP");
			scmp.setBody(getData);
			rrService.send(scmp, 10, false);
		} catch (ScConnectionException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		
	}
}
