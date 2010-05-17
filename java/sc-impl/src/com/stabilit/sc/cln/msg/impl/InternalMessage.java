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
	/** The msgType. */
	private SCMPMsgType msgType;
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
		this.msgType = key;
		this.attrMap = new HashMap<String, Object>();
		this.encodedBuilder = null;
	}

	/** {@inheritDoc} */
	@Override
	public IInternalMessage newInstance() {
		return new InternalMessage();
	}

	/** {@inheritDoc} */
	@Override
	public SCMPMsgType getKey() {
		return msgType;
	}

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
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

	/** {@inheritDoc} */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Msg [key=" + msgType + "]");
		for (String name : attrMap.keySet()) {
			sb.append(" ");
			sb.append(name);
			sb.append("=");
			sb.append(attrMap.get(name));
		}
		return sb.toString();
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, Object> getAttributeMap() {
		return attrMap;
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}

	/** {@inheritDoc} */
	@Override
	public int getLength() {
		if (this.encodedBuilder == null) {
			this.encodedBuilder = new StringBuilder();
			encode(this.encodedBuilder);
		}
		return this.encodedBuilder.length();
	}
}
