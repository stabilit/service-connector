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
package com.stabilit.sc.cln.client;

import com.stabilit.sc.net.IEncoderDecoder;

/**
 * The Class ClientConnectionAdapter.
 * 
 * @author JTraber
 */
public abstract class ClientConnectionAdapter implements IClientConnection {

	/** The encoder decoder. */
	protected IEncoderDecoder encoderDecoder;
	
	/**
	 * Instantiates a new client connection adapter.
	 */
	public ClientConnectionAdapter() {
		encoderDecoder = null;
	}
	
	/* (non-Javadoc)
	 * @see com.stabilit.sc.cln.client.IClientConnection#setEncoderDecoder(com.stabilit.sc.net.IEncoderDecoder)
	 */
	@Override
	public void setEncoderDecoder(IEncoderDecoder encoderDecoder) {
		this.encoderDecoder = encoderDecoder;
	}			
}
