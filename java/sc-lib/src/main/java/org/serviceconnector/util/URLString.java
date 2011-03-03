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
import java.util.Set;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.serviceconnector.Constants;

/**
 * The Class URLString supports the following format:
 * key1=value1&key2=value2&...
 * All keys and values url encoded (see {@link java.net.URLEncoder}) using default encoding
 * The parse Method decodes a given string using url decoding (see {@link java.net.URLDecoder} using default encoding
 * This class is not synchronized.
 */
public class URLString {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = Logger.getLogger(URLString.class);
	/** The parameters. */
	private Map<String, String> map;
	/** The call key. */
	private String callKey;

	/**
	 * Instantiates a new URL response string.
	 */
	public URLString() {
		this.map = new HashMap<String, String>();
	}

	/**
	 * Gets the value for given key if found or null
	 * 
	 * @param key
	 *            the key
	 * @return the value
	 */
	public String getParamValue(String key) {
		return this.map.get(key);
	}

	/**
	 * Gets the response parameters.
	 * 
	 * @return the entries
	 */
	public Set<Entry<String, String>> getParameters() {
		return this.map.entrySet();
	}

	/**
	 * Put key and value, if same key exists then replace its value.
	 * 
	 * @param key
	 *            the key
	 * @param value
	 *            the value
	 */
	public void putParam(String key, String value) {
		this.map.put(key, value);
	}

	public String getCallKey() {
		return this.callKey;
	}

	public void setCallKey(String callKey) {
		this.callKey = callKey;
	}

	/**
	 * Parses the url encoded parameter string into this instance
	 * 
	 * @param encodedString
	 *            the encoded string
	 * @throws UnsupportedEncodingException
	 */
	public void parseResponseURLString(String encodedString) throws UnsupportedEncodingException {
		if (encodedString == null) {
			return;
		}
		try {
			String[] parameterStringArray = encodedString.split(Constants.AMPERSAND_SIGN);
			this.map = new HashMap<String, String>();
			for (int i = 0; i < parameterStringArray.length; i++) {
				String[] splitted = parameterStringArray[i].split(Constants.EQUAL_SIGN);
				if (splitted.length == 2) {
					String key = URLDecoder.decode(splitted[0], Constants.SC_CHARACTER_SET);
					String value = URLDecoder.decode(splitted[1], Constants.SC_CHARACTER_SET);
					this.map.put(key, value);
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			throw new UnsupportedEncodingException("unsupported url response string");
		}
	}

	public void parseRequestURLString(String encodedString) throws UnsupportedEncodingException {
		if (encodedString == null) {
			return;
		}
		try {
			int posQuestionMark = encodedString.indexOf(Constants.QUESTION_MARK);
			LOGGER.trace("position if question mark " + posQuestionMark);
			LOGGER.trace("encodedString=" + encodedString);
			if (posQuestionMark != -1) {
				this.callKey = encodedString.substring(0, posQuestionMark);
			} else {
				this.callKey = encodedString;
				return;
			}
			String params = encodedString.substring(posQuestionMark + 1);
			LOGGER.trace("params found: params=" + params);
			String[] keyValuePairs = params.split("\\" + Constants.AMPERSAND_SIGN);
			for (int i = 0; i < keyValuePairs.length; i++) {
				String keyValuePair = keyValuePairs[i];
				String[] keyValue = keyValuePair.split(Constants.EQUAL_SIGN);
				String key = URLDecoder.decode(keyValue[0], Constants.SC_CHARACTER_SET);
				String value = URLDecoder.decode(keyValue[1], Constants.SC_CHARACTER_SET);
				LOGGER.trace("parameter parsed: key=" + key + " value=" + value);
				this.map.put(key, value);
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			throw new UnsupportedEncodingException("unsupported url encoding format");
		}
	}

	/**
	 * return url encoded string
	 * callKey=parameter1&parameter2&parameter3&...
	 * 
	 * @return the string
	 */
	public String toString() {
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
				sb.append(URLEncoder.encode(key, Constants.SC_CHARACTER_SET));
				sb.append(Constants.EQUAL_SIGN);
				if (value != null) {
					sb.append(URLEncoder.encode(value, Constants.SC_CHARACTER_SET));
				} else {
					sb.append(URLEncoder.encode("", Constants.SC_CHARACTER_SET));
				}
			}
			return sb.toString();
		} catch (Exception e) {
			LOGGER.debug(e);
			LOGGER.debug("unsupported url encoding format", e);
		}
		return null;
	}

	/**
	 * Converts the key value map into a URL response string.
	 * 
	 * @param parameters
	 *            the parameters
	 * @return the string
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public static String toURLResponseString(String... parameters) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		try {
			for (int i = 0; i < parameters.length - 1; i += 2) {
				sb.append(URLEncoder.encode(parameters[i], Constants.SC_CHARACTER_SET));
				sb.append(Constants.EQUAL_SIGN);
				sb.append(URLEncoder.encode(parameters[i + 1], Constants.SC_CHARACTER_SET));
				if (i != parameters.length - 1) {
					sb.append(Constants.AMPERSAND_SIGN);
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			throw new UnsupportedEncodingException("unsupported response parameters");
		}
		return sb.toString();
	}

	/**
	 * Converts the key value map into a URL response string.
	 * 
	 * @param responseParameter
	 *            the response parameter
	 * @return the string
	 * @throws UnsupportedEncodingException
	 *             the unsupported encoding exception
	 */
	public static String toURLResponseString(Map<String, String> responseParameter) throws UnsupportedEncodingException {
		int index = 0;
		Iterator<Entry<String, String>> entryIter = responseParameter.entrySet().iterator();
		StringBuilder sb = new StringBuilder();
		try {
			while (entryIter.hasNext()) {
				if (index++ > 0) {
					sb.append(Constants.AMPERSAND_SIGN);
				}
				Entry<String, String> entry = entryIter.next();
				String key = entry.getKey();
				String value = entry.getValue();

				sb.append(URLEncoder.encode(key, Constants.SC_CHARACTER_SET));
				sb.append(Constants.EQUAL_SIGN);
				if (value != null) {
					sb.append(URLEncoder.encode(value, Constants.SC_CHARACTER_SET));
				} else {
					sb.append(URLEncoder.encode("", Constants.SC_CHARACTER_SET));
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			throw new UnsupportedEncodingException("unsupported response parameters");
		}
		return sb.toString();
	}

	public static String toURLRequestString(String... parameters) throws UnsupportedEncodingException {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(URLEncoder.encode(parameters[0], Constants.SC_CHARACTER_SET));
			if (parameters.length > 1) {
				sb.append(Constants.QUESTION_MARK);
			}
			for (int i = 1; i < parameters.length - 1; i += 2) {
				sb.append(URLEncoder.encode(parameters[i], Constants.SC_CHARACTER_SET));
				sb.append(Constants.EQUAL_SIGN);
				sb.append(URLEncoder.encode(parameters[i + 1], Constants.SC_CHARACTER_SET));
				if (i != parameters.length - 2) {
					sb.append(Constants.AMPERSAND_SIGN);
				}
			}
		} catch (Exception e) {
			LOGGER.debug(e);
			throw new UnsupportedEncodingException("unsupported request parameters");
		}
		return sb.toString();
	}
}
