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
import com.stabilit.sc.common.io.SCMPComposite;
import com.stabilit.sc.common.io.SCMPFault;
import com.stabilit.sc.common.io.SCMPHeaderAttributeKey;
import com.stabilit.sc.common.io.SCMPHeadlineKey;
import com.stabilit.sc.common.io.SCMPInternalStatus;
import com.stabilit.sc.common.io.SCMPPart;

public class LargeMessageEncoderDecoder implements IEncoderDecoder {

	public static final String HEADER_REGEX = "(RES|REQ|PRQ|PRS|EXC) .*";

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

		String scmpBodyType = metaMap.get(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
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
		
		int scmpOffsetInt = 0;
		int scmpSequenceNrInt = 0;
		if (scmp.isComposite() == false) {
			// try to get message offset
			String scmpOffset = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_OFFSET);
			String scmpSequenceNr = scmp.getHeader(SCMPHeaderAttributeKey.SEQUENCE_NR);
			if (scmpOffset == null) {
				scmpOffsetInt = 0;
				scmpSequenceNr = "0";
				scmp.setHeader(SCMPHeaderAttributeKey.SEQUENCE_NR, "0");
				scmp.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, scmpOffsetInt);
			} else {
			   scmpOffsetInt = Integer.parseInt(scmpOffset);
			}
			// TODO wrong here!!
			// scmpSequenceNrInt = Integer.parseInt(scmpSequenceNr);
		} else {
			scmpOffsetInt = ((SCMPComposite) scmp).getOffset();
			scmp.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, scmpOffsetInt);
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
					// sb.append(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
					// sb.append(EQUAL_SIGN);
					// sb.append(TYPE.STRING.getType());
					// sb.append("\n");
					// message chunking
					int bodyLength = scmp.getBodyLength();
					int messagePartLength = bodyLength;
					if (scmp.isPart() == false) {
						if (messagePartLength > SCMP.LARGE_MESSAGE_LIMIT) {
							messagePartLength = (bodyLength - scmpOffsetInt) > SCMP.LARGE_MESSAGE_LIMIT ? SCMP.LARGE_MESSAGE_LIMIT
									: bodyLength - scmpOffsetInt;
							if (headerKey == SCMPHeadlineKey.REQ) {
								if (bodyLength > messagePartLength + scmpOffsetInt) {									
									headerKey = SCMPHeadlineKey.PRQ;
								}
							}
							if (headerKey == SCMPHeadlineKey.RES) {
								if (bodyLength > messagePartLength + scmpOffsetInt) {									
									headerKey = SCMPHeadlineKey.PRS;
								}
							}
						}
					}
					// this is a scmp part, we need to redefine the body length (message part length)
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(messagePartLength));
					sb.append("\n");
					String scmpCallLength = scmp.getHeader(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH);
					sb.append(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					// if scmpCallLength is set - we are on SC, take over attribute
					if (scmpCallLength == null) {
						sb.append(String.valueOf(bodyLength));
					} else {
						sb.append(scmpCallLength);
					}
					sb.append("\n\n");
					int messageLength = sb.length() + messagePartLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					if (scmp.isPart() == false) {
						bw.write(t, scmpOffsetInt, messagePartLength);
					} else {
						if (scmp.isBodyOffset()) {
						    bw.write(t, scmpOffsetInt, messagePartLength);
						} else {
						    bw.write(t, 0, messagePartLength);						
						}
					}
					scmpOffsetInt += messagePartLength;
					scmp.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, scmpOffsetInt);
					scmpSequenceNrInt++;
					scmp.setHeader(SCMPHeaderAttributeKey.SEQUENCE_NR, scmpSequenceNrInt);
					bw.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				}

				if (byte[].class == body.getClass()) {
					// sb.append(SCMPHeaderAttributeKey.SCMP_BODY_TYPE.getName());
					// sb.append(EQUAL_SIGN);
					// sb.append(TYPE.ARRAY.getType());
					// sb.append("\n");
					byte[] ba = (byte[]) body;
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					// message chunking
					int bodyLength = scmp.getBodyLength();
					int messagePartLength = bodyLength;
					if (scmp.isPart() == false) {
						if (messagePartLength > SCMP.LARGE_MESSAGE_LIMIT) {
							messagePartLength = (bodyLength - scmpOffsetInt) > SCMP.LARGE_MESSAGE_LIMIT ? SCMP.LARGE_MESSAGE_LIMIT
									: bodyLength - scmpOffsetInt;
							if (headerKey == SCMPHeadlineKey.REQ) {
								headerKey = SCMPHeadlineKey.PRQ;
							}
							if (headerKey == SCMPHeadlineKey.RES) {
								headerKey = SCMPHeadlineKey.PRS;
							}
						}
					}
					sb.append(String.valueOf(messagePartLength));
					sb.append("\n");
					sb.append(SCMPHeaderAttributeKey.SCMP_CALL_LENGTH.getName());
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
					scmp.setHeader(SCMPHeaderAttributeKey.SCMP_OFFSET, scmpOffsetInt);
					scmpSequenceNrInt++;
					scmp.setHeader(SCMPHeaderAttributeKey.SEQUENCE_NR, scmpSequenceNrInt);
					os.flush();
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				} else {
					scmp.setInternalStatus(SCMPInternalStatus.FAILED);
					throw new EncodingDecodingException("unsupported large message body type");
				}
			} else {
				// sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
				// sb.append(EQUAL_SIGN);
				// sb.append("0\n");
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
