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
package com.stabilit.scm.common.msg.impl;

import com.stabilit.scm.common.scmp.SCMPMsgType;

/**
 * The Class EchoMessage. Echo Message used for testing/maintaining.
 */
public class EchoMessage extends InternalMessage {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -5461603317301105352L;
	/** The ID. */
	private static final SCMPMsgType ID = SCMPMsgType.CLN_ECHO;

	/**
	 * Instantiates a new echo message.
	 */
	public EchoMessage() {
		super(ID);
	}
}
