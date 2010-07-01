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
import java.io.OutputStream;
import java.io.OutputStreamWriter;

import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.scmp.SCMPHeadlineKey;
import com.stabilit.scm.common.scmp.SCMPKeepAlive;

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
