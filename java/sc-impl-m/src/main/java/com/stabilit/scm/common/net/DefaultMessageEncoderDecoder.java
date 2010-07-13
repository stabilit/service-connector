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
package com.stabilit.scm.common.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.SCMPPoint;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPInternalStatus;

/**
 * The Class DefaultEncoderDecoder. Defines default SCMP encoding/decoding of object into/from stream.
 * 
 * @author JTraber
 */
public class DefaultMessageEncoderDecoder extends MessageEncoderDecoderAdapter {

	/**
	 * Instantiates a new default encoder decoder.
	 */
	DefaultMessageEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPMessage scmpMsg = (SCMPMessage) obj;

		if (scmpMsg.isGroup() == false) {
			// no group call reset internal status, if group call internal status already set
			scmpMsg.setInternalStatus(SCMPInternalStatus.NONE);
		}

		// evaluate right headline key from SCMP type
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.UNDEF;
		if (scmpMsg.isReply()) {
			headerKey = SCMPHeadlineKey.RES;
			if (scmpMsg.isFault()) {
				headerKey = SCMPHeadlineKey.EXC;
			}
		} else {
			headerKey = SCMPHeadlineKey.REQ;
		}

		Map<String, String> metaMap = scmpMsg.getHeader();
		StringBuilder sb = new StringBuilder();

		// write header fields
		Set<Entry<String, String>> entrySet = metaMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			key = key.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);
			if (value == null) {
				throw new EncodingDecodingException("key [" + key + "] has null value");
			}
			value = value.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);
			sb.append(key);
			sb.append(EQUAL_SIGN);
			sb.append(value);
			sb.append("\n");
		}
		int headerSize = sb.length();
		// write body depends on body type
		Object body = scmpMsg.getBody();
		try {
			if (body != null) {
				if (body instanceof byte[]) {
					byte[] ba = (byte[]) body;
					int messageLength = sb.length() + ba.length;
					writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString());
					bw.flush();
					os.write((byte[]) ba);
					os.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				}
				if (String.class == body.getClass()) {
					String t = (String) body;
					int messageLength = sb.length() + t.length();
					writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString()); // write header
					bw.flush();
					bw.write(t); // write body
					bw.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				}
				scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
				throw new EncodingDecodingException("unsupported body type");
			} else {
				writeHeadLine(bw, headerKey, headerSize, headerSize);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException e1) {
			ExceptionPoint.getInstance().fireException(this, e1);
			scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		SCMPPoint.getInstance().fireEncode(this, scmpMsg);
		return;
	}
}
