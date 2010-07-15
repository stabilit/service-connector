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

package com.stabilit.scm.common.service;

/**
 * The Class SCMessageFault.
 */
public class SCMessageFault extends SCMessage {

	private String appErrorCode;
	private String appErrorText;

	/**
	 * Checks if is fault.
	 * 
	 * @return true, if is fault
	 */
	@Override
	public boolean isFault() {
		return true;
	}

	public String getAppErrorCode() {
		return appErrorCode;
	}

	public void setAppErrorCode(String appErrorCode) {
		this.appErrorCode = appErrorCode;
	}

	public String getAppErrorText() {
		return appErrorText;
	}

	public void setAppErrorText(String appErrorText) {
		this.appErrorText = appErrorText;
	}
}
