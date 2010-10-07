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

import org.apache.log4j.Logger;
import org.serviceconnector.ctx.AppContext;
import org.serviceconnector.scmp.SCMPMessage;

/**
 * A factory for creating EncoderDecoder objects.
 * 
 * @author JTraber
 */
public final class EncoderDecoderFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(EncoderDecoderFactory.class);
	/** The Constant LARGE, key for large encoder decoder. */
	private static final String LARGE = "large";
	private static final String KEEP_ALIVE = "keepAlive";
	private static final String DEFAULT = "default";

	private AppContext appContext;
	
	public void initEncoders(AppContext appContext) {
		this.appContext = appContext;
		IEncoderDecoder encoderDecoder = new DefaultMessageEncoderDecoder();
		this.addEncoderDecoder(DEFAULT, encoderDecoder);
		encoderDecoder = new LargeMessageEncoderDecoder();
		this.addEncoderDecoder(LARGE, encoderDecoder);
		encoderDecoder = new KeepAliveMessageEncoderDecoder();
		this.addEncoderDecoder(KEEP_ALIVE, encoderDecoder);	
	}

	/**
	 * Checks if is large.
	 * 
	 * @param message
	 *            the scmp message
	 * @return true, if is large
	 */
	public boolean isLarge(SCMPMessage message) {
		if (message.isPart() || message.isBodyOffset()) {
			// message is a part or has offset for reading body - message is part of large message
			return true;
		}
		if (message.isLargeMessage()) {
			// message size is large
			return true;
		}
		return false;
	}

	/**
	 * New instance.
	 * 
	 * @param message
	 *            the scmp message
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder createEncoderDecoder(SCMPMessage message) {
		if (message.isKeepAlive()) {
			return createEncoderDecoder(KEEP_ALIVE);
		}
		if (message.isPart() || message.isBodyOffset()) {
			// message is a part or has offset for reading body - message is part of large message take large instance
			return createEncoderDecoder(LARGE);
		}
		if (message.isLargeMessage()) {
			// message size is large - take large instance
			return createEncoderDecoder(LARGE);
		}
		return createEncoderDecoder(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param scmpBuffer
	 *            the scmp message buffer
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder createEncoderDecoder(byte[] scmpBuffer) {
		if (scmpBuffer[0] == 'P') {
			// headline key start with 'P' means message must be of type part - take large instance
			return createEncoderDecoder(LARGE);
		}
		if (scmpBuffer[0] == 'K') {
			// headline key start with 'K' means message must be of type keep alive
			return createEncoderDecoder(KEEP_ALIVE);
		}
		return createEncoderDecoder(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder createEncoderDecoder(String key) {
		IEncoderDecoder encoderDecoder = AppContext.getCurrentContext().getEncodersDecoders().get(key);
		return encoderDecoder;
	}

	/**
	 * Adds the encoder decoder.
	 *
	 * @param key the key
	 * @param encoderDecoder the encoder decoder
	 */
	private void addEncoderDecoder(String key, IEncoderDecoder encoderDecoder) {
		this.appContext.getEncodersDecoders().put(key, encoderDecoder);
	}
}
