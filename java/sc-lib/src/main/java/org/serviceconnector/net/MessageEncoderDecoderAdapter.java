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
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPKeepAlive;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.scmp.SCMPMessageFault;
import org.serviceconnector.scmp.SCMPPart;

/**
 * The Class MessageEncoderDecoderAdapter.
 * 
 * @author JTraber
 */
public abstract class MessageEncoderDecoderAdapter implements IEncoderDecoder {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(MessageEncoderDecoderAdapter.class);

	/** The df msg size. */
	private DecimalFormat dfMsgSize = new DecimalFormat(Constants.SCMP_FORMAT_OF_MSG_SIZE);

	/** The df header size. */
	private DecimalFormat dfHeaderSize = new DecimalFormat(Constants.SCMP_FORMAT_OF_HEADER_SIZE);

	/** The default frame decoder. */
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
		// evaluating header key and creating corresponding SCMP type
		SCMPHeaderKey headerKey = SCMPHeaderKey.getKeyByHeadline(headline);
		switch (headerKey) {
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
						metaMap.put(new String(buffer, keyOff, (index - keyOff), Constants.DEFAULT_ENCODING), new String(buffer, index + 1,
								(inLoopIndex - 1) - index, Constants.DEFAULT_ENCODING));
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
				metaMap.put(new String(buffer, keyOff, (index - keyOff), Constants.DEFAULT_ENCODING), null);
				// updating offset for next key, +1 for <LF>
				keyOff = index + 1;
			}
		}

		scmpMsg.setHeader(metaMap);
		// message logging
		MessageLogger.logInputMessage(headerKey, scmpMsg);
		if (scmpBodySize <= 0) {
			// no body found stop decoding
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
					// message decompression required
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
			LOGGER.error("decode", ex);
			throw new EncodingDecodingException("io error when decoding message", ex);
		}
		return scmpMsg;
	}

	/**
	 * Write head line.
	 * 
	 * @param bw
	 *            the bw
	 * @param headerKey
	 *            the header key
	 * @param messageSize
	 *            the message size
	 * @param headerSize
	 *            the header size
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	protected void writeHeadLine(BufferedWriter bw, SCMPHeaderKey headerKey, int messageSize, int headerSize) throws IOException {
		bw.write(headerKey.toString());
		bw.write(dfMsgSize.format(messageSize));
		bw.write(dfHeaderSize.format(headerSize));
		bw.write(" ");
		bw.write(SCMPMessage.SCMP_VERSION.toString());
		bw.write(Constants.LINE_BREAK_SIGN);
		bw.flush();
	}

	/**
	 * Write header.
	 * 
	 * @param metaMap
	 *            the meta map
	 * @return the string builder
	 */
	protected StringBuilder writeHeader(Map<String, String> metaMap) {
		StringBuilder sb = new StringBuilder();

		// write header fields
		Set<Entry<String, String>> entrySet = metaMap.entrySet();
		for (Entry<String, String> entry : entrySet) {
			String value = entry.getValue();
			sb.append(entry.getKey());
			if (value != null) {
				sb.append(Constants.EQUAL_SIGN);
				sb.append(value);
			}
			sb.append(Constants.LINE_BREAK_SIGN);
		}
		return sb;
	}

	/**
	 * Compress body.
	 * 
	 * @param bodyBuffer
	 *            the body buffer
	 * @param bodyOffset
	 *            the body offset
	 * @param bodyLength
	 *            the body length
	 * @return the byte[]
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
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
