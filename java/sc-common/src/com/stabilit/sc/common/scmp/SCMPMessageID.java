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
public class SCMPMessageID {

	private int msgSequenceNr;
	private int partSequenceNr;
	private StringBuilder sb;

	public SCMPMessageID() {
		this.msgSequenceNr = 0;
		this.partSequenceNr = 0;
		this.sb = null;
	}

//	public String getNextMessageId(SCMP scmp) {
//		if (scmp.isPart()) {
//			sb = new StringBuilder();
//			if (partSequenceNr == 0)
//				msgSequenceNr++;
//			sb.append(msgSequenceNr);
//			sb.append("/");
//			sb.append(++partSequenceNr);
//			return sb.toString();
//		}
//		if (partSequenceNr != 0) {
//			sb = new StringBuilder();
//			sb.append(msgSequenceNr);
//			sb.append("/");
//			sb.append(++partSequenceNr);
//			partSequenceNr = 0;
//			return sb.toString();
//		}
//		return String.valueOf(++msgSequenceNr);
//	}
	
	public String getNextMessageID() {
		if(partSequenceNr == 0) {
			return String.valueOf(msgSequenceNr);
		}
		sb = new StringBuilder();
		sb.append(msgSequenceNr);
		sb.append("/");
		sb.append(partSequenceNr);
		return sb.toString();
	}
	
	public void incrementPartSequenceNr() {
		partSequenceNr++;
	}
	
	public void incrementMsgSequenceNr() {
		msgSequenceNr++;
	}

	public Integer getMessageSequenceNr() {
		return msgSequenceNr;
	}

	public Integer getPartSequenceNr() {
		return partSequenceNr;
	}
}
