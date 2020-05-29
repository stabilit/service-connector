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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.MessageLogger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class LargeMessageEncoderDecoder. Defines large SCMP encoding/decoding of object into/from stream.
 */
public class LargeMessageEncoderDecoder extends MessageEncoderDecoderAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(LargeMessageEncoderDecoder.class);

	/**
	 * Instantiates a new large message encoder decoder.
	 */
	LargeMessageEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os, Constants.SC_CHARACTER_SET);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPMessage scmpMsg = (SCMPMessage) obj;

		// evaluate right headline key from SCMP type
		SCMPHeaderKey headerKey = SCMPHeaderKey.UNDEF;
		if (scmpMsg.isReply()) {
			if (scmpMsg.isFault()) {
				headerKey = SCMPHeaderKey.EXC;
			} else {
				if (scmpMsg.isPart()) {
					if (scmpMsg.isPollRequest()) {
						headerKey = SCMPHeaderKey.PAC;
					} else {
						headerKey = SCMPHeaderKey.PRS;
					}
				} else {
					headerKey = SCMPHeaderKey.RES;
				}
			}
		} else {
			if (scmpMsg.isPart() || scmpMsg.isComposite()) {
				if (scmpMsg.isPollRequest()) {
					headerKey = SCMPHeaderKey.PAC;
					scmpMsg.setIsReqCompleteAfterMarshallingPart(false);
				} else {
					headerKey = SCMPHeaderKey.PRQ;
					scmpMsg.setIsReqCompleteAfterMarshallingPart(false);
				}
			} else {
				headerKey = SCMPHeaderKey.REQ;
				scmpMsg.setIsReqCompleteAfterMarshallingPart(true);
			}
		}
		StringBuilder sb = this.writeHeader(scmpMsg.getHeader());
		// write body depends on body type
		Object body = scmpMsg.getBody();
		int headerSize = sb.length();
		try {
			if (body != null) {
				if (byte[].class == body.getClass()) {
					byte[] ba = (byte[]) body;
					int bodyLength = scmpMsg.getBodyLength();
					int bodyOffset = scmpMsg.getBodyOffset();
					if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION) && AppContext.isScEnvironment() == false) {
						// message compression required
						ba = this.compressBody(ba, bodyOffset, bodyLength);
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, ba.length + sb.length(), headerSize);
						bw.write(sb.toString());
						bw.flush();
						os.write(ba);
					} else {
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, bodyLength + sb.length(), headerSize);
						bw.write(sb.toString());
						bw.flush();
						os.write(ba, bodyOffset, bodyLength);
					}
					os.flush();
					return;
				}
				if (String.class == body.getClass()) {
					String t = (String) body;
					int bodyLength = scmpMsg.getBodyLength();
					int bodyOffset = scmpMsg.getBodyOffset();
					if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION) && AppContext.isScEnvironment() == false) {
						// message compression required
						byte[] ba = t.getBytes();
						ba = this.compressBody(ba, bodyOffset, bodyLength);
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, ba.length + sb.length(), headerSize);
						bw.write(sb.toString()); // write header
						bw.flush();
						os.write(ba);
						os.flush();
					} else {
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, bodyLength + sb.length(), headerSize);
						bw.write(sb.toString()); // write header
						bw.flush();
						bw.write(t, bodyOffset, bodyLength);
						bw.flush();
					}
					return;
				}
				if (body instanceof InputStream) {
					@SuppressWarnings("resource")
					InputStream inStream = (InputStream) body; // don't close that stream here, needed to stay for large messages
					int msgPartSize = scmpMsg.getPartSize();
					byte[] buffer = new byte[msgPartSize];
					// try reading as much as we can until stream is closed or part size reached
					int bytesRead = 0;
					bytesRead = inStream.read(buffer, bytesRead, buffer.length - bytesRead);
					if (bytesRead <= 0) {
						bytesRead = 0;
						scmpMsg.setPartSize(bytesRead);
						// this is the last message
						headerKey = SCMPHeaderKey.REQ;
						scmpMsg.setIsReqCompleteAfterMarshallingPart(true);
					}
					this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, bytesRead + sb.length(), headerSize);
					bw.write(sb.toString());
					bw.flush();
					os.write(buffer, 0, bytesRead);
					os.flush();
					return;
				}
				throw new EncodingDecodingException("unsupported large message body type");
			} else {
				writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, headerSize, headerSize);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException ex) {
			LOGGER.error("encode", ex);
			throw new EncodingDecodingException("io error when decoding message", ex);
		} finally {
			// message logging
			MessageLogger.logOutputMessage(headerKey, scmpMsg);
		}
	}
}
