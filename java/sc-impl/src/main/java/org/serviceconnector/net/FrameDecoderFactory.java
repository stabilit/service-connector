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

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;
import org.serviceconnector.ctx.AppContext;

/**
 * The Class FrameDecoderFactory. Provides access to concrete frame decoders.
 * 
 * @author JTraber
 */
public final class FrameDecoderFactory {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(FrameDecoderFactory.class);

	private AppContext appContext;

	public void initFrameDecoders(AppContext appContext) {
		this.appContext = appContext;
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
	public static IFrameDecoder getFrameDecoder(String key) {
		if (Constants.HTTP.equalsIgnoreCase(key)) {
			return new HttpFrameDecoder();
		} else if (Constants.TCP.equalsIgnoreCase(key)) {
			return new DefaultFrameDecoder();
		} else {
			logger.fatal("key : " + key + " not found!");
			throw new InvalidParameterException("key : " + key + " not found!");
		}
	}

	private void addFrameDecoder(String key, IFrameDecoder frameDecoder) {
		this.appContext.getFrameDecoders().put(key, frameDecoder);
	}
}