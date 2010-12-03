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
package org.serviceconnector.api;

import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

public class SCPublishMessage extends SCMessage {

	private String mask = null;

	public SCPublishMessage() {
	}

	public SCPublishMessage(byte[] data) {
		super(data);
	}

	public SCPublishMessage(String data) {
		super(data);
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) throws SCMPValidatorException {
		if (mask != null) {
			ValidatorUtility.validateStringLength(1, mask.trim(), 256, SCMPError.HV_WRONG_MASK);
		}
		this.mask = mask;
	}
}
