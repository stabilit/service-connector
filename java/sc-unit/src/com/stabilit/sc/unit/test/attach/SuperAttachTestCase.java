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
package com.stabilit.sc.unit.test.attach;

import java.util.Date;

import org.junit.After;
import org.junit.Before;

import com.stabilit.sc.cln.call.SCMPCallFactory;
import com.stabilit.sc.cln.call.SCMPAttachCall;
import com.stabilit.sc.cln.call.SCMPDetachCall;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPMessage;
import com.stabilit.sc.unit.test.SuperTestCase;
import com.stabilit.sc.util.ValidatorUtility;

/**
 * @author JTraber
 */
public abstract class SuperAttachTestCase extends SuperTestCase {

	protected Date localDateTimeOfConnect;

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public SuperAttachTestCase(String fileName) {
		super(fileName);
	}

	@Before
	public void setup() throws Exception {
		super.setup();
		clnAttachBefore();
	}

	@After
	public void tearDown() throws Exception {
		clnDetachAfter();
		super.tearDown();
	}

	public void clnAttachBefore() throws Exception {
		SCMPAttachCall connectCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(client);

		connectCall.setVersion(SCMPMessage.SC_VERSION.toString());
		connectCall.setCompression(false);
		connectCall.setKeepAliveTimeout(30);
		connectCall.setKeepAliveInterval(360);
		connectCall.invoke();
		localDateTimeOfConnect = ValidatorUtility.validateLocalDateTime(connectCall.getRequest().getHeader(
				SCMPHeaderAttributeKey.LOCAL_DATE_TIME));
	}

	public void clnDetachAfter() throws Exception {
		SCMPDetachCall disconnectCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL
				.newInstance(client);
		disconnectCall.invoke();
	}
}