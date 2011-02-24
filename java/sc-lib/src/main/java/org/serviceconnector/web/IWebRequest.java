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
package org.serviceconnector.web;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Map;

import org.jboss.netty.handler.codec.http.Cookie;

/**
 * The Interface IWebRequest abstracts a web request.
 */
public interface IWebRequest {

	/**
	 * Gets the local server address.
	 * 
	 * @return the local address
	 */
	public InetSocketAddress getLocalAddress();

	/**
	 * Gets the remote address.
	 * 
	 * @return the remote address
	 */
	public InetSocketAddress getRemoteAddress();

	/**
	 * Gets the host.
	 * 
	 * @return the host
	 */
	public String getHost();

	/**
	 * Gets the port.
	 * 
	 * @return the port
	 */
	public int getPort();

	/**
	 * Gets the remote host.
	 * 
	 * @return the remote host
	 */
	public String getRemoteHost();

	/**
	 * Gets the remote port.
	 * 
	 * @return the remote port
	 */
	public int getRemotePort();

	/**
	 * Returns the URI (or path) of this request.
	 * 
	 * @return the uRL
	 */
	public abstract String getURL();

	/**
	 * Gets the cookie.
	 * 
	 * @param key
	 *            the key
	 * @return the cookie
	 */
	public abstract Cookie getCookie(String key);

	/**
	 * Gets the http header for given key if any otherwise null
	 *
	 * @param key the key
	 * @return the header
	 */
	public abstract String getHeader(String key);

	/**
	 * Gets the parameter.
	 * 
	 * @param string
	 *            the string
	 * @return the parameter
	 */
	public abstract String getParameter(String string);

	/**
	 * Gets the parameter list.
	 * 
	 * @param string
	 *            the string
	 * @return the parameter list
	 */
	public abstract List<String> getParameterList(String string);

	/**
	 * Gets the attribute.
	 * 
	 * @param key
	 *            the key
	 * @return the attribute
	 */
	public abstract Object getAttribute(String key);

	/**
	 * Sets the attribute.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public abstract void setAttribute(String key, Object value);

	/**
	 * Gets the session.
	 * 
	 * @param create
	 *            the create
	 * @return the session
	 */
	public abstract IWebSession getSession(boolean create);

	/**
	 * Gets the parameter map.
	 * 
	 * @return the parameter map
	 */
	public abstract Map<String, List<String>> getParameterMap();

}
