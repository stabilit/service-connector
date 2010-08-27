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
package com.stabilit.scm.common.net;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.log.IExceptionLogger;
import com.stabilit.scm.common.log.IMessageLogger;
import com.stabilit.scm.common.log.impl.ExceptionLogger;
import com.stabilit.scm.common.log.impl.MessageLogger;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.scmp.internal.SCMPInternalStatus;

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
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPMessage scmpMsg = (SCMPMessage) obj;
		IMessageLogger messageLogger = MessageLogger.getInstance();
		
		if (scmpMsg.isGroup() == false) {
			// no group call reset internal status, if group call internal status already set
			scmpMsg.setInternalStatus(SCMPInternalStatus.NONE);
		}

		// evaluate right headline key from SCMP type
		SCMPHeadlineKey headerKey = SCMPHeadlineKey.UNDEF;
		if (scmpMsg.isReply()) {
			headerKey = SCMPHeadlineKey.RES;
			if (scmpMsg.isFault()) {
				headerKey = SCMPHeadlineKey.EXC;
			}
		} else {
			headerKey = SCMPHeadlineKey.REQ;
		}
		
		StringBuilder sb = this.writeHeader(scmpMsg.getHeader());
		
		int headerSize = sb.length();
		// write body depends on body type
		Object body = scmpMsg.getBody();
		try {
			if (body != null) {
				if (body instanceof byte[]) {
					byte[] ba = (byte[]) body;
					int messageLength = sb.length() + ba.length;
					this.writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString());
					bw.flush();
					os.write((byte[]) ba);
					os.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					messageLogger.logMessage(this.getClass().getName(), scmpMsg);
					return;
				}
				if (String.class == body.getClass()) {
					String t = (String) body;
					int messageLength = sb.length() + t.length();
					this.writeHeadLine(bw, headerKey, messageLength, headerSize);
					bw.write(sb.toString()); // write header
					bw.flush();
					bw.write(t); // write body
					bw.flush();
					scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
					messageLogger.logMessage(this.getClass().getName(), scmpMsg);
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
			IExceptionLogger exceptionLogger = ExceptionLogger.getInstance();
			exceptionLogger.logErrorException(logger, this.getClass().getName(), ex);
			scmpMsg.setInternalStatus(SCMPInternalStatus.FAILED);
			throw new EncodingDecodingException("io error when decoding message", ex);
		}
		scmpMsg.setInternalStatus(SCMPInternalStatus.getInternalStatus(headerKey));
		messageLogger.logMessage(this.getClass().getName(), scmpMsg);
		return;
	}
}