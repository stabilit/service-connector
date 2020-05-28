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
package org.serviceconnector.web.netty;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.serviceconnector.web.IWebResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class NettyWebResponse.
 */
public class NettyWebResponse implements IWebResponse {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyWebResponse.class);
	/** The response. */
	private HttpResponse response;
	/** The os. */
	private ByteArrayOutputStream os;

	/**
	 * Instantiates a new netty web response.
	 *
	 * @param httpResponse the response
	 */
	public NettyWebResponse(HttpResponse httpResponse) {
		this.response = httpResponse;
		this.os = null;
	}

	/** {@inheritDoc} */
	@Override
	public void addHeader(String name, String value) {
		this.response.headers().add(name, value);
	}

	/** {@inheritDoc} */
	@Override
	public OutputStream getOutputStream() {
		if (this.os == null) {
			this.os = new ByteArrayOutputStream();
		}
		return this.os;
	}

	/** {@inheritDoc} */
	@Override
	public byte[] getBytes() {
		if (this.os == null) {
			return null;
		}
		return this.os.toByteArray();
	}

	/** {@inheritDoc} */
	@Override
	public void setContentType(String contentType) {
		this.response.headers().add("Content-Type", contentType);
	}

	@Override
	public void redirect(String path) {
		LOGGER.debug("redirect location = " + path);
		response.setStatus(HttpResponseStatus.FOUND);
		response.headers().add("Location", path);
	}

	@Override
	public boolean isRedirect() {
		return response.getStatus() == HttpResponseStatus.FOUND;
	}
}
