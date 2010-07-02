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
package com.stabilit.scm.common.net;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.SCMPPoint;
import com.stabilit.scm.common.scmp.IInternalMessage;
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
public abstract class MessageEncoderDecoderAdapter implements IEncoderDecoder {

	private DecimalFormat df = new DecimalFormat(IConstants.FORMAT_OF_MSG_SIZE);
	protected IFrameDecoder defaultFrameDecoder = FrameDecoderFactory.getDefaultFrameDecoder();

	/** {@inheritDoc} */
	@Override
	public Object decode(InputStream is) throws Exception {
		InputStreamReader isr = new InputStreamReader(is, CHARSET);
		BufferedReader br = new BufferedReader(isr);

		// read headline
		byte[] headline = new byte[IConstants.FIX_HEADLINE_SIZE];
		is.read(headline);

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
		int readBytes = IConstants.FIX_HEADLINE_SIZE;
		String line;
		while (readBytes < IConstants.FIX_HEADLINE_SIZE + scmpHeaderSize) {
			line = br.readLine();
			if (line == null || line.length() <= 0) {
				break;
			}
			readBytes += line.getBytes().length;
			readBytes += 1; // read LF

			Matcher match = IEncoderDecoder.DECODE_REG.matcher(line);
			if (match.matches() && match.groupCount() == 2) {
				String key = match.group(1).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
				String value = match.group(2).replace(ESCAPED_EQUAL_SIGN, EQUAL_SIGN);
				metaMap.put(key, value);
			}
		}
		// reading body - depends on body type
		String scmpBodyTypeString = metaMap.get(SCMPHeaderAttributeKey.BODY_TYPE.getName());
		scmpMsg.setHeader(metaMap);
		if (scmpBodySize <= 0) {
			SCMPPoint.getInstance().fireDecode(this, scmpMsg);
			return scmpMsg;
		}
		SCMPBodyType scmpBodyType = SCMPBodyType.getBodyType(scmpBodyTypeString);
		try {
			switch (scmpBodyType) {
			case binary:
			case undefined:
				return this.decodeBinaryData(is, scmpMsg, readBytes, scmpBodySize);
			case text:
				return this.decodeTextData(br, scmpMsg, scmpBodySize);
			case internalMessage:
				return this.decodeInternalMessage(br, scmpMsg);
			}
		} catch (Exception e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
		SCMPPoint.getInstance().fireDecode(this, scmpMsg);
		return scmpMsg;
	}

	protected Object decodeInternalMessage(BufferedReader br, SCMPMessage scmpMsg) throws IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
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
		Class<?> messageClass = Class.forName(t[1]);
		IInternalMessage message = (IInternalMessage) messageClass.newInstance();
		message.decode(br);
		scmpMsg.setBody(message);
		return scmpMsg;
	}

	protected Object decodeTextData(BufferedReader br, SCMPMessage scmpMsg, int scmpBodySize) throws IOException {
		char[] caBuffer = new char[scmpBodySize];
		br.read(caBuffer);
		String bodyString = new String(caBuffer, 0, scmpBodySize);
		scmpMsg.setBody(bodyString);
		return scmpMsg;
	}

	protected Object decodeBinaryData(InputStream is, SCMPMessage scmpMsg, int readBytes, int scmpBodySize)
			throws IOException {
		byte[] baBuffer = new byte[scmpBodySize];
		is.reset();
		is.skip(readBytes);
		is.read(baBuffer);
		scmpMsg.setBody(baBuffer);
		return scmpMsg;
	}

	protected void writeHeadLine(BufferedWriter bw, SCMPHeadlineKey headerKey, int messageSize, int headerSize)
			throws IOException {
		bw.write(headerKey.toString());
		bw.write(df.format(messageSize));
		bw.write(df.format(headerSize));
		bw.write(" ");
		bw.append(SCMPMessage.SCMP_VERSION.toString());
		bw.append("\n");
		bw.flush();
	}
}
