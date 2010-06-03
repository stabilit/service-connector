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

/**
 * The Class NettyEvent. Wraps a successful response of Netty framework. Used to unify the process of catching the
 * response synchronously.
 * 
 * @author JTraber
 */
public abstract class NettyEvent {

	/**
	 * Gets the buffer.
	 * 
	 * @return the buffer
	 */
	public abstract Object getResponse();

	/**
	 * Checks if is fault.
	 * 
	 * @return true, if is fault
	 */
	public boolean isFault() {
		return false;
	}
}
