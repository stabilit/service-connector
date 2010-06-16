/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package com.stabilit.scm.common.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import com.stabilit.scm.common.scmp.IInternalMessage;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public abstract class MessageEncoderDecoderAdapter implements IEncoderDecoder{

	protected Object decodeInternalMessage(BufferedReader br, SCMPMessage scmpMsg) throws IOException,
			ClassNotFoundException, InstantiationException, IllegalAccessException {
		String classLine = br.readLine();
		if (classLine == null) {
			return null;
		}
		String[] t = classLine.split(EQUAL_SIGN);
		if (IInternalMessage.class.getName().equals(t[0]) == false) {
			return null;
		}
		if (t.length != 2) {
			return null;
		}
		Class<?> messageClass = Class.forName(t[1]);
		IInternalMessage message = (IInternalMessage) messageClass.newInstance();
		message.decode(br);
		scmpMsg.setBody(message);
		return scmpMsg;
	}

	protected Object decodeTextData(BufferedReader br, SCMPMessage scmpMsg, String scmpBodyLength) throws IOException {
		int caLength = Integer.parseInt(scmpBodyLength);
		char[] caBuffer = new char[caLength];
		br.read(caBuffer);
		String bodyString = new String(caBuffer, 0, caLength);
		scmpMsg.setBody(bodyString);
		return scmpMsg;
	}

	protected Object decodeBinaryData(InputStream is, SCMPMessage scmpMsg, int readBytes, String scmpBodyLength)
			throws IOException {
		int baLength = Integer.parseInt(scmpBodyLength);
		byte[] baBuffer = new byte[baLength];
		is.reset();
		is.skip(readBytes);
		is.read(baBuffer);
		scmpMsg.setBody(baBuffer);
		return scmpMsg;
	}
}
