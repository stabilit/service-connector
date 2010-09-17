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
import org.serviceconnector.common.conf.Constants;
import org.serviceconnector.common.factory.Factory;
import org.serviceconnector.common.net.IFrameDecoder;


/**
 * The Class FrameDecoderFactory. Provides access to concrete frame decoders.
 * 
 * @author JTraber
 */
public final class FrameDecoderFactory extends Factory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FrameDecoderFactory.class);
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
		this.add(Constants.TCP, frameDecoder);
		frameDecoder = new HttpFrameDecoder();
		this.add(Constants.HTTP, frameDecoder);
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