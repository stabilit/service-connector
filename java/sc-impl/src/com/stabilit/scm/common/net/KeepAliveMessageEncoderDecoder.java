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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * The Class DefaultEncoderDecoder. Defines default SCMP encoding/decoding of object into/from stream.
 * 
 * @author JTraber
 */
public class KeepAliveMessageEncoderDecoder extends MessageEncoderDecoderAdapter implements IEncoderDecoder {

	/**
	 * Instantiates a new default encoder decoder.
	 */
	KeepAliveMessageEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public IFactoryable newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public Object decode(InputStream is) throws Exception {
		InputStreamReader isr = new InputStreamReader(is, CHARSET);
		BufferedReader br = new BufferedReader(isr);
		// read heading line
		String line;
		SCMPMessage scmpMsg = null;
		int readBytes = 0;
		try {
			line = br.readLine(); // TODO performance
			readBytes += line.getBytes().length;
			readBytes += 1; // read LF
			if (line == null || line.length() <= 0) {
				return null;
			}
			// evaluating headline key and creating corresponding SCMP type
			SCMPHeadlineKey headlineKey = SCMPHeadlineKey.getKeyByHeadline(line);
			switch (headlineKey) {
			case KRS:
			case KRQ:
				scmpMsg = new SCMPKeepAlive();
				break;
			default:
				throw new EncodingDecodingException("wrong protocol in message not possible to decode");
			}
		} catch (IOException e1) {
			ExceptionPoint.getInstance().fireException(this, e1);
			throw new EncodingDecodingException("io error when decoding message", e1);
		}
		return scmpMsg;
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os, CHARSET);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPKeepAlive keepAlive = (SCMPKeepAlive) obj;
		if (keepAlive.isReply()) {
			SCMPHeadlineKey headerKey = SCMPHeadlineKey.KRS;
			writeHeadLine(bw, headerKey, 0, 0);
		} else {
			SCMPHeadlineKey headerKey = SCMPHeadlineKey.KRQ;
			writeHeadLine(bw, headerKey, 0, 0);
		}
		return;
	}
}
