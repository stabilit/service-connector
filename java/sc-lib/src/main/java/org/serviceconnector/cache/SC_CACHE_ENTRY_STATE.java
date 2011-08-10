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

/**
 * The Enum SC_CACHE_ENTRY_STATE. Identifies the state of the entry in cache. Loading means the entry is not completed yet, somebody
 * is loading the rest of the message. Loaded marks a complete entry which is ready to deliver from cache.
 */
public enum SC_CACHE_ENTRY_STATE {

	/** The LOADING, cache entry not complete. */
	LOADING,
	/** The LOADEDm cache entry complete. */
	LOADED,
	/** The UNDEFINDED. */
	UNDEFINDED;
}
