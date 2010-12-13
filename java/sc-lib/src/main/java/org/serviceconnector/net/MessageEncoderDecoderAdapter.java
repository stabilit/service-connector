/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.net;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.MessageLogger;
import org.serviceconnector.scmp.SCMPBodyType;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeadlineKey;
import org.serviceconnector.scmp.SCMPKeepAlive;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPPart;

/**
 * @author JTraber
 */
public abstract class MessageEncoderDecoderAdapter implements IEncoderDecoder {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(MessageEncoderDecoderAdapter.class);

	private DecimalFormat dfMsgSize = new DecimalFormat(Constants.SCMP_FORMAT_OF_MSG_SIZE);
	private DecimalFormat dfHeaderSize = new DecimalFormat(Constants.SCMP_FORMAT_OF_HEADER_SIZE);
	protected IFrameDecoder defaultFrameDecoder = AppContext.getFrameDecoderFactory().getFrameDecoder(Constants.TCP);

	/** {@inheritDoc} */
	@Override
	public Object decode(InputStream is) throws Exception {
		// read headline
		byte[] headline = new byte[Constants.SCMP_HEADLINE_SIZE_WITHOUT_VERSION];
		is.read(headline);

		byte[] version = new byte[Constants.SCMP_VERSION_LENGTH_IN_HEADLINE];
		is.read(version);
		SCMPMessage.SCMP_VERSION.isSupported(version);
		is.skip(1); // read LF

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
			// no poll request
			scmpMsg = new SCMPPart(false);
			break;
		case PAC:
			// poll request
			scmpMsg = new SCMPPart(true);
			break;
		case EXC:
			scmpMsg = new SCMPMessageFault();
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
		byte[] buffer = new byte[scmpHeaderSize];
		int keyOff = 0;
		// decoding header
		int readBytes = is.read(buffer);
		for (int index = 0; index < readBytes; index++) {
			// looping until <=> found, looking for key value pair
			if (buffer[index] == 0x3D) {
				// <=> found
				for (int inLoopIndex = index; inLoopIndex < readBytes; inLoopIndex++) {
					// looping until <LF> got found
					if (buffer[inLoopIndex] == 0x0A) {
						// <LF> found
						metaMap.put(new String(buffer, keyOff, (index - keyOff), CHARSET), new String(buffer, index + 1,
								(inLoopIndex - 1) - index, CHARSET));
						// updating outer loop index
						index = inLoopIndex;
						// updating offset for next key, +1 for <LF>
						keyOff = inLoopIndex + 1;
						// key value pair found, stop inner loop
						break;
					}
				}
				// key value pair found, continue looking for next pair
				continue;
			}
			// looping until <LF> found, looking for header flag
			if (buffer[index] == 0x0A) {
				// <LF> found
				metaMap.put(new String(buffer, keyOff, (index - keyOff), CHARSET), null);
				// updating offset for next key, +1 for <LF>
				keyOff = index + 1;
			}
		}

		scmpMsg.setHeader(metaMap);
		if (scmpBodySize <= 0) {
			// no body found stop decoding
			if (MessageLogger.isEnabled()) {
				MessageLogger.logMessage(this.getClass().getSimpleName(), scmpMsg);
			}
			return scmpMsg;
		}
		// decoding body - depends on body type
		String scmpBodyTypeString = metaMap.get(SCMPHeaderAttributeKey.BODY_TYPE.getValue());
		SCMPBodyType scmpBodyType = SCMPBodyType.getBodyType(scmpBodyTypeString);
		try {
			byte[] body = new byte[scmpBodySize];
			int bodySize = is.read(body);
			if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION)) {
				if (AppContext.isScEnvironment() == false) {
					// message compression required
					Inflater decompresser = new Inflater();
					decompresser.setInput(body, 0, bodySize);
					ByteArrayOutputStream bos = new ByteArrayOutputStream(Constants.MAX_MESSAGE_SIZE);
					byte[] buf = new byte[Constants.MAX_MESSAGE_SIZE];
					bodySize = 0;
					while (!decompresser.finished()) {
						int count = decompresser.inflate(buf);
						bodySize += count;
						bos.write(buf, 0, count);
					}
					bos.close();
					decompresser.end();
					body = bos.toByteArray();
				} else {
					// is an SC environment - body is compressed - below switch is irrelevant
					scmpMsg.setBody(body, 0, bodySize);
					return scmpMsg;
				}
			}

			switch (scmpBodyType) {
			case BINARY:
			case INPUT_STREAM:
			case UNDEFINED:
				scmpMsg.setBody(body, 0, bodySize);
				return scmpMsg;
			case TEXT:
				scmpMsg.setBody(new String(body, 0, bodySize));
				return scmpMsg;
			}
		} catch (Exception ex) {
			logger.error("decode", ex);
		}
		if (MessageLogger.isEnabled()) {
			MessageLogger.logMessage(this.getClass().getSimpleName(), scmpMsg);
		}
		return scmpMsg;
	}

	protected void writeHeadLine(BufferedWriter bw, SCMPHeadlineKey headerKey, int messageSize, int headerSize) throws IOException {
		bw.write(headerKey.toString());
		bw.write(dfMsgSize.format(messageSize));
		bw.write(dfHeaderSize.format(headerSize));
		bw.write(" ");
		bw.write(SCMPMessage.SCMP_VERSION.toString());
		bw.write("\n");
		bw.flush();
	}

	protected StringBuilder writeHeader(Map<String, String> metaMap) {
		StringBuilder sb = new StringBuilder();

		// write header fields
		Set<Entry<String, String>> entrySet = metaMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String value = entry.getValue();
			sb.append(entry.getKey());
			if (value != null) {
				sb.append(EQUAL_SIGN);
				sb.append(value);
			}
			sb.append("\n");
		}
		return sb;
	}
	
	protected byte[] compressBody(byte[] bodyBuffer, int bodyOffset, int bodyLength) throws IOException {
		byte[] output = null;
		// message compression required
		output = new byte[bodyLength];
		Deflater compresser = new Deflater();
		// compresser.setInput(ba);
		compresser.setInput(bodyBuffer, bodyOffset, bodyLength);
		compresser.finish();
		ByteArrayOutputStream baos = new ByteArrayOutputStream(output.length);
		int numCompressedBytes = 0;
		while (!compresser.finished()) {
			numCompressedBytes = compresser.deflate(output);
			if (numCompressedBytes > 0) {
				baos.write(output, 0, numCompressedBytes);
				baos.flush();
			}
		}
		baos.close();
		bodyBuffer = baos.toByteArray();
		return bodyBuffer;
	}
}
