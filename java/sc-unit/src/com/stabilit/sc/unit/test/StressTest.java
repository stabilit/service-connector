package com.stabilit.sc.unit.test;

import org.junit.Test;

import com.stabilit.sc.cln.scmp.SCMPSession;
import com.stabilit.sc.cln.service.SCMPCallFactory;
import com.stabilit.sc.cln.service.SCMPClnCreateSessionCall;
import com.stabilit.sc.cln.service.SCMPClnDeleteSessionCall;
import com.stabilit.sc.cln.service.SCMPConnectCall;
import com.stabilit.sc.cln.service.SCMPDisconnectCall;
import com.stabilit.sc.scmp.SCMP;

public class StressTest extends SuperTestCase {

	public StressTest(String fileName) {
		super(fileName);
	}

	// @Test
	public void connectDisconnect() {
		for (int i = 0; i < 10000; i++) {
			try {
				SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL
						.newInstance(client);
				connectCall.setVersion("1.0-00");
				connectCall.setCompression(false);
				connectCall.setKeepAliveTimeout(30);
				connectCall.setKeepAliveInterval(360);
				SCMP result = connectCall.invoke();
				SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
						.newInstance(client);
				disconnectCall.invoke();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Test
	public void createDeleteSession() {
		try {
			SCMPConnectCall connectCall = (SCMPConnectCall) SCMPCallFactory.CONNECT_CALL
					.newInstance(client);
			connectCall.setVersion("1.0-00");
			connectCall.setCompression(false);
			connectCall.setKeepAliveTimeout(30);
			connectCall.setKeepAliveInterval(360);
			SCMP result = connectCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			for (int i = 0; i < 10000; i++) {
				SCMPClnCreateSessionCall createSessionCall = (SCMPClnCreateSessionCall) SCMPCallFactory.CLN_CREATE_SESSION_CALL

				.newInstance(client);
				createSessionCall.setServiceName("simulation");
				createSessionCall
						.setSessionInfo("SNBZHP - TradingClientGUI 10.2.7");
				SCMPSession scmpSession = createSessionCall.invoke();

				SCMPClnDeleteSessionCall deleteSessionCall = (SCMPClnDeleteSessionCall) SCMPCallFactory.CLN_DELETE_SESSION_CALL
						.newInstance(client, scmpSession);
				deleteSessionCall.invoke();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			SCMPDisconnectCall disconnectCall = (SCMPDisconnectCall) SCMPCallFactory.DISCONNECT_CALL
					.newInstance(client);
			disconnectCall.invoke();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
