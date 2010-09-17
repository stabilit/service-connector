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
package org.serviceconnector.common.scmp.internal;

import java.util.Map;

import org.apache.log4j.Logger;
import org.serviceconnector.common.scmp.SCMPMessage;


/**
 * The Class SCMPPart. Indicates this SCMP is a part of a bigger request/response. Request/Response is complete at
 * the time all parts are sent and put together.
 * 
 * @author JTraber
 */
public class SCMPPart extends SCMPMessage {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(SCMPPart.class);
	
	/**
	 * Instantiates a new SCMPPart.
	 */
	public SCMPPart() {
	}

	/**
	 * Instantiates a new SCMPPart.
	 * 
	 * @param map
	 *            the map
	 */
	public SCMPPart(Map<String, String> map) {
		this.header = map;
	}

	/** {@inheritDoc} */
	public boolean isPart() {
		return true;
	}
}