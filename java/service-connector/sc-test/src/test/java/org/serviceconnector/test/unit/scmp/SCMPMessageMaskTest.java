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
package org.serviceconnector.test.unit.scmp;

import org.junit.Assert;
import org.junit.Test;
import org.serviceconnector.TestConstants;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPVersion;
import org.serviceconnector.service.InvalidMaskLengthException;
import org.serviceconnector.service.SubscriptionMask;

public class SCMPMessageMaskTest {

	/**
	 * Description: Invalid mask test<br>
	 * Expectation: passes
	 */
	@Test
	public void t01_maskDoesntMatchTest() {
		String clientMask = "000012100012832102FADF-----------X-----------";
		SubscriptionMask clnMask = new SubscriptionMask(clientMask);
		SCMPMessage publishMsg = new SCMPMessage(SCMPVersion.CURRENT);

		// missing X in mask
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "0000121%%%%%%%%%%%%%%%-----------------------");
		Assert.assertFalse(clnMask.matches(publishMsg));

		// missing X in mask
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "000012100012832102FADF-----------------------");
		Assert.assertFalse(clnMask.matches(publishMsg));
	}

	/**
	 * Description: Valid mask test<br>
	 * Expectation: passes
	 */
	@Test
	public void t10_maskDoesMatchTest() {
		String clientMask = "000012100012832102FADF-----------X-----------";
		SubscriptionMask clnMask = new SubscriptionMask(clientMask);
		SCMPMessage publishMsg = new SCMPMessage(SCMPVersion.CURRENT);

		// wild card match
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "0000121%%%%%%%%%%%%%%%-----------X-----------");
		Assert.assertTrue(clnMask.matches(publishMsg));

		// mask are equally
		publishMsg.setHeader(SCMPHeaderAttributeKey.MASK, "000012100012832102FADF-----------X-----------");
		Assert.assertTrue(clnMask.matches(publishMsg));
	}

	/**
	 * Description: Combine to masks<br>
	 * Expectation: passes
	 *
	 * @throws InvalidMaskLengthException
	 */
	@Test
	public void t15_maskingTest() throws InvalidMaskLengthException {
		byte[] combinedMask = SubscriptionMask.masking(TestConstants.mask.getBytes(), TestConstants.mask1.getBytes());
		Assert.assertEquals("Combination of mask is wrong", TestConstants.combinedMask, new String(combinedMask));
	}
}
