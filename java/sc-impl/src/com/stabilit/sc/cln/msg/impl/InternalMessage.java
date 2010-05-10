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
package com.stabilit.sc.cln.msg.impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stabilit.sc.net.DefaultEncoderDecoder;
import com.stabilit.sc.scmp.IInternalMessage;
import com.stabilit.sc.scmp.SCMPMsgType;

/**
 * The Class InternalMessage. Internal Messages are used to communicate for testing / maintaining reasons.
 */
public class InternalMessage implements IInternalMessage, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -1763291531850424661L;
	/** The key. */
	private SCMPMsgType key;
	/** The attr map to store data. */
	protected Map<String, Object> attrMap;
	/** The encoded builder. */
	protected StringBuilder encodedBuilder;

	/**
	 * Instantiates a new internal message.
	 */
	public InternalMessage() {
		this(SCMPMsgType.UNDEFINED);
	}

	/**
	 * Instantiates a new internal message.
	 * 
	 * @param key
	 *            the key
	 */
	public InternalMessage(SCMPMsgType key) {
		this.key = key;
		this.attrMap = new HashMap<String, Object>();
		this.encodedBuilder = null;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#newInstance()
	 */
	@Override
	public IInternalMessage newInstance() {
		return new InternalMessage();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#getKey()
	 */
	@Override
	public SCMPMsgType getKey() {
		return key;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#encode(java.io.BufferedWriter)
	 */
	@Override
	public void encode(BufferedWriter bw) throws IOException {
		if (this.encodedBuilder == null) {
			this.encodedBuilder = new StringBuilder();
			encode(this.encodedBuilder);
		}
		bw.write(this.encodedBuilder.toString());
	}

	/**
	 * Encode.
	 * 
	 * @param eb
	 *            the eb
	 */
	private void encode(StringBuilder eb) {
		Map<String, Object> attrMap = this.getAttributeMap();
		Set<Entry<String, Object>> attrEntrySet = attrMap.entrySet();
		eb.append(IInternalMessage.class.getName());
		eb.append(DefaultEncoderDecoder.EQUAL_SIGN);
		eb.append(this.getClass().getName());
		eb.append("\n");
		for (Entry<String, Object> entry : attrEntrySet) {
			String key = entry.getKey();
			String value = entry.getValue().toString();
			key = key.replace(DefaultEncoderDecoder.EQUAL_SIGN, DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN);
			value = value.replace(DefaultEncoderDecoder.EQUAL_SIGN, DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN);

			eb.append(key);
			eb.append(DefaultEncoderDecoder.EQUAL_SIGN);
			eb.append(value);
			eb.append("\n");
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#decode(java.io.BufferedReader)
	 */
	@Override
	public void decode(BufferedReader br) throws IOException {
		while (true) {
			String line = br.readLine();
			if (line == null || line.length() <= 0) {
				break;
			}
			Pattern decodReg = Pattern.compile(DefaultEncoderDecoder.UNESCAPED_EQUAL_SIGN_REGEX);
			Matcher match = decodReg.matcher(line);
			if (match.matches() && match.groupCount() == 2) {
				String key = match.group(1).replace(DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN,
						DefaultEncoderDecoder.EQUAL_SIGN);
				String value = match.group(2).replace(DefaultEncoderDecoder.ESCAPED_EQUAL_SIGN,
						DefaultEncoderDecoder.EQUAL_SIGN);
				this.setAttribute(key, value);
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Msg [key=" + key + "]");
		for (String name : attrMap.keySet()) {
			sb.append(" ");
			sb.append(name);
			sb.append("=");
			sb.append(attrMap.get(name));
		}
		return sb.toString();
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#getAttributeMap()
	 */
	@Override
	public Map<String, Object> getAttributeMap() {
		return attrMap;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#getAttribute(java.lang.String)
	 */
	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#setAttribute(java.lang.String, java.lang.Object)
	 */
	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.scmp.IInternalMessage#getLength()
	 */
	@Override
	public int getLength() {
		if (this.encodedBuilder == null) {
			this.encodedBuilder = new StringBuilder();
			encode(this.encodedBuilder);
		}
		return this.encodedBuilder.length();
	}
}
