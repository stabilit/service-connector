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
package com.stabilit.scm.unit.test.attach;

import junit.framework.Assert;

import org.junit.Test;

import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDetachCall;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;

public class DetachTestCase extends SuperAttachTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param fileName
	 *            the file name
	 */
	public DetachTestCase(String fileName) {
		super(fileName);
	}

	@Test
	public void detach() throws Exception {
		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);

		SCMPMessage result = null;
		try {
			detachCall.invoke(this.attachCallback);
			result = this.attachCallback.getMessageSync();
		} catch (Exception e) {
			Assert.fail();
		}

		/*********************************** Verify detach response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.DETACH.getValue());
		super.clnAttachBefore();
	}
}
