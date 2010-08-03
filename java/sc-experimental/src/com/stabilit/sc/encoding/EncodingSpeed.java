/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.sc.encoding;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.SCMPPoint;
import com.stabilit.scm.common.net.EncodingDecodingException;
import com.stabilit.scm.common.net.FrameDecoderFactory;
import com.stabilit.scm.common.net.IEncoderDecoder;
import com.stabilit.scm.common.net.IFrameDecoder;
import com.stabilit.scm.common.scmp.SCMPBodyType;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPHeaderAttributeKey;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPPart;

/**
 * @author JTraber
 */
public class EncodingSpeed {

	/** The Constant UNESCAPED_EQUAL_SIGN_REGEX. */
	public static final String UNESCAPED_EQUAL_SIGN_REGEX = "(.*)(?<!\\\\)=(.*)";
	/** The Constant FLAG_REGEX. */
	public static final String FLAG_REGEX = ".*[^=]";
	/** The Constant ESCAPED_EQUAL_SIGN. */
	public static final String ESCAPED_EQUAL_SIGN = "\\=";
	/** The Constant EQUAL_SIGN. */
	public static final String EQUAL_SIGN = "=";
	/** The Constant CHARSET. */
	public static final String CHARSET = "ISO-8859-1";
	/** The Constant DECODE_REG. */
	public static final Pattern EQUAL_SIGN_DECODE_REG = Pattern.compile(UNESCAPED_EQUAL_SIGN_REGEX);
	/** The Constant FLAG_DECODE_REG. */
	public static final Pattern FLAG_DECODE_REG = Pattern.compile(FLAG_REGEX);
	protected IFrameDecoder defaultFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();

	public static void main(String[] args) throws Exception {
		EncodingSpeed enc = new EncodingSpeed();
		enc.runRegex();
	}

	private void runRegex() throws Exception {

		byte[] encodeBytes = new byte[] { 82, 69, 81, 32, 48, 48, 48, 48, 48, 51, 52, 32, 48, 48, 48, 50, 50, 32, 49,
				46, 48, 10, 98, 116, 121, 61, 98, 105, 110, 10, 109, 105, 100, 61, 49, 10, 109, 116, 121, 61, 65, 84,
				84, 10, 104, 101, 108, 108, 111, 32, 119, 111, 114, 108, 100, 33 };
		InputStream in = new ByteArrayInputStream(encodeBytes);

		long startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			this.encodeWithRegex(in);
			in.reset();
		}
		System.out.println("encodeWithRegex time needed: " + (System.currentTimeMillis() - startTime) + " millis");

