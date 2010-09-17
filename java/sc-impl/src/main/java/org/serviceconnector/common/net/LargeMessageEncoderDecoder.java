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
package org.serviceconnector.common.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;
import org.serviceconnector.common.factory.IFactoryable;
import org.serviceconnector.common.log.IMessageLogger;
import org.serviceconnector.common.log.impl.MessageLogger;
import org.serviceconnector.common.scmp.SCMPHeadlineKey;
import org.serviceconnector.common.scmp.SCMPMessage;
import org.serviceconnector.common.scmp.internal.SCMPInternalStatus;


/**
 * The Class LargeMessageEncoderDecoder. Defines large SCMP encoding/decoding of
 * object into/from stream.
 */
public class LargeMessageEncoderDecoder extends MessageEncoderDecoderAdapter {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(LargeMessageEncoderDecoder.class);

	/** The Constant messageLogger. */
	private final static IMessageLogger messageLogger = MessageLogger.getInstance();

	/**
	 * Instantiates a new large message encoder decoder.
	 */
	LargeMessageEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
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
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.UNDEF;
		if (scmpMsg.isReply()) {
			if (scmpMsg.isFault()) {
				headerKey = SCMPHeadlineKey.EXC;
			} else {
				if (scmpMsg.isPart()) {
					headerKey = SCMPHeadlineKey.PRS;
				} else {
					headerKey = SCMPHeadlineKey.RES;
				}
			}
		} else {
			if (scmpMsg.isPart() || scmpMsg.isComposite()) {
				headerKey = SCMPHeadlineKey.PRQ;
			} else {
				headerKey = SCMPHeadlineKey.REQ;
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
					int bodySize = scmpMsg.getBodyLength();
					int messageLength = sb.length() + bodySize;
					writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString());
					bw.flush();
					int bodyOffset = scmpMsg.getBodyOffset();
					os.write((byte[]) ba, bodyOffset, bodySize);
					os.flush();
					// set internal status to save communication state
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					if (messageLogger.isDebugEnabled()) {
						messageLogger.logMessage(this.getClass().getSimpleName(), scmpMsg);
					}
					return;
				}
				if (String.class == body.getClass()) {
					String t = (String) body;
					int bodyLength = scmpMsg.getBodyLength();
					int messageSize = sb.length() + bodyLength;
					writeHeadLine(bw, headerKey, messageSize, headerSize);
					bw.write(sb.toString());
					bw.flush();
					// gets the offset of body - body of part message is written
					int bodyOffset = scmpMsg.getBodyOffset();
					bw.write(t, bodyOffset, bodyLength);
					bw.flush();
					// set internal status to save communication state
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					if (messageLogger.isDebugEnabled()) {
						messageLogger.logMessage(this.getClass().getSimpleName(), scmpMsg);
					}
					return;
				}
				// set internal status to save communication state
				scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
				throw new EncodingDecodingException("unsupported large message body type");
			} else {
				writeHeadLine(bw, headerKey, headerSize, headerSize);
				bw.write(sb.toString());
				bw.flush();
			}
		} catch (IOException ex) {
			logger.error("encode", ex);
			scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", ex);
		}
		// set internal status to save communication state
		scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		if (messageLogger.isDebugEnabled()) {
			messageLogger.logMessage(this.getClass().getSimpleName(), scmpMsg);
		}
		return;
	}
}
