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
package com.stabilit.sc.common.scmp.impl;

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

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.scmp.IEncoderDecoder;
import com.stabilit.sc.common.scmp.IInternalMessage;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.SCMPBodyType;
import com.stabilit.sc.common.scmp.SCMPFault;
import com.stabilit.sc.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.scmp.SCMPHeadlineKey;
import com.stabilit.sc.common.scmp.SCMPInternalStatus;
import com.stabilit.sc.common.scmp.SCMPPart;

public class LargeMessageEncoderDecoder implements IEncoderDecoder {

	public LargeMessageEncoderDecoder() {
	}

	@Override
	public IFactoryable newInstance() {
		return this;
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object decode(InputStream is) throws EncodingDecodingException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Map<String, String> metaMap = new HashMap<String, String>();
		// read heading line
		SCMP scmp;
		try {
			String line = br.readLine(); // TODO
			if (line == null || line.length() <= 0) {
				return null;
			}
			scmp = null;

			SCMPHeadlineKey headlineKey = SCMPHeadlineKey.getKeyByHeadline(line);
			switch (headlineKey) {
			case PRQ:
			case PRS:
				scmp = new SCMPPart();
				break;
			case EXC:
				scmp = new SCMPFault();
				break;
			case UNDEF:
				throw new EncodingDecodingException("wrong protocol in message not possible to decode");
			default:
				scmp = new SCMP();
			}

			while (true) {
				line = br.readLine(); // TODO
				if (line == null || line.length() <= 0) {
					break;
				}
				
				Matcher match = IEncoderDecoder.DECODE_REG.matcher(line);
				if (match.matches() && match.groupCount() == 2) {
					/********* escaping *************/
					String key = match.group(1).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
					String value = match.group(2).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
					metaMap.put(key, value);
				}
			}
		} catch (IOException e1) {
			throw new EncodingDecodingException("io error when decoding message", e1);
		}

		String scmpBodyType = metaMap.get(SCMPHeaderAttributeKey.BODY_TYPE.getName());
		String scmpBodyLength = metaMap.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
		SCMPBodyType scmpBodyTypEnum = SCMPBodyType.getBodyType(scmpBodyType);
		scmp.setHeader(metaMap);
		try {
			if (scmpBodyTypEnum == SCMPBodyType.text) {
				int caLength = Integer.parseInt(scmpBodyLength);
				char[] caBuffer = new char[caLength];
				br.read(caBuffer);
				String bodyString = new String(caBuffer, 0, caLength);
				scmp.setBody(bodyString);
				return scmp;
			}
			if (scmpBodyTypEnum == SCMPBodyType.message) {
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
				scmp.setBody(message);
				return scmp;
			}
			if (scmpBodyTypEnum == SCMPBodyType.binary) {
				int baLength = Integer.parseInt(scmpBodyLength);
				byte[] baBuffer = new byte[baLength];
				is.read(baBuffer);
				scmp.setBody(baBuffer);
				return scmp;
			}
		} catch (Exception e) {
		}
		return scmp;
	}

	@Override
	public void encode(OutputStream os, Object obj) throws EncodingDecodingException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMP scmp = (SCMP) obj;

		scmp.setInternalStatus(SCMPInternalStatus.NONE);

		// message chunking
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.UNDEF;
		if (scmp.isReply()) {
			if (scmp.isFault()) {
				headerKey = SCMPHeadlineKey.EXC;
			} else {
				if (scmp.isPart()) {
					headerKey = SCMPHeadlineKey.PRS;
				} else {
					headerKey = SCMPHeadlineKey.RES;
				}
			}
		} else {
			if (scmp.isPart() || scmp.isComposite()) {
				headerKey = SCMPHeadlineKey.PRQ;
			} else {
				headerKey = SCMPHeadlineKey.REQ;
			}
		}
		Map<String, String> metaMap = scmp.getHeader();
		// create meta part
		StringBuilder sb = new StringBuilder();

		Set<Entry<String, String>> entrySet = metaMap.entrySet();

		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			if (this.isIgnoreHeader(key) == false) {
				String value = entry.getValue();
				if (value == null) {
					throw new EncodingDecodingException("key [" + key + "] has null value");
				}
				/********* escaping *************/
				key = key.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);
				value = value.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);

				sb.append(key);
				sb.append(EQUAL_SIGN);
				sb.append(value);
				sb.append("\n");
			}
		}
		try {
			Object body = scmp.getBody();
			if (body != null) {
				if (String.class == body.getClass()) {
					String t = (String) body;
					// message chunking
					int bodyLength = scmp.getBodyLength(); // returns body of part only
					// this is a scmp part, we need to redefine the body length (message part length)
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(bodyLength));
					sb.append("\n\n");
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					int bodyOffset = scmp.getBodyOffset();
					bw.write(t, bodyOffset, bodyLength);
					bw.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				}

				if (byte[].class == body.getClass()) {
					byte[] ba = (byte[]) body;
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					// message chunking
					int bodyLength = scmp.getBodyLength();
					sb.append(String.valueOf(bodyLength));
					sb.append("\n\n");
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					int bodyOffset = scmp.getBodyOffset();
					os.write((byte[]) ba, bodyOffset, bodyLength);
					os.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				} else {
					scmp.setInternalStatus(SCMPInternalStatus.FAILED);
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
			scmp.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		return;
	}

	private void writeHeadLine(BufferedWriter bw, SCMPHeadlineKey headerKey, int messageLength)
			throws IOException {
		bw.write(headerKey.toString());
		bw.write(" /s=");
		bw.write(String.valueOf(messageLength));
		bw.write("& SCMP/");
		bw.append(SCMP.SCMP_VERSION);
		bw.append("\n");
		bw.flush();
	}

	private boolean isIgnoreHeader(String key) {
		if (SCMPHeaderAttributeKey.BODY_LENGTH.getName().equals(key)) {
			return true;
		}
		return false;
	}
}
