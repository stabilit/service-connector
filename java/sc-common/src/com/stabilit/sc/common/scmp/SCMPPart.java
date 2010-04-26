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
package com.stabilit.sc.common.scmp;

import java.util.Map;

/**
 * @author JTraber
 * 
 */
public class SCMPPart extends SCMP {
	
	private static final long serialVersionUID = -3379254138164380850L;
	private boolean isReply;

	public SCMPPart() {
		super();
		isReply = false;
	}

	public SCMPPart(Map<String, String> map) {
		this.header = map;
	}

	public boolean isPart() {
		return true;
	}
	
	public void setPartId(String messageId) {
		this.setHeader(SCMPHeaderAttributeKey.PART_ID, messageId);
	}
	
	public String getPartId() {
		return this.getHeader(SCMPHeaderAttributeKey.PART_ID);		
	}

	@Override
	public boolean isReply() {
		return isReply;
	}
	
	public void setIsReply(boolean isReply) {
		this.isReply = isReply;
	}
}