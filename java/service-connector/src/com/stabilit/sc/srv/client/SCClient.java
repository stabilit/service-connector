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
package com.stabilit.sc.srv.client;

import com.stabilit.sc.cln.client.Client;
import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.scmp.SCMP;
import com.stabilit.sc.common.scmp.impl.EncoderDecoderFactory;
import com.stabilit.sc.common.scmp.impl.IEncoderDecoder;

/**
 * @author JTraber
 * 
 */
public class SCClient extends Client {

	public SCClient() {
	}
	
	@Override
	public IFactoryable newInstance() {
		return new SCClient();
	}

	@Override
	public SCMP sendAndReceive(SCMP scmp) throws Exception {
		IEncoderDecoder encoderDecoder = EncoderDecoderFactory.getCurrentEncoderDecoderFactory().newInstance(scmp);
		clientConnection.setEncoderDecoder(encoderDecoder);
		return clientConnection.sendAndReceive(scmp);
	}
}
