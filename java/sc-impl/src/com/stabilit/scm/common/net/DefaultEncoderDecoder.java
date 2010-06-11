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
import com.stabilit.scm.common.log.listener.ExceptionPoint;
import com.stabilit.scm.common.log.listener.SCMPPoint;
import com.stabilit.scm.common.scmp.IInternalMessage;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPInternalStatus;

/**
 * The Class DefaultEncoderDecoder. Defines default SCMP encoding/decoding of object into/from stream.
 * 
 * @author JTraber
 */
public class DefaultEncoderDecoder implements IEncoderDecoder {

	/**
	 * Instantiates a new default encoder decoder.
	 */
	DefaultEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	@SuppressWarnings("unchecked")
	public Object decode(InputStream is) throws EncodingDecodingException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Map<String, String> metaMap = new HashMap<String, String>();
		// read heading line
		String line;
		SCMPMessage scmpMsg = null;
		int readBytes = 0;
		try {
			line = br.readLine(); // TODO performance
			readBytes += line.getBytes().length;
			readBytes += 1; // read LF

			if (line == null || line.length() <= 0) {
				return null;
			}

			// evaluating headline key and creating corresponding SCMP type
			SCMPHeadlineKey headlineKey = SCMPHeadlineKey.getKeyByHeadline(line);
			switch (headlineKey) {
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
		String scmpBodyLength = metaMap.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
		SCMPBodyType scmpBodyType = SCMPBodyType.getBodyType(scmpBodyTypeString);
		scmpMsg.setHeader(metaMap);
		try {
			if (scmpBodyType == SCMPBodyType.text) {
				int caLength = Integer.parseInt(scmpBodyLength);
				char[] caBuffer = new char[caLength];
				br.read(caBuffer);
				String bodyString = new String(caBuffer, 0, caLength);
				scmpMsg.setBody(bodyString);
				return scmpMsg;
			}
			if (scmpBodyType == SCMPBodyType.internalMessage) {
				String classLine = br.readLine();
				if (classLine == null) {
					return null;
				}
				String[] t = classLine.split(EQUAL_SIGN);
				if (IInternalMessage.class.getName().equals(t[0]) == false) {
					return null;
				}
				if (t.length != 2) {
					return null;
				}
				Class messageClass = Class.forName(t[1]);
				IInternalMessage message = (IInternalMessage) messageClass.newInstance();
				message.decode(br);
				scmpMsg.setBody(message);
				return scmpMsg;
			}
			if (scmpBodyType == SCMPBodyType.binary) {
				int baLength = Integer.parseInt(scmpBodyLength);
				byte[] baBuffer = new byte[baLength];
				is.reset();
				is.skip(readBytes);
				is.read(baBuffer);
				scmpMsg.setBody(baBuffer);
				return scmpMsg;
			}
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		SCMPPoint.getInstance().fireDecode(this, scmpMsg);
		return scmpMsg;
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws EncodingDecodingException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPMessage scmpMsg = (SCMPMessage) obj;

		if (scmpMsg.isGroup() == false) {
			// no group call reset internal status, if group call internal status already set
			scmpMsg.setInternalStatus(SCMPInternalStatus.NONE);
		}

		Map<String, String> metaMap = scmpMsg.getHeader();
		StringBuilder sb = new StringBuilder();

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

		// write body depends on body type
		Object body = scmpMsg.getBody();
		try {
			if (body != null) {
				if (String.class == body.getClass()) {
					String t = (String) body;
					sb.append("\n");
					int messageLength = sb.length() + t.length();
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString()); // write header
					bw.flush();
					bw.write(t); // write body
					bw.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				}
				if (body instanceof IInternalMessage) {
					sb.append("\n");
					IInternalMessage message = (IInternalMessage) body;
					int messageLength = sb.length() + message.getLength();
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					message.encode(bw);
					bw.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				}
				if (body instanceof byte[]) {
					byte[] ba = (byte[]) body;
					sb.append("\n");
					int messageLength = sb.length() + ba.length;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					os.write((byte[]) ba);
					os.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					SCMPPoint.getInstance().fireEncode(this, scmpMsg);
					return;
				} else {
					scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
					throw new EncodingDecodingException("unsupported body type");
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
		scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		SCMPPoint.getInstance().fireEncode(this,scmpMsg);
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
}
