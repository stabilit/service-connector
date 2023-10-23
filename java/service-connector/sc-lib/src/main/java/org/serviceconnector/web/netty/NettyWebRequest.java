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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.QueryStringDecoder;
import org.serviceconnector.Constants;
import org.serviceconnector.web.AbstractWebRequest;

/**
 * The Class NettyWebRequest.
 */
public class NettyWebRequest extends AbstractWebRequest {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(NettyWebRequest.class);

	/** The request. */
	private FullHttpRequest request;

	/** The parameters. */
	private Map<String, List<String>> parameters;

	/** The url. */
	private String url;

	/**
	 * Instantiates a new netty web request.
	 *
	 * @param httpRequest the request
	 * @param localAddress the local address
	 * @param remoteAddress the remote address
	 */
	public NettyWebRequest(FullHttpRequest httpRequest, InetSocketAddress localAddress, InetSocketAddress remoteAddress) {
		super(localAddress, remoteAddress);
		this.request = httpRequest;
		this.url = null;
		if (this.request != null) {
			// extract any encoded session in url
			this.parseEncodedSessionId();
			// http get
			this.parameters = new HashMap<String, List<String>>();
			QueryStringDecoder qsd = new QueryStringDecoder(this.getURL());
			Map<String, List<String>> qsdParameters = qsd.parameters();
			if (qsdParameters != null) {
				this.parameters.putAll(qsdParameters);
			}
			// http post
			ByteBuf content = request.content();
			if (content.isReadable()) {
				String charsetName = Constants.SC_CHARACTER_SET;
				if (request.headers().contains(HttpHeaderNames.ACCEPT_CHARSET)) {
					String contentType = request.headers().get(HttpHeaderNames.ACCEPT_CHARSET);
					charsetName = contentType.indexOf("charset=") > -1 ? contentType.substring(contentType.indexOf("charset=") + 8) : charsetName;
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
				Map<String, List<String>> postParams = queryStringDecoder.parameters();
				this.parameters.putAll(postParams);
			}
		}
	}

	/** {@inheritDoc} */
	@Override
	public String getURL() {
		if (this.url != null) {
			return this.url;
		}
		return this.request.uri();
	}

	/** {@inheritDoc} */
	@Override
	public String getHeader(String key) {
		if (this.request == null) {
			return null;
		}
		return this.request.headers().get(key);
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
