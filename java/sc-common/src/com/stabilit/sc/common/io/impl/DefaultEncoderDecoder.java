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
package com.stabilit.sc.common.io.impl;

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
import java.util.regex.Pattern;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IMessage;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPBodyType;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPHeadlineKey;
import com.stabilit.sc.common.io.SCMPInternalStatus;

public class DefaultEncoderDecoder implements IEncoderDecoder {

	public DefaultEncoderDecoder() {
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
		String line;
		SCMP scmp = null;
		try {
			// TODO performance
			line = br.readLine();

			if (line == null || line.length() <= 0) {
				return null;
			}

			Pattern decodHeadReg = Pattern.compile(HEADER_REGEX);
			Matcher matchHeadline = decodHeadReg.matcher(line);

			if (matchHeadline.matches() == false) {
				throw new EncodingDecodingException("wrong protocol in message not possible to decode");
			}

			if (line.startsWith("EXC ")) {
				scmp = new SCMPFault();
			} else {
				scmp = new SCMP();
			}
			while (true) {
				line = br.readLine(); // TODO
				if (line == null || line.length() <= 0) {
					break;
				}
				Pattern decodReg = Pattern.compile(UNESCAPED_EQUAL_SIGN_REGEX);
				Matcher match = decodReg.matcher(line);

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

		String scmpBodyTypeString = metaMap.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
		String scmpBodyLength = metaMap.get(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
		SCMPBodyType scmpBodyType = SCMPBodyType.getBodyType(scmpBodyTypeString);
		scmp.setHeader(metaMap);
		try {		
			if (scmpBodyType == SCMPBodyType.text) {
				int caLength = Integer.parseInt(scmpBodyLength);
				char[] caBuffer = new char[caLength];
				br.read(caBuffer);
				String bodyString = new String(caBuffer, 0, caLength);
				scmp.setBody(bodyString);
				return scmp;
			}
			if (scmpBodyType == SCMPBodyType.message) {
				String classLine = br.readLine();
				if (classLine == null) {
					return null;
				}
				String[] t = classLine.split(EQUAL_SIGN);
				if (IMessage.class.getName().equals(t[0]) == false) {
					return null;
				}
				if (t.length != 2) {
					return null;
				}
				Class messageClass = Class.forName(t[1]);
				IMessage message = (IMessage) messageClass.newInstance();
				message.decode(br);
				scmp.setBody(message);
				return scmp;
			}
			if (scmpBodyType == SCMPBodyType.binary) {
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
		Map<String, String> metaMap = scmp.getHeader();
		// create meta part
		StringBuilder sb = new StringBuilder();
				
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.UNDEF;
		if(scmp.isReply()) {
			headerKey = SCMPHeadlineKey.RES;
			if(scmp.isFault()) {
				headerKey = SCMPHeadlineKey.EXC;
			}
		} else {
			headerKey = SCMPHeadlineKey.REQ;
		}

		Set<Entry<String, String>> entrySet = metaMap.entrySet();

		for (Entry<String, String> entry : entrySet) {
			String key = entry.getKey();
			String value = entry.getValue();
			/********* escaping *************/
			key = key.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);
			value = value.replace(EQUAL_SIGN, ESCAPED_EQUAL_SIGN);

			sb.append(key);
			sb.append(EQUAL_SIGN);
			sb.append(value);
			sb.append("\n");
		}
		Object body = scmp.getBody();
		try {
			if (body != null) {
				if (String.class == body.getClass()) {
					String t = (String) body;
//					sb.append(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
//					sb.append(EQUAL_SIGN);
//					sb.append(SCMPBodyType.TEXT.getType());
//					sb.append("\n");
//					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
//					sb.append(EQUAL_SIGN);
//					sb.append(String.valueOf(t.length()));
//					sb.append("\n\n");
					sb.append("\n");
					int messageLength = sb.length() + t.length();
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());  //  write header
					bw.flush();
					bw.write(t);              // write body
					bw.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				}
				if (body instanceof IMessage) {
//					sb.append(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
//					sb.append(EQUAL_SIGN);
//					sb.append(SCMPBodyType.MESSAGE.getType());
//					sb.append("\n\n");
					sb.append("\n");
					IMessage message = (IMessage) body;
					int messageLength = sb.length() + message.getLength();
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					message.encode(bw);
					bw.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				}
				if (body instanceof byte[]) {
//					sb.append(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
//					sb.append(EQUAL_SIGN);
//					sb.append(SCMPBodyType.BINARY.getType());
//					sb.append("\n");
					byte[] ba = (byte[]) body;
//					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
//					sb.append(EQUAL_SIGN);
//					sb.append(String.valueOf(ba.length));
//					sb.append("\n\n");
					sb.append("\n");
				    int messageLength = sb.length() + ba.length;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					os.write((byte[]) ba);
					os.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				} else {
					scmp.setInternalStatus(SCMPInternalStatus.FAILED);
					throw new EncodingDecodingException("unsupported body type");
				}
			} else {
//				sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
//				sb.append(EQUAL_SIGN);
//				sb.append("0\n");
//				sb.append("\n\n");
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
}
