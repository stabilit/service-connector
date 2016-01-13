/*-----------------------------------------------------------------------------*
 *                                                                             *
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
 *-----------------------------------------------------------------------------*/
package org.serviceconnector.test.system;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.serviceconnector.test.system.api.cln.casc1.APICacheCoherencyCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APICreateDeleteSessionCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APIExecuteAndSendCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APIExecuteCacheCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APIMultipleClientChangeSubscriptionCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APIMultipleClientSubscribeCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APIReceivePublicationCasc1Test;
import org.serviceconnector.test.system.api.cln.casc1.APISubscribeUnsubscribeChangeCasc1Test;
import org.serviceconnector.test.system.api.cln.casc2.APICacheCoherencyCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APICreateDeleteSessionCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APIExecuteAndSendCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APIExecuteCacheCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APIMultipleClientChangeSubscriptionCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APIMultipleClientSubscribeCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APIReceivePublicationCasc2Test;
import org.serviceconnector.test.system.api.cln.casc2.APISubscribeUnsubscribeChangeCasc2Test;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnChangeSubscriptionCasc1Test;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnCreateSessionCasc1Test;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnExecuteCacheCasc1Test;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnExecuteCasc1Test;
import org.serviceconnector.test.system.scmp.casc1.SCMPClnSubscribeCasc1Test;
import org.serviceconnector.test.system.scmp.casc2.SCMPClnChangeSubscriptionCasc2Test;
import org.serviceconnector.test.system.scmp.casc2.SCMPClnCreateSessionCasc2Test;
import org.serviceconnector.test.system.scmp.casc2.SCMPClnExecuteCacheCasc2Test;
import org.serviceconnector.test.system.scmp.casc2.SCMPClnExecuteCasc2Test;
import org.serviceconnector.test.system.scmp.casc2.SCMPClnSubscribeCasc2Test;

@RunWith(Suite.class)
@Suite.SuiteClasses( {
		// test need to be at the begin, they check log files for EXC
		APIMultipleClientSubscribeCasc1Test.class,
		APIMultipleClientChangeSubscriptionCasc1Test.class,
		APIMultipleClientSubscribeCasc2Test.class,
		APIMultipleClientChangeSubscriptionCasc2Test.class,

		// API session tests
		APICreateDeleteSessionCasc1Test.class,
		APIExecuteAndSendCasc1Test.class,
		
		// cache coherency tests
		APICacheCoherencyCasc1Test.class,

		// API publish tests
		APISubscribeUnsubscribeChangeCasc1Test.class,
		APIReceivePublicationCasc1Test.class,
		APIExecuteCacheCasc1Test.class,

		// SCMP session test
		SCMPClnCreateSessionCasc1Test.class,
		SCMPClnExecuteCasc1Test.class,
		SCMPClnChangeSubscriptionCasc1Test.class,
		
		// SCMP publish test
		SCMPClnSubscribeCasc1Test.class,
		
		// SCMP cache test
		SCMPClnExecuteCacheCasc1Test.class,
		
		// API session tests for cascaded 2 mode
		APICreateDeleteSessionCasc2Test.class,
		APIExecuteAndSendCasc2Test.class,

		// cache coherency tests
		APICacheCoherencyCasc2Test.class,
		
		// API publish tests for cascaded 2 mode
		APISubscribeUnsubscribeChangeCasc2Test.class,
		APIReceivePublicationCasc2Test.class,
		APIExecuteCacheCasc2Test.class,
		
		// SCMP session test for cascaded 2 mode
		SCMPClnCreateSessionCasc2Test.class,
		SCMPClnExecuteCasc2Test.class,
		SCMPClnChangeSubscriptionCasc2Test.class,
		
		// SCMP publish test for cascaded 2 mode
		SCMPClnSubscribeCasc2Test.class,
		
		// SCMP cache tests for cascaded 2 mode
		SCMPClnExecuteCacheCasc2Test.class
		})
public class AllCascSystemTests {
}
