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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.regex.Matcher;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.SCMPPoint;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPInternalStatus;
import com.stabilit.scm.common.scmp.internal.SCMPPart;

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
	public Object decode(InputStream is) throws Exception {
		InputStreamReader isr = new InputStreamReader(is, CHARSET);
		BufferedReader br = new BufferedReader(isr);
		Map<String, String> metaMap = new HashMap<String, String>();
		// read heading line
		SCMPMessage scmpMsg = null;
		int readBytes = 0;
		try {
			String line = br.readLine(); // TODO performance
			readBytes += line.getBytes().length;
			readBytes += 1; // read LF
			if (line == null || line.length() <= 0) {
				return null;
			}
			// evaluating headline key and creating corresponding SCMP type
			SCMPHeadlineKey headlineKey = SCMPHeadlineKey.getKeyByHeadline(line);
			switch (headlineKey) {
			case PRQ:
			case PRS:
				scmpMsg = new SCMPPart();
				break;
			case EXC:
				scmpMsg = new SCMPFault();
				break;
			case UNDEF:
				throw new EncodingDecodingException("wrong protocol in message not possible to decode");
			default:
				scmpMsg = new SCMPMessage();
			}
			// storing header fields in meta map
			while (true) {
				line = br.readLine(); // TODO performance
				readBytes += line.getBytes().length;
				readBytes += 1; // read LF
				if (line == null || line.length() <= 0) {
					break;
				}

				Matcher match = IEncoderDecoder.DECODE_REG.matcher(line);
				if (match.matches() && match.groupCount() == 2) {
					String key = match.group(1).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
					String value = match.group(2).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
					metaMap.put(key, value);
				}
			}
		} catch (IOException e1) {
			ExceptionPoint.getInstance().fireException(this, e1);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		// reading body - depends on body type
		String scmpBodyTypeString = metaMap.get(SCMPHeaderAttributeKey.BODY_TYPE.getName());
		SCMPBodyType scmpBodyTyp = SCMPBodyType.getBodyType(scmpBodyTypeString);
		String scmpBodyLength = metaMap.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
		scmpMsg.setHeader(metaMap);
		try {
			switch (scmpBodyTyp) {
			case binary:
			case undefined:
				return this.decodeBinaryData(is, scmpMsg, readBytes, scmpBodyLength);
			case text:
				return this.decodeTextData(br, scmpMsg, scmpBodyLength);
			case internalMessage:
				return this.decodeInternalMessage(br, scmpMsg);
			}
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		SCMPPoint.getInstance().fireDecode(this, scmpMsg);
		return scmpMsg;
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
					sb.append("\n\n");
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength);
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
					sb.append("\n\n");
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength);
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
				sb.append("\n");
				int messageLength = sb.length();
				writeHeadLine(bw, headerKey, messageLength);
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
	 * Write head line.
	 * 
	 * @param bw
	 *            the bw
	 * @param headerKey
	 *            the header key
	 * @param messageLength
	 *            the message length
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	private void writeHeadLine(BufferedWriter bw, SCMPHeadlineKey headerKey, int messageLength) throws IOException {
		bw.write(headerKey.toString());
		bw.write(" /s=");
		bw.write(String.valueOf(messageLength));
		bw.write("& SCMP/");
		bw.append(SCMPMessage.SCMP_VERSION.toString());
		bw.append("\n");
		bw.flush();
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
