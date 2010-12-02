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
import org.serviceconnector.test.integration.cln.AttachDetachTest;
import org.serviceconnector.test.integration.cln.AttachTest;
import org.serviceconnector.test.integration.cln.MultipleSCsClientTest;
import org.serviceconnector.test.integration.cln.EnableDisableServiceTest;
import org.serviceconnector.test.integration.cln.NewServiceTest;
import org.serviceconnector.test.integration.cln.AfterSCAbortClientTest;
import org.serviceconnector.test.integration.cln.AfterSCRestartClientTest;
import org.serviceconnector.test.integration.srv.SCAbortServerTest;
import org.serviceconnector.test.integration.srv.PublishConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.PublishConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServerConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerDeregisterServerConnectionTypeHttpTest;
import org.serviceconnector.test.integration.srv.RegisterServerDeregisterServerConnectionTypeTcpTest;
import org.serviceconnector.test.integration.srv.RegisterServerToMultipleSCTest;
import org.serviceconnector.test.integration.srv.SCRestartServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( { 
	// client tests
		AttachTest.class,
		MultipleSCsClientTest.class,
		AttachDetachTest.class, 
		EnableDisableServiceTest.class,
		NewServiceTest.class, 
		AfterSCAbortClientTest.class,
	// server tests
		SCAbortServerTest.class,
		RegisterServerDeregisterServerConnectionTypeHttpTest.class,
		RegisterServerDeregisterServerConnectionTypeTcpTest.class,
		RegisterServerToMultipleSCTest.class,
		RegisterServerConnectionTypeHttpTest.class, 
		RegisterServerConnectionTypeTcpTest.class,
		AfterSCRestartClientTest.class, 
		SCRestartServerTest.class,
		PublishConnectionTypeHttpTest.class, 
		PublishConnectionTypeTcpTest.class
})
public class AllIntegrationTests {
}
