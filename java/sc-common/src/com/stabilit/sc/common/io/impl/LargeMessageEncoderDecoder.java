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
import com.stabilit.sc.common.io.SCMPHeaderType;
import com.stabilit.sc.common.io.SCMPPart;

public class LargeMessageEncoderDecoder implements IEncoderDecoder {

	public static final String UNESCAPED_EQUAL_SIGN_REGEX = "(.*)(?<!\\\\)=(.*)";
	public static final String ESCAPED_EQUAL_SIGN = "\\=";
	public static final String EQUAL_SIGN = "=";
	public static final String CHARSET = "UTF-8"; // TODO ISO gemäss doc

	public static Integer lastMessageID = 0;

	public LargeMessageEncoderDecoder() {
	}

	@Override
	@SuppressWarnings("unchecked")
	public Object decode(InputStream is) throws IOException, ClassNotFoundException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Map<String, String> metaMap = new HashMap<String, String>();
		// read heading line
		String line = br.readLine(); // TODO
		if (line == null || line.length() <= 0) {
			return null;
		}
		SCMP scmp = null;
		if (line.startsWith("EXC ")) {
			scmp = new SCMPFault();
		} else {
			if (line.charAt(0) == 'P') {
				scmp = new SCMPPart();
			} else {
				scmp = new SCMP();
			}
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
		String scmpBodyType = metaMap.get(SCMPHeaderType.SCMP_BODY_TYPE.getName());
		String scmpBodyLength = metaMap.get(SCMPHeaderType.BODY_LENGTH.getName());
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
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMP scmp = (SCMP) obj;
		// try to get message offset
		String scmpOffset = scmp.getHeader(SCMPHeaderType.SCMP_OFFSET.getName());
		String scmpSequenceNr = scmp.getHeader(SCMPHeaderType.SEQUENCE_NR.getName());
		if (scmpOffset == null) {
			scmpOffset = "0";
			scmpSequenceNr = "0";
			scmp.setHeader(SCMPHeaderType.SCMP_OFFSET.getName(), scmpOffset);
			scmp.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), "0");
			synchronized (LargeMessageEncoderDecoder.class) {
				scmp.setHeader(SCMPHeaderType.SCMP_MESSAGE_ID.getName(), lastMessageID++);
			}
		}
		int scmpOffsetInt = Integer.parseInt(scmpOffset);
		int scmpSequenceNrInt = Integer.parseInt(scmpSequenceNr);
		Map<String, String> metaMap = scmp.getHeader();
		// create meta part
		StringBuilder sb = new StringBuilder();
		String messageType = scmp.getMessageType(); // messageType is never null
		if (messageType == null) {
			throw new EncodingDecodingException("No messageType found (null)");
		}
		String headLineKey = null;
		if (messageType.startsWith("REQ_")) {
			if (scmp.isPart()) {
				headLineKey = "PRQ";
			} else {
				headLineKey = "REQ";

			}
		} else if (messageType.startsWith("RES_")) {
			if (scmp.isFault()) {
				headLineKey = "EXC";
			} else {
				headLineKey = "PRS";
			}
		}
		sb.append(" / SCMP/");
		sb.append(SCMP.VERSION);
		sb.append("\n");
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
		if (body != null) {
			if (String.class == body.getClass()) {
				String t = (String) body;
				sb.append(SCMPHeaderType.SCMP_BODY_TYPE.getName());
				sb.append(EQUAL_SIGN);
				sb.append(TYPE.STRING.getType());
				sb.append("\n");
				int bodyLength = scmp.getBodyLength();
				int messagePartLength = bodyLength;
				if (scmp.isPart() == false) {
					if (messagePartLength > SCMP.LARGE_MESSAGE_LIMIT) {
						messagePartLength = (bodyLength - scmpOffsetInt) > SCMP.LARGE_MESSAGE_LIMIT ? SCMP.LARGE_MESSAGE_LIMIT
								: bodyLength - scmpOffsetInt;
						if ("REQ".equals(headLineKey)) {
							if (bodyLength > messagePartLength + scmpOffsetInt) {
								headLineKey = "PRQ";
							}
						}
					}
				}
				sb.append(SCMPHeaderType.BODY_LENGTH.getName());
				sb.append(EQUAL_SIGN);
				sb.append(String.valueOf(messagePartLength));
				sb.append("\n");
				sb.append(SCMPHeaderType.SCMP_CALL_LENGTH.getName());
				sb.append(EQUAL_SIGN);
				sb.append(String.valueOf(bodyLength));
				sb.append("\n\n");
				bw.write(headLineKey);
				bw.write(sb.toString());
				if (scmp.isPart()) {
					bw.write(t, 0, messagePartLength);
				} else {
					bw.write(t, scmpOffsetInt, messagePartLength);
				}
				scmpOffsetInt += messagePartLength;
				scmp.setHeader(SCMPHeaderType.SCMP_OFFSET.getName(), scmpOffsetInt);
				scmpSequenceNrInt++;
				scmp.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), scmpSequenceNrInt);
				bw.flush();
				return;
			}
			if (byte[].class == body.getClass()) {
				sb.append(SCMPHeaderType.SCMP_BODY_TYPE.getName());
				sb.append(EQUAL_SIGN);
				sb.append(TYPE.ARRAY.getType());
				sb.append("\n");
				byte[] ba = (byte[]) body;
				sb.append(SCMPHeaderType.BODY_LENGTH.getName());
				sb.append(EQUAL_SIGN);
				int bodyLength = scmp.getBodyLength();
				int messagePartLength = bodyLength;
				if (scmp.isPart() == false) {
					if (messagePartLength > SCMP.LARGE_MESSAGE_LIMIT) {
						messagePartLength = (bodyLength - scmpOffsetInt) > SCMP.LARGE_MESSAGE_LIMIT ? SCMP.LARGE_MESSAGE_LIMIT
								: bodyLength - scmpOffsetInt;
						if ("REQ".equals(headLineKey)) {
							headLineKey = "PRQ";
						}
					}
				}
				sb.append(String.valueOf(messagePartLength));
				sb.append("\n");
				sb.append(SCMPHeaderType.SCMP_CALL_LENGTH.getName());
				sb.append(EQUAL_SIGN);
				sb.append(String.valueOf(bodyLength));
				sb.append("\n\n");
				bw.write(headLineKey);
				bw.write(sb.toString());
				bw.flush();
				if (scmp.isPart()) {
					os.write((byte[]) ba, scmpOffsetInt, messagePartLength);
				} else {
					os.write((byte[]) ba, 0, messagePartLength);
				}
				scmpOffset += messagePartLength;
				scmp.setHeader(SCMPHeaderType.SCMP_OFFSET.getName(), scmpOffset);
				scmpSequenceNrInt++;
				scmp.setHeader(SCMPHeaderType.SEQUENCE_NR.getName(), scmpSequenceNrInt);
				os.flush();
				return;
			} else {
				throw new IOException("unsupported large message body type");
			}
		} else { // TODO verify with DANI, added because null bodies are allowed!
			bw.write(sb.toString());
			bw.flush();
		}
		return;
	}

	private static enum TYPE {
		UNDEFINED("undefined"), MESSAGE("msg"), ARRAY("array"), STRING("string");
		private String type = "undefined";

		private TYPE(String type) {
			this.type = type;
		}

		public String getType() {
			return type;
		}

		public static TYPE getEnumType(String type) {
			if (UNDEFINED.getType().equals(type)) {
				return UNDEFINED;
			}
			if (STRING.getType().equals(type)) {
				return STRING;
			}
			if (MESSAGE.getType().equals(type)) {
				return MESSAGE;
			}
			if (ARRAY.getType().equals(type)) {
				return ARRAY;
			}
			return UNDEFINED;
		}
	}
}
