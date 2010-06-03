/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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
package com.stabilit.scm.net;

import com.stabilit.scm.factory.Factory;
import com.stabilit.scm.factory.IFactoryable;
import com.stabilit.scm.scmp.SCMPMessage;

/**
 * A factory for creating EncoderDecoder objects.
 * 
 * @author JTraber
 */
public final class EncoderDecoderFactory extends Factory {

	/** The Constant LARGE, key for large encoder decoder. */
	private static final String LARGE = "large";
	/** The encoder decoder factory. */
	private static EncoderDecoderFactory encoderDecoderFactory = new EncoderDecoderFactory();

	/**
	 * Gets the current encoder decoder factory.
	 * 
	 * @return the current encoder decoder factory
	 */
	public static EncoderDecoderFactory getCurrentEncoderDecoderFactory() {
		return encoderDecoderFactory;
	}

	/**
	 * Instantiates a new encoder decoder factory.
	 */
	private EncoderDecoderFactory() {
		IEncoderDecoder encoderDecoder = new DefaultEncoderDecoder();
		this.add(DefaultEncoderDecoder.class.getName(), encoderDecoder);
		this.add(DEFAULT, encoderDecoder);
		encoderDecoder = new LargeMessageEncoderDecoder();
		this.add(LargeMessageEncoderDecoder.class.getName(), encoderDecoder);
		this.add(LARGE, encoderDecoder);
	}

	/** {@inheritDoc} */
	public IFactoryable newInstance() {
		return newInstance(DEFAULT);
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
	public IEncoderDecoder newInstance(SCMPMessage message) {
		if (message.isPart() || message.isBodyOffset()) {
			// message is a part or has offset for reading body - message is part of large message take large instance
			return newInstance(LARGE);
		}
		if (message.isLargeMessage()) {
			// message size is large - take large instance
			return newInstance(LARGE);
		}
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param scmpBuffer
	 *            the scmp message buffer
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder newInstance(byte[] scmpBuffer) {
		if (scmpBuffer[0] == 'P') {
			// headline key start with 'P' means message must be of type part - take large instance
			return newInstance(LARGE);
		}
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param key
	 *            the key
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder newInstance(String key) {
		IEncoderDecoder encoderDecoder = (IEncoderDecoder) super.newInstance(key);
		return encoderDecoder;
	}

	/**
	 * Gets the encoder decoders.
	 * 
	 * @return the encoder decoders
	 */
	public Object[] getEncoderDecoders() {
		return this.factoryMap.keySet().toArray();
	}
}
