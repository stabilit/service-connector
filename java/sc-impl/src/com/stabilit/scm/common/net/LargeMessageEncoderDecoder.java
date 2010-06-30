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
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPInternalStatus;

/**
 * The Class LargeMessageEncoderDecoder. Defines large SCMP encoding/decoding of object into/from stream.
 */
public class LargeMessageEncoderDecoder extends MessageEncoderDecoderAdapter {

	/**
	 * Instantiates a new large message encoder decoder.
	 */
	LargeMessageEncoderDecoder() {
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
			if (scmpMsg.isFault()) {
				headerKey = SCMPHeadlineKey.EXC;
			} else {
				if (scmpMsg.isPart()) {
					headerKey = SCMPHeadlineKey.PRS;
				} else {
					headerKey = SCMPHeadlineKey.RES;
				}
			}
		} else {
			if (scmpMsg.isPart() || scmpMsg.isComposite()) {
				headerKey = SCMPHeadlineKey.PRQ;
			} else {
				headerKey = SCMPHeadlineKey.REQ;
			}
		}

		Map<String, String> metaMap = scmpMsg.getHeader();
		StringBuilder sb = new StringBuilder();
		// write header fields
		Set<Entry<String, String>> entrySet = metaMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();

			if (this.isIgnoreHeader(key) == false) {
				// some header need to be ignored because they are dependent to large message - now we are encoding
				// only a part
				String value = entry.getValue();
				if (value == null) {
					throw new EncodingDecodingException("key [" + key + "] has null value");
				}
				key = key.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);
				value = value.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);
				sb.append(key);
				sb.append(EQUAL_SIGN);
				sb.append(value);
				sb.append("\n");
			}
		}
		
		// write body depends on body type
		try {
			Object body = scmpMsg.getBody();
			if (body != null) {
				if (String.class == body.getClass()) {
					String t = (String) body;
					int bodyLength = scmpMsg.getBodyLength(); // returns body length of part only
					// this is a message part, we need to redefine the body length (message part length)
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(bodyLength));
					sb.append("\n");
					int headerSize = sb.length();
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString());
					bw.flush();
					// gets the offset of body - body of part message is written
					int bodyOffset = scmpMsg.getBodyOffset();
					bw.write(t, bodyOffset, bodyLength);
					bw.flush();
					// set internal status to save communication state
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				}

				if (byte[].class == body.getClass()) {
					byte[] ba = (byte[]) body;
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					int bodyLength = scmpMsg.getBodyLength(); // returns body length of part only
					sb.append(String.valueOf(bodyLength));
					sb.append("\n");
					int headerSize = sb.length();
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString());
					bw.flush();
					int bodyOffset = scmpMsg.getBodyOffset();
					os.write((byte[]) ba, bodyOffset, bodyLength);
					os.flush();
					// set internal status to save communication state
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				} else {
					// set internal status to save communication state
					scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
					throw new EncodingDecodingException("unsupported large message body type");
				}
			} else {
				int headerSize = sb.length();
				writeHeadLine(bw, headerKey, headerSize, headerSize);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException e1) {
			ExceptionPoint.getInstance().fireException(this, e1);
			scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		// set internal status to save communication state
		scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		SCMPPoint.getInstance().fireEncode(this, scmpMsg);
		return;
	}

	/**
	 * Checks if is ignore header.
	 * 
	 * @param key
	 *            the key
	 * @return true, if is ignore header
	 */
	private boolean isIgnoreHeader(String key) {
		// Body length has to be ignored because is dependent to large SCMP body not to part body
		if (SCMPHeaderAttributeKey.BODY_LENGTH.getName().equals(key)) {
			return true;
		}
		return false;
	}
}
