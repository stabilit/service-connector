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
package com.stabilit.sc.unit;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.sc.ServiceConnector;
import com.stabilit.sc.client.ClientFactory;
import com.stabilit.sc.client.IClient;
import com.stabilit.sc.config.ClientConfig;
import com.stabilit.sc.io.SCMP;
import com.stabilit.sc.io.SCMPErrorCode;
import com.stabilit.sc.io.SCMPHeaderType;
import com.stabilit.sc.io.SCMPMsgType;
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.service.SCMPCallFactory;
import com.stabilit.sc.service.SCMPConnectCall;
import com.stabilit.sc.service.SCMPMaintenanceCall;
import com.stabilit.sc.service.SCMPServiceException;
import com.stabilit.sc.util.ValidatorUtility;

public class ConnectDisconnectTestCase {

	static ClientConfig config = null;
	static IClient client = null;

	static {
		try {
			ServiceConnector.main(null);
			config = new ClientConfig();
			config.load("sc-unit.properties");

			ClientFactory clientFactory = new ClientFactory();
			client = clientFactory.newInstance(config.getClientConfig());
			client.connect(); // physical connect
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		client.disconnect(); // physical disconnect
		client = null;
	}

	@Test
	public void runTests() throws Exception {
		// guarantees test sequence
		failConnect();
		connect();
//		secondConnect();
//		disconnect();
//		secondDisconnect();
	}

	public void failConnect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);
		connectCall.setVersion("2.0-00");
		SCMP result = null;
		try{
			result = connectCall.invoke();
			Assert.fail("Should throw Exception!");
		} catch(SCMPServiceException ex) {
			verifyError(ex.getFault(), SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.RES_CONNECT);
		}		
		
	}

	public void connect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);

		connectCall.setVersion("1.0-00");
		connectCall.setCompression(false);
		connectCall.setKeepAliveTimeout(30);
		connectCall.setKeepAliveInterval(360);

		SCMP result = connectCall.invoke();

		/*********************************** Verify connect response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.MSG_TYPE.getName()), SCMPMsgType.RES_CONNECT
				.getResponseName());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderType.LOCAL_DATE_TIME.getName())));

		/*************** scmp maintenance ********/
		SCMPMaintenanceCall maintenanceCall = (SCMPMaintenanceCall) SCMPCallFactory.MAINTENANCE_CALL.newInstance(client);
		SCMP maintenance = maintenanceCall.invoke();

		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String expectedScEntry = ":compression=0;localDateTime="
				+ ValidatorUtility.validateLocalDateTime(connectCall.getCall().getHeader(SCMPHeaderType.LOCAL_DATE_TIME.getName()))
				+ ";keepAliveTimeout=30,360;scmpVersion=1.0-00;";
		String scEntry = (String) mainMsg.getAttribute("connectionRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.substring(scEntry.indexOf(":") + 1);
		scEntry = scEntry.substring(scEntry.indexOf(":"));

		Assert.assertEquals(expectedScEntry, scEntry);
	}

	public void secondConnect() throws Exception {
		SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL.newInstance(client);
		
		connectCall.setVersion("1.0-00");
		connectCall.setCompression(false);
		connectCall.setKeepAliveTimeout(30);
		connectCall.setKeepAliveInterval(360);

		SCMP result = connectCall.invoke();
		verifyError(result, SCMPErrorCode.ALREADY_CONNECTED, SCMPMsgType.RES_CONNECT);
	}

	public void disconnect() throws Exception {
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_DISCONNECT.getRequestName());
		SCMP result = client.sendAndReceive(scmp);

		/*********************************** Verify disconnect response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.MSG_TYPE.getName()), SCMPMsgType.RES_DISCONNECT
				.getResponseName());

		/*************** scmp maintenance ********/
		MaintenanceMessage msgMain = new MaintenanceMessage();
		scmp.setMessageType(SCMPMsgType.REQ_MAINTENANCE.getRequestName());
		scmp.setBody(msgMain);
		SCMP maintenance = client.sendAndReceive(scmp);

		/*********************************** Verify registry entries in SC ***********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		String scEntry = (String) mainMsg.getAttribute("connectionRegistry");
		Assert.assertEquals("", scEntry);
	}

	public void secondDisconnect() throws Exception {
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_DISCONNECT.getRequestName());
		SCMP result = client.sendAndReceive(scmp);

		verifyError(result, SCMPErrorCode.NOT_CONNECTED, SCMPMsgType.RES_DISCONNECT);
	}

	/**
	 * @param result
	 */
	private void verifyError(SCMP result, SCMPErrorCode error, SCMPMsgType msgType) {
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.MSG_TYPE.getName()), msgType.getResponseName());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.SC_ERROR_CODE.getName()), error.getErrorCode());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.SC_ERROR_TEXT.getName()), error.getErrorText());
	}
}
