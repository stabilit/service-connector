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

import java.io.OutputStream;

import org.jboss.netty.handler.codec.http.Cookie;


/**
 * The Interface IWebResponse abstracts a web response.
 */
public interface IWebResponse {

	/**
	 * Adds the header.
	 *
	 * @param name the name
	 * @param value the value
	 */
	void addHeader(String name, String value);

	/**
	 * Gets the output stream.
	 * 
	 * @return the output stream
	 */
	public abstract OutputStream getOutputStream();

	/**
	 * Gets the bytes.
	 * 
	 * @return the bytes
	 */
	public abstract byte[] getBytes();

	/**
	 * Sets the content type.
	 * 
	 * @param string
	 *            the new content type
	 */
	public abstract void setContentType(String string);

	/**
	 * Adds the cookie.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public abstract void addCookie(String key, String value);

	/**
	 * Adds the cookie.
	 * 
	 * @param cookie
	 *            the cookie
	 */
	public abstract void addCookie(Cookie cookie);

	/**
	 * Redirect.
	 * 
	 * @param string
	 *            the string
	 */
	public abstract void redirect(String path);

	public abstract boolean isRedirect();


}
