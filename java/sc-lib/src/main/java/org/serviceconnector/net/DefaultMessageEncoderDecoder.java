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

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.log.MessageLogger;
import org.serviceconnector.scmp.SCMPHeaderAttributeKey;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPInternalStatus;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * The Class DefaultEncoderDecoder. Defines default SCMP encoding/decoding of object into/from stream.
 * 
 * @author JTraber
 */
public class DefaultMessageEncoderDecoder extends MessageEncoderDecoderAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(DefaultMessageEncoderDecoder.class);

	/**
	 * Instantiates a new default encoder decoder.
	 */
	DefaultMessageEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPMessage scmpMsg = (SCMPMessage) obj;

		if (scmpMsg.isGroup() == false) {
			// no group call reset internal status, if group call internal
			// status already set
			scmpMsg.setInternalStatus(SCMPInternalStatus.NONE);
		}

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
					if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION) && AppContext.isScEnvironment() == false) {
						// message compression required
						ba = this.compressBody(ba, 0, bodyLength);
					}
					this.writeHeadLine(bw, headerKey, ba.length + sb.length(), headerSize);
					bw.write(sb.toString());
					bw.flush();
					os.write(ba);
					os.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));

					return;
				}
				if (String.class == body.getClass()) {
					String t = (String) body;
					int bodyLength = t.length();
					if (scmpMsg.getHeaderFlag(SCMPHeaderAttributeKey.COMPRESSION) && AppContext.isScEnvironment() == false) {
						// message compression required
						byte[] ba = t.getBytes();
						ba = this.compressBody(ba, 0, bodyLength);
						this.writeHeadLine(bw, headerKey, ba.length + sb.length(), headerSize);
						bw.write(sb.toString()); // write header
						bw.flush();
						os.write(ba);
						os.flush();
					} else {
						this.writeHeadLine(bw, headerKey, bodyLength + sb.length(), headerSize);
						bw.write(sb.toString()); // write header
						bw.write(t); // write body
						bw.flush();
					}
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					return;
				}
				scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
				throw new EncodingDecodingException("unsupported body type");
			} else {
				this.writeHeadLine(bw, headerKey, headerSize, headerSize);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException ex) {
			logger.error("encode", ex);
			scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", ex);
		}
		scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		return;
	}
}