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
import com.stabilit.sc.common.io.SCMPComposite;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderKey;
import com.stabilit.sc.common.io.SCMPHeaderAttributeType;
import com.stabilit.sc.common.io.SCMPPart;

public class LargeMessageEncoderDecoder implements IEncoderDecoder {

	public static final String HEADER_REGEX = "(RES|REQ|PRQ|PRS|EXC) .*";

	public LargeMessageEncoderDecoder() {
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

			Pattern decodHeadReg = Pattern.compile(HEADER_REGEX);
			Matcher matchHeadline = decodHeadReg.matcher(line);

			if (matchHeadline.matches() == false) {
				throw new EncodingDecodingException("wrong protocol in message not possible to decode");
			}

			if (line.startsWith("EXC ")) {
				scmp = new SCMPFault();
			} else {
				// message chunking
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
//		String messageType = scmp.getMessageType(); // messageType is never null
//		if (messageType == null) {
//			throw new EncodingDecodingException("No messageType found (null)");
//		}
		// message chunking

		SCMPHeaderKey headerKey = SCMPHeaderKey.UNDEF;
		if (scmp.isReply()) {
			if (scmp.isFault()) {
				headerKey = SCMPHeaderKey.EXC;
			} else {
				headerKey = SCMPHeaderKey.PRS;
			}
		} else {
			if (scmp.isPart() || scmp.isComposite()) {
				headerKey = SCMPHeaderKey.PRQ;
			} else {
				headerKey = SCMPHeaderKey.REQ;
			}
		}
		// SCMPHeaderKey headerKey = SCMPHeaderKey.UNDEF;
		// if (messageType.startsWith("REQ_")) {
		// if (scmp.isPart() || scmp.isComposite()) {
		// headerKey = SCMPHeaderKey.PRQ;
		// } else {
		// headerKey = SCMPHeaderKey.REQ;
		//
		// }
		// } else if (messageType.startsWith("RES_")) {
		// if (scmp.isFault()) {
		// headerKey = SCMPHeaderKey.EXC;
		// } else {
		// headerKey = SCMPHeaderKey.PRS;
		// }
		// }
		int scmpOffsetInt = 0;
		int scmpSequenceNrInt = 0;
		if (scmp.isComposite() == false) {
			// try to get message offset
			String scmpOffset = scmp.getHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName());
			String scmpSequenceNr = scmp.getHeader(SCMPHeaderAttributeType.SEQUENCE_NR.getName());
			if (scmpOffset == null) {
				scmpOffset = "0";
				scmpSequenceNr = "0";
				scmp.setHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName(), scmpOffset);
				scmp.setHeader(SCMPHeaderAttributeType.SEQUENCE_NR.getName(), "0");
			}
			scmpOffsetInt = Integer.parseInt(scmpOffset);
			//TODO wrong here!!
			//scmpSequenceNrInt = Integer.parseInt(scmpSequenceNr);
		} else {
			scmpOffsetInt = ((SCMPComposite) scmp).getOffset();
			scmp.setHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName(), String.valueOf(scmpOffsetInt));
		}
		Map<String, String> metaMap = scmp.getHeader();
		// create meta part
		StringBuilder sb = new StringBuilder();

		// TODO ?? needs to be removed will be set 10 lines down
		metaMap.remove(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
		metaMap.remove(SCMPHeaderAttributeType.BODY_LENGTH.getName());
		metaMap.remove(SCMPHeaderAttributeType.SCMP_CALL_LENGTH.getName());

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
		try {
			Object body = scmp.getBody();
			if (body != null) {
				if (String.class == body.getClass()) {
					String t = (String) body;
					sb.append(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
					sb.append(EQUAL_SIGN);
					sb.append(TYPE.STRING.getType());
					sb.append("\n");
					// message chunking
					int bodyLength = scmp.getBodyLength();
					int messagePartLength = bodyLength;
					if (scmp.isPart() == false) {
						if (messagePartLength > SCMP.LARGE_MESSAGE_LIMIT) {
							messagePartLength = (bodyLength - scmpOffsetInt) > SCMP.LARGE_MESSAGE_LIMIT ? SCMP.LARGE_MESSAGE_LIMIT
									: bodyLength - scmpOffsetInt;
							if (headerKey == SCMPHeaderKey.REQ) {
								if (bodyLength > messagePartLength + scmpOffsetInt) {
									headerKey = SCMPHeaderKey.PRQ;
								}
							}
						}
					}
					sb.append(SCMPHeaderAttributeType.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(messagePartLength));
					sb.append("\n");
					sb.append(SCMPHeaderAttributeType.SCMP_CALL_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(bodyLength));
					sb.append("\n\n");
					int messageLength = sb.length() + messagePartLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					if (scmp.isPart() == false) {
						bw.write(t, scmpOffsetInt, messagePartLength);
					} else {
						bw.write(t, 0, messagePartLength);
					}
					scmpOffsetInt += messagePartLength;
					scmp.setHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName(), scmpOffsetInt);
					scmpSequenceNrInt++;
					scmp.setHeader(SCMPHeaderAttributeType.SEQUENCE_NR.getName(), scmpSequenceNrInt);
					bw.flush();
					return;
				}

				if (byte[].class == body.getClass()) {
					sb.append(SCMPHeaderAttributeType.SCMP_BODY_TYPE.getName());
					sb.append(EQUAL_SIGN);
					sb.append(TYPE.ARRAY.getType());
					sb.append("\n");
					byte[] ba = (byte[]) body;
					sb.append(SCMPHeaderAttributeType.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					// message chunking
					int bodyLength = scmp.getBodyLength();
					int messagePartLength = bodyLength;
					if (scmp.isPart() == false) {
						if (messagePartLength > SCMP.LARGE_MESSAGE_LIMIT) {
							messagePartLength = (bodyLength - scmpOffsetInt) > SCMP.LARGE_MESSAGE_LIMIT ? SCMP.LARGE_MESSAGE_LIMIT
									: bodyLength - scmpOffsetInt;
							if (headerKey == SCMPHeaderKey.REQ) {
								headerKey = SCMPHeaderKey.PRQ;
							}
						}
					}
					sb.append(String.valueOf(messagePartLength));
					sb.append("\n");
					sb.append(SCMPHeaderAttributeType.SCMP_CALL_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(bodyLength));
					sb.append("\n\n");
					int messageLength = sb.length() + messagePartLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					if (scmp.isPart() == false) {
						os.write((byte[]) ba, scmpOffsetInt, messagePartLength);
					} else {
						os.write((byte[]) ba, 0, messagePartLength);
					}
					scmpOffsetInt += messagePartLength;
					scmp.setHeader(SCMPHeaderAttributeType.SCMP_OFFSET.getName(), scmpOffsetInt);
					scmpSequenceNrInt++;
					scmp.setHeader(SCMPHeaderAttributeType.SEQUENCE_NR.getName(), scmpSequenceNrInt);
					os.flush();
					return;
				} else {
					throw new EncodingDecodingException("unsupported large message body type");
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