		startTime = System.currentTimeMillis();
		for (int i = 0; i < 10000; i++) {
			this.encodeWithByteBase(in);
			in.reset();
		}
		System.out.println("encodeWithByteBase time needed: " + (System.currentTimeMillis() - startTime) + " millis");
	}

	private SCMPMessage encodeWithByteBase(InputStream is) throws Exception {
		// read headline
		byte[] headline = new byte[Constants.FIX_HEADLINE_SIZE];
		is.read(headline);

		String scmpVer = new String(headline, Constants.FIX_SCMP_VERSION_START, Constants.FIX_SCMP_VERSION_LENGTH);
		SCMPMessage.SCMP_VERSION.isSupported(scmpVer);

		SCMPMessage scmpMsg = null;
		// evaluating headline key and creating corresponding SCMP type
		SCMPHeadlineKey headlineKey = SCMPHeadlineKey.getKeyByHeadline(headline);
		switch (headlineKey) {
		case KRS:
		case KRQ:
			scmpMsg = new SCMPKeepAlive();
			return scmpMsg;
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

		// parse headerSize & bodySize
		int scmpHeaderSize = defaultFrameDecoder.parseHeaderSize(headline);
		int scmpBodySize = defaultFrameDecoder.parseMessageSize(headline) - scmpHeaderSize;

		// storing header fields in meta map
		Map<String, String> metaMap = new HashMap<String, String>();

		int length = 256;
		byte[] buffer = new byte[length];
		int keyOff = 0;

		int readBytes = is.read(buffer);
		do {
			for (int i = 0; i < readBytes; i++) {
				// looping until "="
				if (buffer[i] == 0x3D) {
					// byte = "="
					for (int j = i; j < readBytes; j++) {
						// looping until <LF>
						if (buffer[j] == 0x0A) {
							// byte == "LF"
							metaMap.put(new String(buffer, keyOff, (i - keyOff)),
									new String(buffer, i + 1, (j - 1) - i));
							i = j;
							keyOff = j + 1;
							break;
						}
					}
				}

			}
			buffer = new byte[length];
			readBytes = is.read(buffer);
		} while (readBytes != -1);

		// reading body - depends on body type
		String scmpBodyTypeString = metaMap.get(SCMPHeaderAttributeKey.BODY_TYPE.getValue());
		scmpMsg.setHeader(metaMap);
		if (scmpBodySize <= 0) {
			// no body found stop decoding
			SCMPPoint.getInstance().fireDecode(this, scmpMsg);
			return scmpMsg;
		}
		SCMPBodyType scmpBodyType = SCMPBodyType.getBodyType(scmpBodyTypeString);
		try {
			switch (scmpBodyType) {
			case BINARY:
			case UNDEFINED:
				return this.decodeBinaryData(is, scmpMsg, readBytes, scmpBodySize);
			case TEXT:
				return this.decodeTextData(is, scmpMsg, scmpBodySize);
			}
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		SCMPPoint.getInstance().fireDecode(this, scmpMsg);
		return scmpMsg;
	}

	private SCMPMessage encodeWithRegex(InputStream is) throws Exception {
		InputStreamReader isr = new InputStreamReader(is, CHARSET);
		BufferedReader br = new BufferedReader(isr);

		// read headline
		byte[] headline = new byte[Constants.FIX_HEADLINE_SIZE];
		is.read(headline);

		String scmpVer = new String(headline, Constants.FIX_SCMP_VERSION_START, Constants.FIX_SCMP_VERSION_LENGTH);
		SCMPMessage.SCMP_VERSION.isSupported(scmpVer);

		SCMPMessage scmpMsg = null;
		// evaluating headline key and creating corresponding SCMP type
		SCMPHeadlineKey headlineKey = SCMPHeadlineKey.getKeyByHeadline(headline);
		switch (headlineKey) {
		case KRS:
		case KRQ:
			scmpMsg = new SCMPKeepAlive();
			return scmpMsg;
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

		// parse headerSize & bodySize
		int scmpHeaderSize = defaultFrameDecoder.parseHeaderSize(headline);
		int scmpBodySize = defaultFrameDecoder.parseMessageSize(headline) - scmpHeaderSize;

		// storing header fields in meta map
		Map<String, String> metaMap = new HashMap<String, String>();
		int readBytes = Constants.FIX_HEADLINE_SIZE;
		String line;
		while (readBytes < Constants.FIX_HEADLINE_SIZE + scmpHeaderSize) {
			line = br.readLine();
			if (line == null || line.length() <= 0) {
				break;
			}
			readBytes += line.getBytes().length;
			readBytes += 1; // read LF

			Matcher match = IEncoderDecoder.EQUAL_SIGN_DECODE_REG.matcher(line);
			if (match.matches()) {
				String key = match.group(1).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
				String value = null;
				if (match.groupCount() == 2) {
					// key has a value mapping - extract value
					value = match.group(2).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
				}
				metaMap.put(key, value);
				continue;
			}
			match = IEncoderDecoder.FLAG_DECODE_REG.matcher(line);
			if (match.matches()) {
				String key = match.group(0);
				metaMap.put(key, null);
			}
		}
		// reading body - depends on body type
		String scmpBodyTypeString = metaMap.get(SCMPHeaderAttributeKey.BODY_TYPE.getValue());
		scmpMsg.setHeader(metaMap);
		if (scmpBodySize <= 0) {
			// no body found stop decoding
			SCMPPoint.getInstance().fireDecode(this, scmpMsg);
			return scmpMsg;
		}
		SCMPBodyType scmpBodyType = SCMPBodyType.getBodyType(scmpBodyTypeString);
		try {
			switch (scmpBodyType) {
			case BINARY:
			case UNDEFINED:
				return this.decodeBinaryData(is, scmpMsg, readBytes, scmpBodySize);
			case TEXT:
				return this.decodeTextData(br, scmpMsg, scmpBodySize);
			}
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		SCMPPoint.getInstance().fireDecode(this, scmpMsg);
		return scmpMsg;
	}

	protected SCMPMessage decodeTextData(InputStream is, SCMPMessage scmpMsg, int scmpBodySize) throws IOException {
		byte[] caBuffer = new byte[scmpBodySize];
		is.read(caBuffer);
		String bodyString = new String(caBuffer, 0, scmpBodySize);
		scmpMsg.setBody(bodyString);
		return scmpMsg;
	}

	protected SCMPMessage decodeTextData(BufferedReader br, SCMPMessage scmpMsg, int scmpBodySize) throws IOException {
		char[] caBuffer = new char[scmpBodySize];
		br.read(caBuffer);
		String bodyString = new String(caBuffer, 0, scmpBodySize);
		scmpMsg.setBody(bodyString);
		return scmpMsg;
	}

	protected SCMPMessage decodeBinaryData(InputStream is, SCMPMessage scmpMsg, int readBytes, int scmpBodySize)
			throws IOException {
		byte[] baBuffer = new byte[scmpBodySize];
		is.reset();
		is.skip(readBytes);
		is.read(baBuffer);
		scmpMsg.setBody(baBuffer);
		return scmpMsg;
	}

}
