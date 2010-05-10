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
package com.stabilit.sc.scmp.internal;

import com.stabilit.sc.scmp.SCMP;

/**
 * The Class SCMPCompositeSender. Used to handle outgoing large request/response. Works like an iterator and provides
 * functionality of splitting large SCMP into parts.
 * 
 * @author JTraber
 */
public class SCMPCompositeSender extends SCMP {

	/** The large scmp. */
	private SCMP scmp;	
	/** The offset. */
	private int offset;	
	/** The scmp call length. */
	private int scmpCallLength;	
	/** The current part. */
	private SCMP current;

	/**
	 * Instantiates a new SCMPCompositeSender.
	 * 
	 * @param scmp the scmp
	 */
	public SCMPCompositeSender(SCMP scmp) {
		this.scmp = scmp;
		this.scmpCallLength = this.scmp.getBodyLength();
		this.offset = 0;
		this.current = null;
	}

	/**
	 * Gets the first part.
	 * 
	 * @return the first
	 */
	public SCMP getFirst() {
		this.offset = 0;
		this.current = new SCMPSendPart(this.scmp, this.offset);
		this.offset += current.getBodyLength();
		return this.current;
	}

	/**
	 * Checks for next part.
	 * 
	 * @return true, if successful
	 */
	public boolean hasNext() {
		return this.offset < this.scmpCallLength;
	}

	/**
	 * Gets the next part.
	 * 
	 * @return the next
	 */
	public SCMP getNext() {
		if (this.hasNext()) {
			this.current = new SCMPSendPart(scmp, this.offset);
			this.offset += current.getBodyLength();
			return this.current;
		}
		this.current = null;
		return this.current;
	}
}
