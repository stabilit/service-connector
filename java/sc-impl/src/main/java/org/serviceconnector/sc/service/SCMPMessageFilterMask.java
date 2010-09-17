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
package org.serviceconnector.sc.service;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.IFilterMask;


/**
 * The Class FilterMask.
 */
public class SCMPMessageFilterMask implements IFilterMask<SCMPMessage> {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(SCMPMessageFilterMask.class);
	
	/** The mask in bytes. */
	private byte[] mask;

	/**
	 * Instantiates a new filter mask.
	 * 
	 * @param mask
	 *            the mask
	 */
	public SCMPMessageFilterMask(String mask) {
		this.mask = mask.getBytes();
	}

	/** {@inheritDoc} */
	@Override
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
