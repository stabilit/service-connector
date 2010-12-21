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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

/**
 * A utility class that provides a reverse map of the {@link Enum} that is keyed by the value of the {@link Enum} constant.
 * 
 * @author JTraber
 * @param <K>
 *            the key type
 * @param <V>
 *            the value type
 */
public class ReverseEnumMap<K, V extends IReversibleEnum<K, V>> {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(ReverseEnumMap.class);

	/** The reverse map. */
	private final Map<K, V> reverseMap = new HashMap<K, V>();

	/**
	 * Create a new instance of ReverseEnumMap. *
	 * 
	 * @param valueType
	 *            the value type
	 */
	public ReverseEnumMap(final Class<V> valueType) {
		for (final V v : valueType.getEnumConstants()) {
			reverseMap.put(v.getValue(), v);
		}
	}

	/**
	 * Perform the reverse lookup for the given enum value and return the enum constant.
	 * 
	 * @param enumValue
	 *            the enum value
	 * @return enum constant
	 */
	public V get(final K enumValue) {
		if (enumValue == null) {
			return null;
		}
		return reverseMap.get(enumValue);
	}
}
