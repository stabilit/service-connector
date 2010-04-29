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
import com.stabilit.sc.common.scmp.SCMPPart;

/**
 * @author JTraber
 * 
 */
public class SCMPResponsePart extends SCMPPart {

	private int offset;
	private int size;
	private int callLength;

	public SCMPResponsePart(SCMP scmp, int offset) {
		this.offset = offset;
		this.callLength = scmp.getBodyLength();
		this.size = this.callLength - this.offset < SCMP.LARGE_MESSAGE_LIMIT ? this.callLength - this.offset
				: SCMP.LARGE_MESSAGE_LIMIT;
		this.setHeader(scmp);
		this.setBody(scmp.getBody());
	}

	@Override
	public boolean isPart() {
		return offset + size < callLength;
	}

	@Override
	public boolean isReply() {
		return true;
	}

	@Override
	public boolean isBodyOffset() {
		return true;
	}

	public int getBodyOffset() {
		return offset;
	}

	@Override
	public int getBodyLength() {
		return this.size;
	}
}
