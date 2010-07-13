/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.cln.service;

/**
 * @author JTraber
 */
public class SCMessage {

	private String messageInfo;
	private Boolean compressed;
	private Object data;

	public SCMessage() {
		this.messageInfo = null;
		this.compressed = null;
		this.data = null;
	}

	public SCMessage(Object data) {
		this();
		this.data = data;
	}

	public void setMessageInfo(String messageInfo) {

		this.messageInfo = messageInfo;
	}

	public String getMessageInfo() {
		return messageInfo;
	}

	public Boolean isCompressed() {
		return compressed;
	}

	public void setCompressed(Boolean compressed) {
		this.compressed = compressed;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
	public Object getData() {
		return this.data;
	}
}
