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

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultCookie;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.serviceconnector.web.IWebResponse;


/**
 * The Class NettyWebResponse.
 */
public class NettyWebResponse implements IWebResponse {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(NettyWebResponse.class);

	/** The response. */
	private HttpResponse response;

	/** The os. */
	private ByteArrayOutputStream os;

	CookieEncoder ce = null;

	/**
	 * Instantiates a new netty web response.
	 * 
	 * @param httpResponse
	 *            the response
	 */
	public NettyWebResponse(HttpResponse httpResponse) {
		this.response = httpResponse;
		this.os = null;
		this.ce = null; // server side encoding
	}
	
	/** {@inheritDoc} */
	@Override
	public void addHeader(String name, String value) {
	   this.response.addHeader(name, value);
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
		this.response.addHeader("Content-Type", contentType);
	}

	/** {@inheritDoc} */
	@Override
	public void addCookie(String key, String value) {
		DefaultCookie cookie = new DefaultCookie(key, value);
		cookie.setPath("/");
		if (this.ce == null) {
			this.ce = new CookieEncoder(true);
		}
		this.ce.addCookie(cookie);
	}

	/** {@inheritDoc} */
	@Override
	public void addCookie(Cookie cookie) {
		if (this.ce == null) {
			this.ce = new CookieEncoder(true);
		}
		this.ce.addCookie(cookie);
	}

	@Override
	public void redirect(String path) {
		logger.debug("redirect location = " + path);
		response.setStatus(HttpResponseStatus.FOUND);
		response.addHeader("Location", path);
	}

	@Override
	public boolean isRedirect() {
		return response.getStatus() == HttpResponseStatus.FOUND;
	}

	public CookieEncoder getCookieEncoder() {
		return this.ce;
	}
}
