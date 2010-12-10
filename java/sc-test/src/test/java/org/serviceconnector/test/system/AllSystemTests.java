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
import org.serviceconnector.test.system.publish.ChangeSubscriptionTest;
import org.serviceconnector.test.system.publish.ReceivePublicationTest;
import org.serviceconnector.test.system.publish.PublishTest;
import org.serviceconnector.test.system.publish.SubscribeTest;
import org.serviceconnector.test.system.publish.UnsubscribeTest;
import org.serviceconnector.test.system.session.AfterSCAbortSessionTest;
import org.serviceconnector.test.system.session.AfterServerAbortSessionTest;
import org.serviceconnector.test.system.session.CreateSessionTest;
import org.serviceconnector.test.system.session.DeleteSessionTest;
import org.serviceconnector.test.system.session.ExecuteAsynchronousTest;
import org.serviceconnector.test.system.session.ExecuteTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// session tests
		CreateSessionTest.class,
		DeleteSessionTest.class,
		ExecuteTest.class, 
		ExecuteAsynchronousTest.class,
		AfterSCAbortSessionTest.class,
		AfterServerAbortSessionTest.class,

		// publish tests
		SubscribeTest.class, 
		UnsubscribeTest.class, 
		ChangeSubscriptionTest.class, 
		ReceivePublicationTest.class,
		PublishTest.class})
public class AllSystemTests {
}
