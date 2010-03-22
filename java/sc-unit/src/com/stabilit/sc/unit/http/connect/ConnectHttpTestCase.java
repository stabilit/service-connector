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
package com.stabilit.sc.unit.http.connect;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.msg.impl.ConnectMessage;
import com.stabilit.sc.util.DateTimeUtility;

public class ConnectHttpTestCase {

	ClientConfig config = null;
	IClient client = null;

	public ConnectHttpTestCase() throws IOException {
		config = new ClientConfig();
		config.load("sc-unit.properties");

	}

	@Before
	public void setUp() throws Exception {
		ClientFactory clientFactory = new ClientFactory();
		client = clientFactory.newInstance(config.getClientConfig());
	}

	@After
	public void tearDown() throws Exception {
		client = null;
	}

	@Test
	public void singleConnect() {

		try {
			client.connect(); //physical connect

			/*************** scmp connect ******************/
			SCMP scmp = new SCMP();
			scmp.setMessageType(SCMPMsgType.REQ_CONNECT.getRequestName());
			ConnectMessage connect = new ConnectMessage();

			connect.setVersion("1.0");
			connect.setCompression(false);
			connect.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());
			connect.setKeepAliveTimeout(30);
			connect.setKeepAliveInterval(360);

			scmp.setBody(connect);
			SCMP result = client.sendAndReceive(scmp);

			System.out.println("Body: " + result.getBody());

			System.out.println("Header:");
			for (Entry<String, String> entry : result.getHeader().entrySet()) {
				System.out.println(entry.getKey() + " " + entry.getValue());
			}
			/*************** scmp connect ******************/			
			/*************** scmp disconnect *********/
			//TODO
			/*************** scmp disconnect *********/
			
			client.disconnect(); //physical disconnect
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		}
	}
}
