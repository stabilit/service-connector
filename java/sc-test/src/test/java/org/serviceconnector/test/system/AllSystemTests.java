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
import org.serviceconnector.test.system.publish.ChangeSubscriptionClientTest;
import org.serviceconnector.test.system.publish.PublishClientTest;
import org.serviceconnector.test.system.publish.SubscribeClientTest;
import org.serviceconnector.test.system.publish.SubscribeUnsubscribeClientTest;
import org.serviceconnector.test.system.publish.SubscriptionServerTest;
import org.serviceconnector.test.system.session.AsynchronousExecuteClientTest;
import org.serviceconnector.test.system.session.CreateSessionHttpClientTest;
import org.serviceconnector.test.system.session.CreateSessionTest;
import org.serviceconnector.test.system.session.ExecuteClientTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfSCProcessClientTest;
import org.serviceconnector.test.system.session.PrematureDestroyOfServerProcessClientTest;
import org.serviceconnector.test.system.session.RejectSessionClientTest;
import org.serviceconnector.test.system.session.SessionServerTest;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// session tests
		CreateSessionHttpClientTest.class, 
		CreateSessionTest.class,
		ExecuteClientTest.class, 
		AsynchronousExecuteClientTest.class,
		PrematureDestroyOfSCProcessClientTest.class, 
		PrematureDestroyOfServerProcessClientTest.class,
		RejectSessionClientTest.class,
		SessionServerTest.class,

		// publish tests
		SubscribeClientTest.class, 
		SubscribeUnsubscribeClientTest.class,
		ChangeSubscriptionClientTest.class, 
		PublishClientTest.class,
		SubscriptionServerTest.class,

		// special tests
		EnableServiceDisableServiceClientToSCTest.class})
public class AllSystemTests {
}
