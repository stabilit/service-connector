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

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.listener.WarningListenerSupport;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPPart;

/**
 * @author JTraber
 * 
 */
public class SCMPComposite extends SCMP {

	private List<SCMP> scmpList;
	private SCMP partRequest;
	private SCMPFault scmpFault;
	private int scmpOffset;
	private ByteArrayOutputStream os;
	private StringWriter w;

	public SCMPComposite(SCMP request, SCMP scmpPart) {
		this.os = null;
		this.w = null;
		this.scmpOffset = 0;
		this.scmpFault = null;
		scmpList = new ArrayList<SCMP>();
		partRequest = new SCMPPart();
		partRequest.setMessageType(request.getMessageType());
		partRequest.setSessionId(request.getSessionId());
		partRequest.setHeader(request, SCMPHeaderAttributeKey.SERVICE_NAME); // tries to set service name
		partRequest.setHeader(request, SCMPHeaderAttributeKey.MAX_NODES); // tries to set maxNodes
		partRequest.setHeader(scmpPart, SCMPHeaderAttributeKey.BODY_TYPE); // tries to set bodyType
		this.add(scmpPart);
	}

	@Override
	public Map<String, String> getHeader() {
		partRequest.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, this.getBodyLength());
		return partRequest.getHeader();
	}

	public void add(SCMP scmp) {
		if (scmp == null) {
			return;
		}
		if (scmp.isFault()) {
			this.scmpList.clear();
			this.scmpFault = (SCMPFault) scmp;
			reset();
		}
		int bodyLength = scmp.getBodyLength();
		this.scmpOffset += bodyLength;
		this.scmpList.add(scmp);
		if (scmp.isPart() == false) {
			this.setHeader(scmp.getHeader());
			this.setHeader(SCMPHeaderAttributeKey.BODY_LENGTH, getBodyLength());
		}
	}

	@Override
	public boolean isFault() {
		if (this.scmpFault != null) {
			return true;
		}
		return false;
	}

	@Override
	public boolean isComposite() {
		return true;
	}

	@Override
	public int getBodyLength() {
		if (this.scmpFault != null) {
			return scmpFault.getBodyLength();
		}
		Object body = this.getBody();
		if (body == null) {
			return 0;
		}
		if (this.os != null) {
			return this.os.toByteArray().length;
		}
		if (this.w != null) {
			return this.w.toString().length();
		}
		return 0;
	}

	@Override
	public Object getBody() {
		if (this.os != null) {
			return this.os.toByteArray();
		}
		if (this.w != null) {
			return this.w.toString();
		}
		if (this.scmpFault != null) {
			return scmpFault.getBody();
		}
		SCMP firstScmp = this.scmpList.get(0);
		if (firstScmp == null) {
			return 0;
		}
		if (firstScmp.isByteArray()) {
			this.os = new ByteArrayOutputStream();
			try {
				for (SCMP scmp : this.scmpList) {
					int bodyLength = scmp.getBodyLength();
					if (bodyLength > 0) {
						Object body = scmp.getBody();
						if (body == null) {
							WarningListenerSupport.getInstance().fireWarning(this,
									"bodyLength > 0 but body == null");
						}
						this.os.write((byte[]) body);
					}
				}
				this.os.flush();
			} catch (Exception e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				return null;
			}
			this.os.toByteArray();
		}
		if (firstScmp.isString()) {
			this.w = new StringWriter();
			try {
				for (SCMP scmp : this.scmpList) {
					int bodyLength = scmp.getBodyLength();
					if (bodyLength > 0) {
						Object body = scmp.getBody();
						this.w.write((String) body);
					}
				}
				this.w.flush();
			} catch (Exception e) {
				ExceptionListenerSupport.getInstance().fireException(this, e);
				return null;
			}
			return this.w.toString();
		}
		return null;
	}

	@Override
	public String getMessageType() {
		return partRequest.getMessageType();
	}

	public SCMP getPartRequest() {
		return partRequest;
	}

	public int getOffset() {
		return this.scmpOffset;
	}

	private void reset() {
		this.partRequest = null;
		this.scmpList.clear();
		this.scmpOffset = 0;
		this.os = null;
		this.w = null;
	}

}
