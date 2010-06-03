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
package com.stabilit.scm.cln.msg.impl;

import com.stabilit.scm.scmp.SCMPMsgType;

/**
 * The Class InspectMessage. Inspect Message used for testing/maintaining. Used to inspect data on SC.
 */
public class InspectMessage extends InternalMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 3298941284664209624L;
	/** The ID. */
	private static final SCMPMsgType ID = SCMPMsgType.INSPECT;

	/**
	 * Instantiates a new inspect message.
	 */
	public InspectMessage() {
		super(ID);
	}
}
