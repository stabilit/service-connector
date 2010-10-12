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
package org.serviceconnector.scmp;

import org.apache.log4j.Logger;

/**
 * The Class ResponseAdapter. Provides basic functionality for responses.
 * 
 * @author JTraber
 */
public abstract class ResponseAdapter implements IResponse {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(ResponseAdapter.class);
	
	/** The scmp. */
	protected SCMPMessage scmp;

	/**
	 * Instantiates a new response adapter.
	 */
	public ResponseAdapter() {
		this.scmp = null;
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMessage getSCMP() {
		return this.scmp;
	}

	/** {@inheritDoc} */
	@Override
	public void setSCMP(SCMPMessage scmp) {
		this.scmp = scmp;
	}

	/** {@inheritDoc} */
	@Override
	public boolean isLarge() {
		if (this.scmp == null) {
			return false;
		}
		return this.scmp.isLargeMessage();
	}
}
