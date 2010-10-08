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
package org.serviceconnector.service;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class SubscriptionMask.
 */
public class SubscriptionMask {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SubscriptionMask.class);
	/** The mask in bytes. */
	private byte[] mask;

	/**
	 * Instantiates a new filter mask.
	 * 
	 * @param mask
	 *            the mask
	 */
	public SubscriptionMask(String mask) {
		this.mask = mask.getBytes();
	}

	/**
	 * Matches.
	 * 
	 * @param message
	 *            the message
	 * @return true, if successful
	 */
	public boolean matches(SCMPMessage message) {
		String msgMask = message.getHeader(SCMPHeaderAttributeKey.MASK);
		byte[] msgMaskByte = msgMask.getBytes();

		if (mask.length != msgMaskByte.length) {
			return false;
		}
		for (int byteIndex = 0; byteIndex < mask.length; byteIndex++) {
			if (msgMaskByte[byteIndex] == 0x25) {
				continue;
			}
			if (mask[byteIndex] != msgMaskByte[byteIndex]) {
				return false;
			}
		}
		return true;
	}
}
