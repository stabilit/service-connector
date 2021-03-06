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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.serviceconnector.Constants;
import org.serviceconnector.scmp.SCMPHeaderKey;
import org.serviceconnector.scmp.SCMPKeepAlive;

/**
 * The Class DefaultEncoderDecoder. Defines default SCMP encoding/decoding of object into/from stream.
 *
 * @author JTraber
 */
public class KeepAliveMessageEncoderDecoder extends MessageEncoderDecoderAdapter implements IEncoderDecoder {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = LoggerFactory.getLogger(KeepAliveMessageEncoderDecoder.class);

	/**
	 * Instantiates a new default encoder decoder.
	 */
	KeepAliveMessageEncoderDecoder() {
	}

	/** {@inheritDoc} */
	@Override
	public void encode(OutputStream os, Object obj) throws Exception {
		OutputStreamWriter osw = new OutputStreamWriter(os, Constants.SC_CHARACTER_SET);
		BufferedWriter bw = new BufferedWriter(osw);
		SCMPKeepAlive keepAlive = (SCMPKeepAlive) obj;
		if (keepAlive.isReply()) {
			SCMPHeaderKey headerKey = SCMPHeaderKey.KRS;
			this.writeHeadLine(keepAlive.getSCMPVersion(), bw, headerKey, 0, 0);
		} else {
			SCMPHeaderKey headerKey = SCMPHeaderKey.KRQ;
			this.writeHeadLine(keepAlive.getSCMPVersion(), bw, headerKey, 0, 0);
		}
		return;
	}
}
