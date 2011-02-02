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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class URLParameterString supports the following format:
 * 
 * This class is not synchronized.
 * 
 * key1=value1&key2=value2&...
 * 
 * All keys and values url encoded (see {@link java.net.URLEncoder}) using UTF-8
 * 
 * The parse Method decodes a given string using url decoding (see {@link java.net.URLDecoder} using UTF-8
 */

public class URLParameterString {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(URLParameterString.class);

	/** The parameters. */
	private Map<String, String> map;

	/**
	 * Instantiates a new URL parameter string.
	 */
	public URLParameterString() {
		this.map = null;
	}

	public URLParameterString(String parameterString) throws UnsupportedEncodingException {
		this.parseString(parameterString);
	}

	/**
	 * Gets the value for given key if found or null
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String getValue(String key) {
		if (this.map == null) {
			return null;
		}
		return this.map.get(key);
	}

	/**
	 * Put key and value, if same key exists then replace its value.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void put(String key, String value) {
		if (this.map == null) {
			this.map = new HashMap<String, String>();
		}
		this.map.put(key, value);
	}

	/**
	 * Parses the url encoded parameter string into this instance
	 * 
	 * @param encodedString
	 *            the encoded string
	 * @throws UnsupportedEncodingException
	 */
	public void parseString(String encodedString) throws UnsupportedEncodingException {
		if (encodedString == null) {
			return;
		}
		String[] parameterStringArray = encodedString.split(Constants.AMPERSAND_SIGN);
		this.map = null;
		this.map = new HashMap<String, String>();
		for (int i = 0; i < parameterStringArray.length; i++) {
			String[] splitted = parameterStringArray[i].split(Constants.EQUAL_SIGN);
			if (splitted.length == 2) {
				String key = URLDecoder.decode(splitted[0], Constants.URL_ENCODING);
				String value = URLDecoder.decode(splitted[1], Constants.URL_ENCODING);
				this.map.put(key, value);
			}
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
		if (this.map == null) {
			return "";
		}
		try {
			int index = 0;
			Iterator<Entry<String, String>> entryIter = this.map.entrySet().iterator();
			StringBuilder sb = new StringBuilder();
			while (entryIter.hasNext()) {
				if (index++ > 0) {
					sb.append(Constants.AMPERSAND_SIGN);
				}
				Entry<String, String> entry = entryIter.next();
				String key = entry.getKey();
				String value = entry.getValue();
				if (key != null) {
			       sb.append(URLEncoder.encode(key, Constants.URL_ENCODING));
			       sb.append(Constants.EQUAL_SIGN);
				}
				if (value != null) {
			       sb.append(URLEncoder.encode(value, Constants.URL_ENCODING));
				} else {				
				}
			}
			return sb.toString();
		} catch (UnsupportedEncodingException e) {
			logger.error("unsupported url encoding format", e);
		}
		return null;
	}
}
