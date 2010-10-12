/*
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
 */
package org.serviceconnector.web;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jboss.netty.handler.codec.http.Cookie;

// TODO: Auto-generated Javadoc
/**
 * The Class AbstractWebRequest.
 */
public abstract class AbstractWebRequest implements IWebRequest {

	/** The Constant logger. */
	protected final static Logger logger = Logger
			.getLogger(AbstractWebRequest.class);

	/** The attr map. */
	private Map<String, Object> attrMap;

	/**
	 * Instantiates a new abstract web request.
	 */
	public AbstractWebRequest() {
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
				   return webSession;
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
