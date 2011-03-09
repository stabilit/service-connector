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

/**
 * The Interface IWebSession.
 */
public interface IWebSession {

	/**
	 * Update access timestamp.
	 */
	public abstract void access();

	/**
	 * Checks if is expired.
	 *
	 * @param timeoutMinutes the timeout minutes
	 * @return true, if is expired
	 */
	public boolean isExpired(long timeoutMinutes);
	
	/**
	 * Gets the session id.
	 *
	 * @return the session id
	 */
	public abstract String getSessionId();

	/**
	 * Gets the user agent.
	 *
	 * @return the user agent
	 */
	public abstract String getUserAgent();

	/**
	 * Sets the user agent.
	 *
	 * @param userAgent the new user agent
	 */
	public abstract void setUserAgent(String userAgent);

	/**
	 * Gets the host.
	 *
	 * @return the host
	 */
	public abstract String getHost();

	/**
	 * Sets the host.
	 * 
	 * @param host
	 *            the new host
	 */
	public abstract void setHost(String host);
	
	/**
	 * Gets the remote host.
	 *
	 * @return the remote host
	 */
	public abstract String getRemoteHost();

	/**
	 * Sets the remote host host.
	 * 
	 * @param host
	 *            the new remote host
	 */
	public abstract void setRemoteHost(String host);

	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public abstract int getPort();
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public abstract void setPort(int port);
	/**
	 * Gets the port.
	 *
	 * @return the port
	 */
	public abstract int getRemotePort();
	
	/**
	 * Sets the port.
	 *
	 * @param port the new port
	 */
	public abstract void setRemotePort(int port);
	
	/**
	 * Gets the attribute.
	 *
	 * @param key the key
	 * @return the attribute
	 */
	public abstract Object getAttribute(String key);

	/**
	 * Sets the attribute.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public abstract void setAttribute(String key, Object value);

	/**
	 * Removes the attribute.
	 *
	 * @param key the key
	 * @return the object
	 */
	public abstract Object removeAttribute(String key);

}
