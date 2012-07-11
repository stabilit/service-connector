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
package org.serviceconnector.cache;

import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

/**
 * The Enumeration SC_CACHING_METHOD. Used by the server to publish appendix!
 */
public enum SC_CACHING_METHOD implements IReversibleEnum<String, SC_CACHING_METHOD> {

	INITIAL("initial"), // this message is a base message for possible appendix
	APPEND("append"), // this message is an appendix for an initial message
	REMOVE("remove"), // this message has removed a cached managed message
	NOT_MANAGED(""); // default state for unmanaged (no appends possible) messages

	/** The value. */
	private String value;

	/** The reverseMap, to get access to the enumeration constants by string value. */
	private static final ReverseEnumMap<String, SC_CACHING_METHOD> REVERSE_MAP = new ReverseEnumMap<String, SC_CACHING_METHOD>(
			SC_CACHING_METHOD.class);

	/**
	 * Instantiates a new caching method.
	 * 
	 * @param value
	 *            the value
	 */
	private SC_CACHING_METHOD(String value) {
		this.value = value;
	}

	/**
	 * Gets the caching method.
	 * 
	 * @param methodString
	 *            the method string
	 * @return the caching method
	 */
	public static SC_CACHING_METHOD getCachingMethod(String methodString) {
		SC_CACHING_METHOD method = REVERSE_MAP.get(methodString);
		if (method == null) {
			// methodString doesn't match to a valid type
			return SC_CACHING_METHOD.NOT_MANAGED;
		}
		return method;
	}

	/** {@inheritDoc} */
	public String getValue() {
		return this.value;
	}

	/** {@inheritDoc} */
	@Override
	public SC_CACHING_METHOD reverse(String methodString) {
		return SC_CACHING_METHOD.getCachingMethod(methodString);
	}
}
