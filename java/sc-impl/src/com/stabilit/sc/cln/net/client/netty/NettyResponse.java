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
package com.stabilit.sc.cln.net.client.netty;

import org.jboss.netty.buffer.ChannelBuffer;

/**
 * The Class NettyResponse. Wraps a successful response of Netty framework. Used to unify the process of catching
 * the response synchronously.
 * 
 * @author JTraber
 */
public class NettyResponse {

	/** The buffer. */
	private ChannelBuffer buffer;
	/** The is fault. */
	private boolean isFault;

	/**
	 * Instantiates a new netty response.
	 * 
	 * @param buffer
	 *            the buffer
	 */
	public NettyResponse(ChannelBuffer buffer) {
		this.buffer = buffer;
		this.isFault = false;
	}

	/**
	 * Gets the buffer.
	 * 
	 * @return the buffer
	 */
	public ChannelBuffer getBuffer() {
		return buffer;
	}

	/**
	 * Checks if is fault.
	 * 
	 * @return true, if is fault
	 */
	public boolean isFault() {
		return isFault;
	}
}
