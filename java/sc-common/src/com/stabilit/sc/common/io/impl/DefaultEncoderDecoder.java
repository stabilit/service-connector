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

import com.stabilit.sc.common.io.IEncoderDecoder;
import com.stabilit.sc.common.io.IMessage;
import com.stabilit.sc.common.io.SCMP;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderKey;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;

public class DefaultEncoderDecoder implements IEncoderDecoder {

	public DefaultEncoderDecoder() {
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

		String scmpBodyType = metaMap.get(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
		String scmpBodyLength = metaMap.get(SCMPHeaderAttributeType.BODY_LENGTH.getName());
		TYPE scmpBodyTypEnum = TYPE.getEnumType(scmpBodyType);
		scmp.setHeader(metaMap);
		try {
			if (scmpBodyTypEnum == TYPE.STRING) {
				int caLength = Integer.parseInt(scmpBodyLength);
				char[] caBuffer = new char[caLength];
				br.read(caBuffer);
				String bodyString = new String(caBuffer, 0, caLength);
				scmp.setBody(bodyString);
				return scmp;
			}
			if (scmpBodyTypEnum == TYPE.MESSAGE) {
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
			if (scmpBodyTypEnum == TYPE.ARRAY) {
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
		Map<String, String> metaMap = scmp.getHeader();
		// create meta part
		StringBuilder sb = new StringBuilder();
		
//		String messageType = scmp.getMessageType(); // messageType is never null
//		if (messageType == null) {
//			throw new EncodingDecodingException("No messageType found (null)");
//		}
		
		SCMPHeaderKey headerKey = SCMPHeaderKey.UNDEF;
		if(scmp.isReply()) {
			headerKey = SCMPHeaderKey.RES;
			if(scmp.isFault()) {
				headerKey = SCMPHeaderKey.EXC;
			}
		} else {
			headerKey = SCMPHeaderKey.REQ;
		}
//		if (messageType.startsWith("REQ_")) {
//			headerKey = SCMPHeaderKey.REQ;
//		} else if (messageType.startsWith("RES_")) {
//			if (scmp.isFault()) {
//				headerKey = SCMPHeaderKey.EXC;
//			} else {
//				headerKey = SCMPHeaderKey.RES;
//			}
//		}
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
					sb.append(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
					sb.append(EQUAL_SIGN);
					sb.append(TYPE.STRING.getType());
					sb.append("\n");
					sb.append(SCMPHeaderAttributeType.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(t.length()));
					sb.append("\n\n");
					int messageLength = sb.length() + t.length();
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());  //  write header
					bw.write(t);              // write body
					bw.flush();
					return;
				}
				if (body instanceof IMessage) {
					sb.append(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
					sb.append(EQUAL_SIGN);
					sb.append(TYPE.MESSAGE.getType());
					sb.append("\n\n");
					IMessage message = (IMessage) body;
					int messageLength = sb.length() + message.getLength();
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					message.encode(bw);
					bw.flush();
					return;
				}
				if (body instanceof byte[]) {
					sb.append(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
					sb.append(EQUAL_SIGN);
					sb.append(TYPE.ARRAY.getType());
					sb.append("\n");
					byte[] ba = (byte[]) body;
					sb.append(SCMPHeaderAttributeType.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(ba.length));
					sb.append("\n\n");
					int messageLength = sb.length() + ba.length;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					os.write((byte[]) ba);
					os.flush();
					return;
				} else {
					throw new EncodingDecodingException("unsupported body type");
				}
			} else {
				int messageLength = sb.length();
				writeHeadLine(bw, headerKey, messageLength);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException e1) {
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		return;
	}

	private void writeHeadLine(BufferedWriter bw, SCMPHeaderKey headerKey, int messageLength)
			throws IOException {
		bw.write(headerKey.toString());
		bw.write(" /s=");
		bw.write(String.valueOf(messageLength));
		bw.write("& SCMP/");
		bw.append(SCMP.SCMP_VERSION);
		bw.append("\n");
	}
}
