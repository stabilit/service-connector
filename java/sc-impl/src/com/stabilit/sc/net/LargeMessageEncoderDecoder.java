/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.sc.net;

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

import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.listener.ExceptionListenerSupport;
import com.stabilit.sc.scmp.IInternalMessage;
import com.stabilit.sc.scmp.SCMP;
import com.stabilit.sc.scmp.SCMPBodyType;
import com.stabilit.sc.scmp.SCMPFault;
import com.stabilit.sc.scmp.SCMPHeaderAttributeKey;
import com.stabilit.sc.scmp.SCMPHeadlineKey;
import com.stabilit.sc.scmp.SCMPInternalStatus;
import com.stabilit.sc.scmp.impl.EncodingDecodingException;
import com.stabilit.sc.scmp.internal.SCMPPart;

/**
 * The Class LargeMessageEncoderDecoder. Defines large SCMP encoding/decoding of object into/from stream.
 */
public class LargeMessageEncoderDecoder implements IEncoderDecoder {

	/**
	 * Instantiates a new large message encoder decoder.
	 */
	LargeMessageEncoderDecoder() {
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.IFactoryable#newInstance()
	 */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IEncoderDecoder#decode(java.io.InputStream)
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object decode(InputStream is) throws EncodingDecodingException {
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		Map<String, String> metaMap = new HashMap<String, String>();
		// read heading line
		SCMP scmp = null;
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
			ExceptionListenerSupport.getInstance().fireException(this, e1);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		// reading body - depends on body type
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
				is.reset();
				is.skip(readBytes);
				is.read(baBuffer);
				scmp.setBody(baBuffer);
				return scmp;
			}
		} catch (Exception e) {
			ExceptionListenerSupport.getInstance().fireException(this, e);
		}
		return scmp;
	}

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.net.IEncoderDecoder#encode(java.io.OutputStream, java.lang.Object)
	 */
	@Override
	public void encode(OutputStream os, Object obj) throws EncodingDecodingException {
		OutputStreamWriter osw = new OutputStreamWriter(os);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMP scmp = (SCMP) obj;

		if (scmp.isGroup() == false) {
			// no group call reset internal status, if group call internal status already set
			scmp.setInternalStatus(SCMPInternalStatus.NONE);
		}

		// evaluate right headline key from SCMP type
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
			Object body = scmp.getBody();
			if (body != null) {
				if (String.class == body.getClass()) {
					String t = (String) body;
					int bodyLength = scmp.getBodyLength(); // returns body length of part only
					// this is a scmp part, we need to redefine the body length (message part length)
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					sb.append(String.valueOf(bodyLength));
					sb.append("\n\n");
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					// gets the offset of body - body of part scmp is written
					int bodyOffset = scmp.getBodyOffset();
					bw.write(t, bodyOffset, bodyLength);
					bw.flush();
					// set internal status to save communication state
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				}

				if (byte[].class == body.getClass()) {
					byte[] ba = (byte[]) body;
					sb.append(SCMPHeaderAttributeKey.BODY_LENGTH.getName());
					sb.append(EQUAL_SIGN);
					int bodyLength = scmp.getBodyLength(); // returns body length of part only
					sb.append(String.valueOf(bodyLength));
					sb.append("\n\n");
					int messageLength = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageLength);
					bw.write(sb.toString());
					bw.flush();
					int bodyOffset = scmp.getBodyOffset();
					os.write((byte[]) ba, bodyOffset, bodyLength);
					os.flush();
					// set internal status to save communication state
					scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				} else {
					// set internal status to save communication state
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
			ExceptionListenerSupport.getInstance().fireException(this, e1);
			scmp.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		// set internal status to save communication state
		scmp.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
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
		bw.append(SCMP.SCMP_VERSION);
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
