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

/**
 * @author JTraber
 * 
 */
public enum SCMPHeadlineKey {

	UNDEF, REQ, RES, EXC, PRQ, PRS;
	
	public static SCMPHeadlineKey getMsgHeaderKey(String headerKey) {
		return SCMPHeadlineKey.valueOf(headerKey);
	}
	public static SCMPHeadlineKey getMsgHeaderKey(byte[]b) {
		if (b == null) {
			return UNDEF;
		}
		if (b.length < 3) {
			return UNDEF;
		}
		if (b[0] == 'R' && b[1] == 'E') {
			if (b[2] == 'Q') {
				return REQ;
			}
			if (b[2] == 'S') {
				return RES;
			}
			return UNDEF;			
		}
		if (b[0] == 'P' && b[1] == 'R') {
			if (b[2] == 'Q') {
				return PRQ;
			}
			if (b[2] == 'S') {
				return PRS;
			}
			return UNDEF;			
		}
		if (b[0] == 'E' && b[1] == 'X' &&  b[2] == 'C') {
			return EXC;
		}
		return UNDEF;
	}

}
