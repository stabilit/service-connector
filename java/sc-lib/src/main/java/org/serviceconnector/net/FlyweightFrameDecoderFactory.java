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

import java.security.InvalidParameterException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class FrameDecoderFactory. Factory is based on the Flyweight pattern
 * (http://www.allapplabs.com/java_design_patterns/flyweight_pattern.htm). FrameDecoders are only instantiated one time.
 * Factory is always returning the same instance from a map.
 * 
 * @author JTraber
 */
public final class FlyweightFrameDecoderFactory {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(FlyweightFrameDecoderFactory.class);
	private static Map<String, IFrameDecoder> frameDecoders;

	public FlyweightFrameDecoderFactory() {
		FlyweightFrameDecoderFactory.frameDecoders = new ConcurrentHashMap<String, IFrameDecoder>();
		IFrameDecoder frameDecoder = new DefaultFrameDecoder();
		this.addFrameDecoder(Constants.TCP, frameDecoder);
		frameDecoder = new HttpFrameDecoder();
		this.addFrameDecoder(Constants.HTTP, frameDecoder);
	}

	/**
	 * Gets the frame decoder.
	 * 
	 * @param key
	 *            the key
	 * @return the frame decoder
	 */
	public IFrameDecoder getFrameDecoder(String key) {
		IFrameDecoder frameDecoder = FlyweightFrameDecoderFactory.frameDecoders.get(key);
		if (frameDecoder == null) {
			logger.fatal("key : " + key + " not found!");
			throw new InvalidParameterException("key : " + key + " not found!");
		}
		return frameDecoder;
	}

	private void addFrameDecoder(String key, IFrameDecoder frameDecoder) {
		FlyweightFrameDecoderFactory.frameDecoders.put(key, frameDecoder);
	}
}