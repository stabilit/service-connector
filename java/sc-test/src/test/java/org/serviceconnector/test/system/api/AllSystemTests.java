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
package org.serviceconnector.test.system.api;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.api.publish.APIAfterSCAbortReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APIAfterServerAbortReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APIAfterServerRestartReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APIChangeSubscriptionTest;
import org.serviceconnector.test.system.api.publish.APIReceivePublicationTest;
import org.serviceconnector.test.system.api.publish.APISubscribeTest;
import org.serviceconnector.test.system.api.publish.APIUnsubscribeTest;
import org.serviceconnector.test.system.api.session.APIAfterSCAbortSessionTest;
import org.serviceconnector.test.system.api.session.APIAfterServerAbortSessionTest;
import org.serviceconnector.test.system.api.session.APIAfterServerRestartSessionTest;
import org.serviceconnector.test.system.api.session.APICreateSessionTest;
import org.serviceconnector.test.system.api.session.APIDeleteSessionTest;
import org.serviceconnector.test.system.api.session.APIExecuteAsynchronousTest;
import org.serviceconnector.test.system.api.session.APIExecuteTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// session tests
		APICreateSessionTest.class,
		APIDeleteSessionTest.class,
		APIExecuteTest.class, 
		APIExecuteAsynchronousTest.class,
		APIAfterSCAbortSessionTest.class,
		APIAfterServerAbortSessionTest.class,
		APIAfterServerRestartSessionTest.class,

		// publish tests
		APISubscribeTest.class, 
		APIUnsubscribeTest.class, 
		APIChangeSubscriptionTest.class, 
		APIReceivePublicationTest.class,
		APIAfterSCAbortReceivePublicationTest.class,
		APIAfterServerAbortReceivePublicationTest.class,
		APIAfterServerRestartReceivePublicationTest.class})
public class AllSystemTests {
}
