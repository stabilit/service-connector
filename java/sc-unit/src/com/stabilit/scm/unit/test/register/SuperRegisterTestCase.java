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
package com.stabilit.scm.unit.test.register;

import org.junit.After;
import org.junit.Before;

import com.stabilit.scm.cln.call.SCMPCallFactory;
import com.stabilit.scm.cln.call.SCMPDeRegisterServiceCall;
import com.stabilit.scm.cln.call.SCMPRegisterServiceCall;
import com.stabilit.scm.unit.test.attach.SuperAttachTestCase;

/**
 * @author JTraber
 */
public abstract class SuperRegisterTestCase extends SuperAttachTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SuperRegisterTestCase(String fileName) {
		super(fileName);
	}

	@Before
	public void setup() throws Exception {
		super.setup();
		registerServiceBefore();
	}

	@After
	public void tearDown() throws Exception {
		deRegisterServiceAfter();
		super.tearDown();
	}

	public void registerServiceBefore() throws Exception {
		SCMPRegisterServiceCall registerServiceCall = (SCMPRegisterServiceCall) SCMPCallFactory.REGISTER_SERVICE_CALL
				.newInstance(req);

		registerServiceCall.setServiceName("P01_RTXS_RPRWS1");
		registerServiceCall.setMaxSessions(10);
		registerServiceCall.setPortNumber(9000);
		registerServiceCall.invoke();
	}

	public void deRegisterServiceAfter() throws Exception {
		this.deRegisterServiceAfter("P01_RTXS_RPRWS1");
	}
	
	public void deRegisterServiceAfter(String serviceName) throws Exception {
		SCMPDeRegisterServiceCall deRegisterServiceCall = (SCMPDeRegisterServiceCall) SCMPCallFactory.DEREGISTER_SERVICE_CALL
				.newInstance(req);
		deRegisterServiceCall.setServiceName(serviceName);
		deRegisterServiceCall.invoke();
	}
}