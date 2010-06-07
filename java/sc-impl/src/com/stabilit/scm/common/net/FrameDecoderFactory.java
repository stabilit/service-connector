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

import com.stabilit.scm.factory.Factory;
import com.stabilit.scm.net.IFrameDecoder;


/**
 * The Class FrameDecoderFactory. Provides access to concrete frame decoders.
 * 
 * @author JTraber
 */
public final class FrameDecoderFactory extends Factory {

	/** The Constant HTTP, key for HTTP frame decoder instance. */
	private static final String HTTP = "http";
	/** The decoder factory. */
	private static FrameDecoderFactory decoderFactory = new FrameDecoderFactory();

	/**
	 * Gets the current instance.
	 * 
	 * @return the current instance
	 */
	public static FrameDecoderFactory getCurrentInstance() {
		return decoderFactory;
	}

	/**
	 * Instantiates a new frame decoder factory.
	 */
	private FrameDecoderFactory() {
		IFrameDecoder frameDecoder = new DefaultFrameDecoder();
		this.add(DEFAULT, frameDecoder);
		frameDecoder = new HttpFrameDecoder();
		this.add(HTTP, frameDecoder);
	}

	/**
	 * Gets the default frame decoder.
	 * 
	 * @return the default frame decoder
	 */
	public static IFrameDecoder getDefaultFrameDecoder() {
		return (IFrameDecoder) decoderFactory.newInstance(DEFAULT);
	}

	/**
	 * Gets the frame decoder.
	 * 
	 * @param key
	 *            the key
	 * @return the frame decoder
	 */
	public static IFrameDecoder getFrameDecoder(String key) {
		return (IFrameDecoder) decoderFactory.newInstance(key);
	}
}