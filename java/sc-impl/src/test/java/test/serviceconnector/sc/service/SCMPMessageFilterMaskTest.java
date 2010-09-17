/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package test.serviceconnector.sc.service;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.common.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.sc.service.SCMPMessageFilterMask;


public class SCMPMessageFilterMaskTest {

	@Test
	public void maskDoesntMatchTest() {
		String clientMask = "000012100012832102FADF-----------X-----------";
		SCMPMessageFilterMask clnMask = new SCMPMessageFilterMask(clientMask);
		SCMPMessage publishMsg = new SCMPMessage();

		// missing X in mask
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "0000121%%%%%%%%%%%%%%%-----------------------");
		Assert.assertFalse(clnMask.matches(publishMsg));

		// missing X in mask
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "000012100012832102FADF-----------------------");
		Assert.assertFalse(clnMask.matches(publishMsg));
	}

	@Test
	public void maskDoesMatchTest() {
		String clientMask = "000012100012832102FADF-----------X-----------";
		SCMPMessageFilterMask clnMask = new SCMPMessageFilterMask(clientMask);
		SCMPMessage publishMsg = new SCMPMessage();

		// wild card match
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "0000121%%%%%%%%%%%%%%%-----------X-----------");
		Assert.assertTrue(clnMask.matches(publishMsg));

		// mask are equally
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "000012100012832102FADF-----------X-----------");
		Assert.assertTrue(clnMask.matches(publishMsg));
	}
}
