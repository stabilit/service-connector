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

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.serviceconnector.web.AbstractWebRequest;

// TODO: Auto-generated Javadoc
/**
 * The Class NettyWebRequest.
 */
public class NettyWebRequest extends AbstractWebRequest {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(NettyWebRequest.class);

	/** The request. */
	private HttpRequest request;

	/** The parameters. */
	private Map<String, List<String>> parameters;

	private Set<Cookie> cookies;

	/**
	 * Instantiates a new netty web request.
	 * 
	 * @param httpRequest
	 *            the request
	 */
	public NettyWebRequest(HttpRequest httpRequest) {
		this.request = httpRequest;
		if (this.request != null) {
			// http get
			QueryStringDecoder qsd = new QueryStringDecoder(this.getURL());
			this.parameters = qsd.getParameters();
			// http post
			ChannelBuffer content = request.getContent();
			if (content.readable()) {
				String param = content.toString("UTF-8");
				QueryStringDecoder queryStringDecoder = new QueryStringDecoder("/?" + param);
				Map<String, List<String>> postParams = queryStringDecoder.getParameters();
				this.parameters.putAll(postParams);
			}
			try {
				CookieDecoder cd = new CookieDecoder();
				String cookie = this.request.getHeader("Cookie");
				if (cookie != null) {
				    this.cookies = cd.decode(cookie);
				}
			} catch (Exception e) {
				this.cookies = null;
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getURL() {
		return this.request.getUri();
	}

	/** {@inheritDoc} */
	@Override
	public String getParameter(String name) {
		List<String> paramList = this.parameters.get(name);
		if (paramList == null || paramList.size() <= 0) {
			return null;
		}
		return paramList.get(0);
	}

	/** {@inheritDoc} */
	@Override
	public List<String> getParameterList(String name) {
		List<String> paramList = this.parameters.get(name);
		return paramList;
	}

	/** {@inheritDoc} */
	@Override
	public Cookie getCookie(String key) {
		if (key == null || this.cookies == null) {
			return null;
		}
		for (Cookie cookie : cookies) {
			if (key.equals(cookie.getName())) {
				return cookie;
			}
		}
		return null;
	}

	/** {@inheritDoc} */
	@Override
	public Map<String, List<String>> getParameterMap() {
		return this.parameters;
	}
}
