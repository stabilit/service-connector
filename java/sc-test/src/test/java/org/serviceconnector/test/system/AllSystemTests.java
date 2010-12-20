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
package org.serviceconnector.test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.api.publish.APIAfterSCAbortReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APIAfterServerAbortReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APIAfterServerRestartReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APIReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APISubscribeUnsubscribeChangeTest;
import org.serviceconnector.test.system.api.session.APIAfterSCAbortSessionTest;
import org.serviceconnector.test.system.api.session.APIAfterServerAbortSessionTest;
import org.serviceconnector.test.system.api.session.APIAfterServerRestartSessionTest;
import org.serviceconnector.test.system.api.session.APICreateDeleteSessionTest;
import org.serviceconnector.test.system.api.session.APIExecuteAndSendTest;
import org.serviceconnector.test.system.scmp.SCMPClnCreateSessionTest;
import org.serviceconnector.test.system.scmp.SCMPClnExecuteTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// API session tests
		APICreateDeleteSessionTest.class,
		APIExecuteAndSendTest.class,
		APIAfterSCAbortSessionTest.class,
		APIAfterServerAbortSessionTest.class,
		APIAfterServerRestartSessionTest.class,

		// API publish tests
		APISubscribeUnsubscribeChangeTest.class,
		APIReceivePublicationTest.class,
		APIAfterSCAbortReceivePublicationTest.class,
		APIAfterServerAbortReceivePublicationTest.class,
		APIAfterServerRestartReceivePublicationTest.class,

		// SCMP session test
		SCMPClnCreateSessionTest.class,
		SCMPClnExecuteTest.class})
public class AllSystemTests {
}
