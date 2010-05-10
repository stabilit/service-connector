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
package com.stabilit.sc.net;

import com.stabilit.sc.factory.Factory;
import com.stabilit.sc.factory.IFactoryable;
import com.stabilit.sc.scmp.SCMP;

/**
 * A factory for creating EncoderDecoder objects.
 * 
 * @author JTraber
 */
public class EncoderDecoderFactory extends Factory {

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

	/*
	 * (non-Javadoc)
	 * @see com.stabilit.sc.factory.Factory#newInstance()
	 */
	public IFactoryable newInstance() {
		return newInstance(DEFAULT);
	}

	/**
	 * Checks if is large.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return true, if is large
	 */
	public boolean isLarge(SCMP scmp) {

		if (scmp.isPart() || scmp.isBodyOffset()) {
			// scmp is a part or has offset for reading body - scmp is part of large message
			return true;
		}
		if (scmp.isLargeMessage()) {
			// scmp size is large
			return true;
		}
		return false;
	}

	/**
	 * New instance.
	 * 
	 * @param scmp
	 *            the scmp
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder newInstance(SCMP scmp) {
		if (scmp.isPart() || scmp.isBodyOffset()) {
			// scmp is a part or has offset for reading body - scmp is part of large message take large instance
			return newInstance(LARGE);
		}
		if (scmp.isLargeMessage()) {
			// scmp size is large - take large instance
			return newInstance(LARGE);
		}
		return newInstance(DEFAULT);
	}

	/**
	 * New instance.
	 * 
	 * @param scmpBuffer
	 *            the scmp buffer
	 * @return the i encoder decoder
	 */
	public IEncoderDecoder newInstance(byte[] scmpBuffer) {
		if (scmpBuffer[0] == 'P') {
			// headline key start with 'P' means scmp must be of type part - take large instance
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
