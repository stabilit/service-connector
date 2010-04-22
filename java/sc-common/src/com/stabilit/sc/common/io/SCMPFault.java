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
package com.stabilit.sc.common.io;

import java.util.Map;

import com.stabilit.sc.common.util.DateTimeUtility;

/**
 * @author JTraber
 * 
 */
public class SCMPFault extends SCMP {

	private static final long serialVersionUID = -4041668035605907106L;

	public SCMPFault() {
		super();
	}
	
	public SCMPFault(Map<String, String> map) {
		this.header = map;
	}

	public SCMPFault(SCMPErrorCode errorCode) {
		setError(errorCode);
	}
	
	public void setLocalDateTime() {
		header.put(SCMPHeaderAttributeKey.LOCAL_DATE_TIME.getName(), DateTimeUtility.getCurrentTimeZoneMillis());
	}
	
	@Override
	public boolean isFault() {
		return true;
	}

	@Override
	public boolean isReply() {
		return true;
	}

	public void setError(String errorCode, String errorText) {
		header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode);
		header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorText);
	}

	public void setError(SCMPErrorCode errorCode) {
		header.put(SCMPHeaderAttributeKey.SC_ERROR_CODE.getName(), errorCode.getErrorCode());
		header.put(SCMPHeaderAttributeKey.SC_ERROR_TEXT.getName(), errorCode.getErrorText());
	}
}