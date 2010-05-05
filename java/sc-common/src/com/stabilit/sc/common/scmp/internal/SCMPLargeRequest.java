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
package com.stabilit.sc.common.scmp.internal;

import com.stabilit.sc.common.scmp.SCMP;


/**
 * @author JTraber
 * 
 */
public class SCMPLargeRequest extends SCMP {

	private SCMP scmp;
	private int offset;
	private int scmpCallLength;
	private SCMP current;
	
	public SCMPLargeRequest(SCMP scmp) {
		this.scmp = scmp;
		this.scmpCallLength = this.scmp.getBodyLength();
		this.offset = 0;
		this.current = null;
	}
	
	public SCMP getFirst() {
		this.offset = 0;
		this.current = new SCMPRequestPart(this.scmp, this.offset);
		this.offset += current.getBodyLength();		
		return this.current;
	}
	
	public boolean hasNext() {
        return this.offset < this.scmpCallLength;		
	}
	
	public SCMP getNext() {
	    if (this.hasNext()) {
			this.current = new SCMPRequestPart(scmp, this.offset);			    
			this.offset += current.getBodyLength();
			return this.current;
	    }
	    this.current = null;
	    return this.current;
	}			
}
