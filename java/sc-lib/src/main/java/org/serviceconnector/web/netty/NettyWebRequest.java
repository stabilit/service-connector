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

import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.handler.codec.http.Cookie;
import org.jboss.netty.handler.codec.http.CookieDecoder;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.QueryStringDecoder;
import org.serviceconnector.Constants;
import org.serviceconnector.web.AbstractWebRequest;

/**
 * The Class NettyWebRequest.
 */
public class NettyWebRequest extends AbstractWebRequest {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(NettyWebRequest.class);

	/** The request. */
	private HttpRequest request;

	/** The parameters. */
	private Map<String, List<String>> parameters;

	/** The url. */
	private String url;

	/** The cookies. */
	private Set<Cookie> cookies;

	/**
	 * Instantiates a new netty web request.
	 * 
	 * @param httpRequest
	 *            the request
	 */
	public NettyWebRequest(HttpRequest httpRequest, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.request = httpRequest;
		this.url = null;
		if (this.request != null) {
			// extract any encoded session in url
			this.parseEncodedSessionId();
			// http get
			this.parameters = new HashMap<String, List<String>>();
			QueryStringDecoder qsd = new QueryStringDecoder(this.getURL());
			Map<String, List<String>> qsdParameters = qsd.getParameters();
			if (qsdParameters != null) {
				this.parameters.putAll(qsdParameters);
			}
			// http post
			ChannelBuffer content = request.getContent();
			if (content.readable()) {
				String charsetName = Constants.SC_CHARACTER_SET;
				if (request.containsHeader(HttpHeaders.Names.ACCEPT_CHARSET)) {
					String contentType = request.getHeader(HttpHeaders.Names.ACCEPT_CHARSET);
					charsetName = contentType.indexOf("charset=") > -1 ? contentType.substring(contentType.indexOf("charset=") + 8)
							: charsetName;
				}
				Charset charset = null;
				try {
					charset = Charset.forName(charsetName);
				} catch (Exception e) {
					charset = Charset.forName(Constants.SC_CHARACTER_SET);
					LOGGER.error("invalid charset name = " + charsetName, e);
				}
				String param = content.toString(charset);
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
		if (this.url != null) {
			return this.url;
		}
		return this.request.getUri();
	}

	/** {@inheritDoc} */
	@Override
	public String getHeader(String key) {
		if (this.request == null) {
			return null;
		}
		return this.request.getHeader(key);
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
	public final Map<String, List<String>> getParameterMap() {
		return this.parameters;
	}

	/**
	 * Gets the encoded session id.
	 * 
	 * @return the encoded session id
	 */
	private String parseEncodedSessionId() {
		StringBuffer sbURL = new StringBuffer();
		String localUrl = this.getURL();
		int paramsIndex = localUrl.indexOf("?");
		String[] splittedURL = localUrl.split("[;?]");
		for (int i = 0; i < splittedURL.length; i++) {
			String splitted = splittedURL[i];
			if (splitted.startsWith("sid")) {
				if (i == 1) {
					sbURL.append(splittedURL[0]);
				}
				this.encodedSessionId = splitted.substring(4);
				if (paramsIndex >= 0) {
					sbURL.append(localUrl.substring(paramsIndex, localUrl.length()));
				}
				this.url = sbURL.toString();
				this.setAttribute("JSESSIONID", this.encodedSessionId);
				return this.encodedSessionId;
			}
		}
		this.url = localUrl;
		return null;
	}
}
