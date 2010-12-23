/*
 *       Copyright © 2010 STABILIT Informatik AG, Switzerland                  *
 *                                                                             *
 *  Licensed under the Apache License, Version 2.0 (the "License");            *
 *  you may not use this file except in compliance with the License.           *
 *  You may obtain a copy of the License at                                    *
 *                                                                             *
 *  http://www.apache.org/licenses/LICENSE-2.0                                 *
 *                                                                             *
 *  Unless required by applicable law or agreed to in writing, software        *
 *  distributed under the License is distributed on an "AS IS" BASIS,          *
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   *
 *  See the License for the specific language governing permissions and        *
 *  limitations under the License.                                             *
 */
package org.serviceconnector.test.integration;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.integration.api.cln.APIAfterSCAbortClientTest;
import org.serviceconnector.test.integration.api.cln.APIAfterSCRestartClientTest;
import org.serviceconnector.test.integration.api.cln.APIAttachDetachClientTest;
import org.serviceconnector.test.integration.api.cln.APIEnableDisableServiceTest;
import org.serviceconnector.test.integration.api.cln.APIMultipleSCsClientTest;
import org.serviceconnector.test.integration.api.cln.APINewServiceTest;
import org.serviceconnector.test.integration.api.srv.APIAfterSCAbortServerTest;
import org.serviceconnector.test.integration.api.srv.APIAfterSCRestartServerTest;
import org.serviceconnector.test.integration.api.srv.APICheckRegistrationTest;
import org.serviceconnector.test.integration.api.srv.APIMultipleSCsServerTest;
import org.serviceconnector.test.integration.api.srv.APIPublishServerTest;
import org.serviceconnector.test.integration.api.srv.APIRegisterPublishServerTest;
import org.serviceconnector.test.integration.api.srv.APIRegisterSessionServerTest;
import org.serviceconnector.test.integration.scmp.SCMPAttachDetachTest;
import org.serviceconnector.test.integration.scmp.SCMPManageTest;
import org.serviceconnector.test.integration.scmp.SCMPRegisterDeregisterServerTest;
import org.serviceconnector.test.integration.scmp.SCMPSCVersionTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// API client tests
		APIAttachDetachClientTest.class,
		APIMultipleSCsClientTest.class,
		APIEnableDisableServiceTest.class,
		APINewServiceTest.class,
		APIAfterSCAbortClientTest.class,
		APIAfterSCRestartClientTest.class,

		// API server tests
		APIRegisterSessionServerTest.class,
		APIRegisterPublishServerTest.class,
		APICheckRegistrationTest.class,
		APIPublishServerTest.class,
		APIMultipleSCsServerTest.class,
		APIAfterSCAbortServerTest.class,
		APIAfterSCRestartServerTest.class,

		// SCMP client test
		SCMPAttachDetachTest.class,
		ConnectionPoolTest.class,
		SCMPManageTest.class,
		SCMPRegisterDeregisterServerTest.class,
		SCMPSCVersionTest.class,
		ConnectionTest.class,
		MultipleNICTest.class
})
public class AllIntegrationTests {
}
