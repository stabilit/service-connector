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

/**
 * The Interface IReversibleEnum.
 *
 * @param <E> the element type
 * @param <V> the value type
 * @author JTraber
 */
public interface IReversibleEnum<E, V> {

	/**
	 * Return the value/code of the enum constant.
	 *
	 * @return value
	 */
	public E getValue();

	/**
	 * Get the {@link Enum} constant by looking up the code in the reverse enum map. *
	 *
	 * @param code the code
	 * @return V - The enum constant
	 */
	public V reverse(E code);
}
