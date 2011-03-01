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

package org.serviceconnector.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class URLRequestString supports the following format:
 * callKey=parameter1&parameter2&parameter3&...
 * All parameters and the call key are url encoded (see {@link java.net.URLEncoder}) using UTF-8
 * The parse Method decodes a given string using url decoding (see {@link java.net.URLDecoder} using UTF-8
 * This class is not synchronized.
 */
public class URLRequestString {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(URLRequestString.class);
	/** The call key. */
	private String callKey;
	/** The parameters. */
	private String[] parameters;

	/**
	 * Instantiates a new URL request string.
	 */
	public URLRequestString() {
		this.callKey = null;
		this.parameters = null;
	}
	
	/**
	 * Instantiates a new request string.
	 * 
	 * @param callKey
	 *            the call key
	 * @param parameters
	 *            the parameters
	 */
	public URLRequestString(String callKey, String... parameters) {
		this.callKey = callKey;
		this.parameters = parameters;
	}

	/**
	 * Gets the call key.
	 * 
	 * @return the call key
	 */
	public String getCallKey() {
		return callKey;
	}

	/**
	 * Sets the call key.
	 * 
	 * @param callKey
	 *            the new call key
	 */
	public void setCallKey(String callKey) {
		this.callKey = callKey;
	}

	/**
	 * Gets the parameters.
	 * 
	 * @return the parameters
	 */
	public String[] getParameters() {
		return parameters;
	}
	
	/**
	 * Gets the parameter for given index otherwise null.
	 *
	 * @param index the index
	 * @return the parameter
	 */
	public String getParameter(int index) {
		if (this.parameters == null || index < 0 || index >= this.parameters.length) {
			return null;
		}
		return this.parameters[index];
	}

	/**
	 * Sets the parameters.
	 * 
	 * @param parameters
	 *            the new parameters
	 */
	public void setParameters(String[] parameters) {
		this.parameters = parameters;
	}

	/**
	 * Parses the url encoded string into this instance
	 * 
	 * @param encodedString
	 *            the encoded string
	 * @throws UnsupportedEncodingException
	 */
	public void parseString(String encodedString) throws UnsupportedEncodingException {
		if (encodedString == null) {
			return;
		}
		String[] splitted = encodedString.split(Constants.EQUAL_SIGN);
		if (splitted.length <= 0 || splitted.length > 2) {
			throw new UnsupportedEncodingException("unsupported url call string format");
		}
		this.callKey = splitted[0];
		if (splitted.length == 1) {
			this.parameters = null;
			return;
		}
		String[] parameterStringArray = splitted[1].split(Constants.AMPERSAND_SIGN);
		this.parameters = new String[parameterStringArray.length];
		for (int i = 0; i < parameterStringArray.length; i++) {
			this.parameters[i] = URLDecoder.decode(parameterStringArray[i], Constants.URL_ENCODING);			
		}
	}

	/**
	 * return url encoded string
	 * 
	 * callKey=parameter1&parameter2&parameter3&...
	 * 
	 * @return the string
	 */
	public String toString() {
		try {
			StringBuilder sb = new StringBuilder();
			if (this.callKey != null) {
				sb.append(URLEncoder.encode(this.callKey, Constants.URL_ENCODING));
			}
			if (this.parameters == null || this.parameters.length == 0) {
				return sb.toString();
			}
			sb.append(Constants.EQUAL_SIGN);
			for (int i = 0; i < this.parameters.length; i++) {
				if (i > 0) {
					sb.append(Constants.AMPERSAND_SIGN);
				}
				sb.append(URLEncoder.encode(this.parameters[i], Constants.URL_ENCODING));
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("unsupported url encoding format", e);
		}
		return null;
	}
}
