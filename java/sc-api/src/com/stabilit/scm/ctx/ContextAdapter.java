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
package com.stabilit.scm.ctx;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The Class ContextAdapter. Provides basic functionality for Contexts.
 * 
 * @author JTraber
 */
public class ContextAdapter implements IContext {

	/** The attr map to store data. */
	protected Map<String, Object> attrMap;

	/**
	 * Instantiates a new context adapter.
	 */
	public ContextAdapter() {
		attrMap = new ConcurrentHashMap<String, Object>();
	}

	/** {@inheritDoc} */
	@Override
	public Object getAttribute(String name) {
		return this.attrMap.get(name);
	}

	/** {@inheritDoc} */
	@Override
	public void setAttribute(String name, Object value) {
		this.attrMap.put(name, value);
	}
}