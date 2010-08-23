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

import com.stabilit.scm.common.call.SCMPAttachCall;
import com.stabilit.scm.common.call.SCMPCallFactory;
import com.stabilit.scm.common.call.SCMPDetachCall;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.SCMPMsgType;
import com.stabilit.scm.common.util.SynchronousCallback;
import com.stabilit.scm.common.util.ValidatorUtility;
import com.stabilit.scm.unit.test.SCTest;
import com.stabilit.scm.unit.test.SuperTestCase;

public class AttachTestCase extends SuperTestCase {

	/**
	 * The Constructor.
	 * 
	 * @param parameter
	 *            the parameter
	 */
	public AttachTestCase(String parameter) {
		super(parameter);
	}

	@Test
	public void attach() throws Exception {
		SCMPAttachCall attachCall = (SCMPAttachCall) SCMPCallFactory.ATTACH_CALL.newInstance(req);

		AttachTestCallback callback = new AttachTestCallback();
		attachCall.invoke(callback);
		SCMPMessage result = callback.getMessageSync();
		SCTest.checkReply(result);
		/*********************************** Verify attach response msg **********************************/
		Assert.assertNull(result.getBody());
		Assert.assertEquals(result.getHeader(SCMPHeaderAttributeKey.MSG_TYPE), SCMPMsgType.ATTACH.getValue());
		Assert.assertNotNull(ValidatorUtility.validateLocalDateTime(result
				.getHeader(SCMPHeaderAttributeKey.LOCAL_DATE_TIME)));

		SCMPDetachCall detachCall = (SCMPDetachCall) SCMPCallFactory.DETACH_CALL.newInstance(req);
		detachCall.invoke(callback);
		SCTest.checkReply(callback.getMessageSync());
	}

	private class AttachTestCallback extends SynchronousCallback {
		// nothing to implement in this case - everything is done by super-class
	}
}
