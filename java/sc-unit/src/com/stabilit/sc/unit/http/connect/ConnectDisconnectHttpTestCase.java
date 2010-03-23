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

import java.util.Date;

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
import com.stabilit.sc.msg.impl.ConnectMessage;
import com.stabilit.sc.msg.impl.MaintenanceMessage;
import com.stabilit.sc.util.DateTimeUtility;
import com.stabilit.sc.util.ValidatorUtility;

public class ConnectDisconnectHttpTestCase {

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
		//guarantees test sequence
		failConnect();
		connect();
		secondConnect();
		disconnect();
		secondDisconnect();
	}
	
	public void failConnect() throws Exception {
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_CONNECT.getRequestName());
		ConnectMessage connect = new ConnectMessage();
		scmp.setBody(connect);
		/*********************************** incompatible scmp version ****************************/
		connect.setVersion("2.0-00");
		connect.setLocalDateTime(DateTimeUtility.getCurrentTimeZoneMillis());

		SCMP result = client.sendAndReceive(scmp);
		verifyError(result, SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.RES_CONNECT);
		/*********************************** wrong dateTime format ******************************/
		connect.setVersion("1.0-00");
		connect.setLocalDateTime("12.12.2000");

		result = client.sendAndReceive(scmp);
		verifyError(result, SCMPErrorCode.VALIDATION_ERROR, SCMPMsgType.RES_CONNECT);
	}

	public void connect() throws Exception {
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_CONNECT.getRequestName());
		ConnectMessage connect = new ConnectMessage();

		connect.setVersion("1.0-00");
		connect.setCompression(false);
		String localDateTimeString = DateTimeUtility.getCurrentTimeZoneMillis();
		connect.setLocalDateTime(localDateTimeString);
		connect.setKeepAliveTimeout(30);
		connect.setKeepAliveInterval(360);

		scmp.setBody(connect);
		SCMP result = client.sendAndReceive(scmp);

		/*********************************** Verify connect response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderType.MSG_TYPE.getName()), SCMPMsgType.RES_CONNECT
				.getResponseName());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderType.LOCAL_DATE_TIME.getName())));

		/*************** scmp maintenance ********/
		MaintenanceMessage msgMain = new MaintenanceMessage();
		scmp.setMessageType(SCMPMsgType.REQ_MAINTENANCE.getRequestName());
		scmp.setBody(msgMain);
		SCMP maintenance = client.sendAndReceive(scmp);

		/*********************************** Verify registry entries in SC ********************************/
		MaintenanceMessage mainMsg = (MaintenanceMessage) maintenance.getBody();
		Date localDateTime = ValidatorUtility.validateLocalDateTime(localDateTimeString);
		String expectedScEntry = ":compression=false;localDateTime=" + localDateTime
				+ ";keepAliveTimeout=30,360;scmpVersion=1.0-00;";
		String scEntry = (String) mainMsg.getAttribute("connectionRegistry");
		// truncate /127.0.0.1:3640 because port may vary.
		scEntry = scEntry.substring(scEntry.indexOf(":") + 1);
		scEntry = scEntry.substring(scEntry.indexOf(":"));

		Assert.assertEquals(expectedScEntry, scEntry);
	}

	
	public void secondConnect() throws Exception {
		/*************** scmp connect second time ****/
		SCMP scmp = new SCMP();
		scmp.setMessageType(SCMPMsgType.REQ_CONNECT.getRequestName());
		ConnectMessage connect = new ConnectMessage();

		connect.setVersion("1.0-00");
		connect.setCompression(false);
		String localDateTimeString = DateTimeUtility.getCurrentTimeZoneMillis();
		connect.setLocalDateTime(localDateTimeString);
		connect.setKeepAliveTimeout(30);
		connect.setKeepAliveInterval(360);

		scmp.setBody(connect);
		SCMP result = client.sendAndReceive(scmp);

		/*********************************** Verify error response double connect **********************/
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

		/*********************************** Verify registry entries in SC ********************************/
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
