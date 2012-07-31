/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.test.system.api.cln.casc2;

import java.util.Map;

import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.api.SCMessage;
import org.serviceconnector.api.SCSubscribeMessage;
import org.serviceconnector.api.cln.SCClient;
import org.serviceconnector.api.cln.SCSessionService;
import org.serviceconnector.cache.SC_CACHE_ENTRY_STATE;
import org.serviceconnector.net.ConnectionType;
import org.serviceconnector.test.system.api.cln.casc1.APICacheCoherencyCasc1Test;

public class APICacheCoherencyCasc2Test extends APICacheCoherencyCasc1Test {

	public APICacheCoherencyCasc2Test() {
		APICacheCoherencyCasc2Test.setUp2CascadedServiceConnectorAndServer();
	}

	/**
	 * Description:
	 * 01: disconnect client to top level (cascaded) SC
	 * 02: connect new client to SC0
	 * 03: load data to cache (cid=700) on SC0
	 * 04: start cache guardian - publish 3 large Appendix
	 * 05: verify callback retrieval - 3 appendix within 10sec
	 * 06: verify data is in cache on SC0
	 * 07: start cache guardian over top level (cascaded) SC - doing nothing
	 * 08: stop cache guardian over SC0 - cached managed data should stay
	 * 09: verify data is still in cache on SC0
	 * 10: verify data is NOT in top level cache
	 * 11: reconnect client to top level (cascaded) SC - read data from cache
	 * 12: verify data is correct
	 * 13: verify data is now in top level cache
	 * Expectation: passes
	 */
	@Test
	public void t40_cc_ManagedDataToSC0PublishAppendixClientToCascSC() throws Exception {
		// 1: disconnect client to top level (cascaded) SC
		this.sessionService1.deleteSession();
		this.sessionClient.detach();

		// 2: connect new client to SC0
		SCClient clientToSc0 = new SCClient(TestConstants.HOST, TestConstants.PORT_SC0_TCP, ConnectionType.NETTY_TCP);
		clientToSc0.attach();
		SCSessionService sessionSrvToSC0 = clientToSc0.newSessionService(TestConstants.sesServiceName1);

		// 3: load data to cache (cid=700) on SC0
		SCMessage request = new SCMessage();
		request.setData("cacheFor1Hour_managedData");
		request.setCacheId("700");
		request.setMessageInfo(TestConstants.cacheCmd);
		sessionSrvToSC0.createSession(new SCMessage(), new SessionMsgCallback(sessionSrvToSC0));
		sessionSrvToSC0.execute(request);

		// 4: start cache guardian - publish 3 large appendix
		SCSubscribeMessage subMsg = new SCSubscribeMessage();
		subMsg.setMask(TestConstants.mask);
		subMsg.setSessionInfo(TestConstants.publish3LargeAppendixMsgCmd);
		subMsg.setData("700");
		clientToSc0.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 5: verify callback retrieval - 3 appendix within 10sec
		cacheGuardianCbk.waitForAppendMessage(10, 3);

		// 6: verify data is in cache on SC0
		Map<String, String> inspectResponse = clientToSc0.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian1);

		// 7: start cache guardian over top level (cascaded) SC - doing nothing
		subMsg.setSessionInfo(TestConstants.doNothingCmd);
		guardianClient.startCacheGuardian(TestConstants.cacheGuardian1, subMsg, cacheGuardianCbk);

		// 8: stop cache guardian over SC0 - cached managed data should stay
		clientToSc0.stopCacheGuardian();

		// 9: verify data is still in cache on SC0
		inspectResponse = clientToSc0.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", TestConstants.cacheGuardian1);

		// 10: verify data is NOT in top level cache
		inspectResponse = this.mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "notfound", SC_CACHE_ENTRY_STATE.UNDEFINDED, "700", "", "", "");

		// 11: reconnect client to top level (cascaded) SC - read data from cache
		this.sessionClient.attach();
		sessionService1 = sessionClient.newSessionService(TestConstants.sesServiceName1);
		sessionService1.createSession(new SCMessage(), new SessionMsgCallback(sessionService1));
		SCMessage response = sessionService1.execute(request);

		// 12: verify data is correct
		this.checkAppendices(response, 3);

		// 13: verify data is now in top level cache
		inspectResponse = mgmtClient.inspectCache("700");
		this.checkCacheInspectString(inspectResponse, "success", SC_CACHE_ENTRY_STATE.LOADED, "700", "3",
				"700/0|0=0&700/1|0=1&700/2|0=1&700/3|0=1&", "unset");
	}
}
