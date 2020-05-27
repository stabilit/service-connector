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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.MessageLogger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class DefaultEncoderDecoder. Defines default SCMP encoding/decoding of object into/from stream.
 *
 * @author JTraber
 */
public class DefaultMessageEncoderDecoder extends MessageEncoderDecoderAdapter {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultMessageEncoderDecoder.class);

	/**
	 * Instantiates a new default encoder decoder.
	 */
	DefaultMessageEncoderDecoder() {
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
			headerKey = SCMPHeaderKey.RES;
			if (scmpMsg.isFault()) {
				headerKey = SCMPHeaderKey.EXC;
			}
		} else {
			headerKey = SCMPHeaderKey.REQ;
		}

		StringBuilder sb = this.writeHeader(scmpMsg.getHeader());

		int headerSize = sb.length();
		// write body depends on body type
		Object body = scmpMsg.getBody();
		try {
			// message logging
			MessageLogger.logOutputMessage(headerKey, scmpMsg);
			if (body != null) {
				if (byte[].class == body.getClass()) {
					byte[] ba = (byte[]) body;
					int bodyLength = scmpMsg.getBodyLength();
					if (bodyLength == 0) {
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, headerSize, headerSize);
						bw.write(sb.toString());
						bw.flush();
						return;
					}
					if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION) && AppContext.isScEnvironment() == false) {
						// message compression required
						ba = this.compressBody(ba, 0, bodyLength);
					}
					this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, ba.length + sb.length(), headerSize);
					bw.write(sb.toString());
					bw.flush();
					os.write(ba);
					os.flush();
					return;
				}
				if (String.class == body.getClass()) {
					String t = (String) body;
					int bodyLength = t.length();
					if (bodyLength == 0) {
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, headerSize, headerSize);
						bw.write(sb.toString());
						bw.flush();
						return;
					}
					if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION) && AppContext.isScEnvironment() == false) {
						// message compression required
						byte[] ba = t.getBytes();
						ba = this.compressBody(ba, 0, bodyLength);
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, ba.length + sb.length(), headerSize);
						bw.write(sb.toString()); // write header
						bw.flush();
						os.write(ba);
						os.flush();
					} else {
						this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, bodyLength + sb.length(), headerSize);
						bw.write(sb.toString()); // write header
						bw.write(t); // write body
						bw.flush();
					}
					return;

				}
				throw new EncodingDecodingException("unsupported body type");
			} else {
				this.writeHeadLine(scmpMsg.getSCMPVersion(), bw, headerKey, headerSize, headerSize);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException ex) {
			LOGGER.error("encode", ex);
			throw new EncodingDecodingException("io error when encoding message", ex);
		}
		return;
	}
}
