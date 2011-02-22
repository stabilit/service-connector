/*
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 */
package org.serviceconnector.web;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.Cookie;

/**
 * The Class AbstractWebRequest.
 */
public abstract class AbstractWebRequest implements IWebRequest {

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(AbstractWebRequest.class);

	/** The attr map. */
	private Map<String, Object> attrMap;

	/** The local address. */
	private InetSocketAddress localAddress;

	/**
	 * Instantiates a new abstract web request.
	 */
	public AbstractWebRequest(InetSocketAddress localAddress) {
		this.localAddress = localAddress;
		attrMap = new HashMap<String, Object>();
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(String key) {
		return this.attrMap.get(key);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(String key, Object value) {
		this.attrMap.put(key, value);

	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebRequest#getLocalAddress()
	 */
	@Override
	public InetSocketAddress getLocalAddress() {
		return this.localAddress;
	}

	/**
	 * Sets the local address.
	 * 
	 * @param localAddress
	 *            the new local address
	 */
	public void setLocalAddress(InetSocketAddress localAddress) {
		this.localAddress = localAddress;
	}

	@Override
	public String getHost() {
		return this.getLocalAddress().getHostName();
	}

	/* (non-Javadoc)
	 * @see org.serviceconnector.web.IWebRequest#getPort()
	 */
	@Override
	public int getPort() {
		return this.getLocalAddress().getPort();
	}

	/** {@inheritDoc} */
	@Override
	public IWebSession getSession(boolean create) {
		// try to get JSESSIONID from cookie request
		Cookie jsessionidCookie = this.getCookie("JSESSIONID");
		if (jsessionidCookie != null) {
			String sessionId = jsessionidCookie.getValue();
			if (sessionId != null) {
				IWebSession webSession = WebSessionRegistry.getCurrentInstance().getSession(sessionId);
				if (webSession != null) {
					// check if web session belongs to same port
					String webSessionHost = webSession.getHost();
					String myHost = this.getHost();
					if (webSessionHost != null && webSessionHost.equals(myHost)) {
						int webSessionPort = webSession.getPort();
						int myPort = this.getPort();
						if (webSessionPort == myPort) {
							return webSession;
						}
					}
				}
			}
			// session is no more valid
		}
		// check for jsessionid in request attribute
		String sessionId = (String) this.getAttribute("JSESSIONID");
		if (sessionId != null) {
			IWebSession webSession = WebSessionRegistry.getCurrentInstance().getSession(sessionId);
			if (webSession != null) {
				return webSession;
			}
		}
		if (create == true) {
			return WebSessionRegistry.getCurrentInstance().newSession();
		}
		return null;
	}

}
