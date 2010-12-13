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
import org.serviceconnector.test.integration.api.cln.AfterSCAbortClientTest;
import org.serviceconnector.test.integration.api.cln.AfterSCRestartClientTest;
import org.serviceconnector.test.integration.api.cln.AttachDetachTest;
import org.serviceconnector.test.integration.api.cln.AttachTest;
import org.serviceconnector.test.integration.api.cln.EnableDisableServiceTest;
import org.serviceconnector.test.integration.api.cln.MultipleSCsClientTest;
import org.serviceconnector.test.integration.api.cln.NewServiceTest;
import org.serviceconnector.test.integration.api.srv.AfterSCAbortServerTest;
import org.serviceconnector.test.integration.api.srv.AfterSCRestartServerTest;
import org.serviceconnector.test.integration.api.srv.MultipleSCsServerTest;
import org.serviceconnector.test.integration.api.srv.PublishServerTest;
import org.serviceconnector.test.integration.api.srv.RegisterPublishServerTest;
import org.serviceconnector.test.integration.api.srv.RegisterSessionServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	// client tests
		AttachTest.class,
		AttachDetachTest.class, 
		MultipleSCsClientTest.class,
		EnableDisableServiceTest.class,
		NewServiceTest.class, 
		AfterSCAbortClientTest.class,
		AfterSCRestartClientTest.class, 
	// server tests
		RegisterSessionServerTest.class, 
		RegisterPublishServerTest.class,
		PublishServerTest.class,
		MultipleSCsServerTest.class,
		AfterSCAbortServerTest.class,
		AfterSCRestartServerTest.class,
})
public class AllIntegrationTests {
}
