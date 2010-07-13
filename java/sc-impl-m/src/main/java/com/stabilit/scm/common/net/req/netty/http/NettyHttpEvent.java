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
package com.stabilit.scm.common.net.req.netty.http;

import org.jboss.netty.handler.codec.http.HttpResponse;

import com.stabilit.scm.common.net.req.netty.NettyEvent;

/**
 * The Class NettyHttpEvent. Wraps a successful response of Netty framework. Used to unify the process of catching
 * the response synchronously.
 * 
 * @author JTraber
 */
public class NettyHttpEvent extends NettyEvent {

	/** The buffer. */
	private HttpResponse response;

	/**
	 * Instantiates a NettyHttpEvent.
	 * 
	 * @param response
	 *            the response
	 */
	public NettyHttpEvent(HttpResponse response) {
		this.response = response;
	}

	/**
	 * Gets the response.
	 * 
	 * @return the response
	 */
	@Override
	public HttpResponse getResponse() {
		return response;
	}
}
