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
package org.serviceconnector.web.cmd;

import org.apache.log4j.Logger;
import org.serviceconnector.web.IWebRequest;
import org.serviceconnector.web.IWebResponse;
import org.serviceconnector.web.IWebSession;
import org.serviceconnector.web.LoginException;

/**
 * The Class NullWebCommandAccessible. Prevents null pointer exception when
 * command does not implement the accessible interface. Throws more specific
 * exception (AccessibleException).
 * 
 * @author JTraber
 */
public final class NullWebCommandAccessible implements IWebCommandAccessible {

	/** The Constant LOGGER. */
	@SuppressWarnings("unused")
	private final static Logger LOGGER = Logger.getLogger(NullWebCommandAccessible.class);

	/** The null command accessible. */
	private static IWebCommandAccessible nullCommandAccessible = new NullWebCommandAccessible();

	/**
	 * New instance.
	 * 
	 * @return the command accessible
	 */
	public static IWebCommandAccessible newInstance() {
		return nullCommandAccessible;
	}

	/** {@inheritDoc} */
	@Override
	public IWebSession login(IWebRequest request, IWebResponse response) throws Exception {
		throw new LoginException("not authorized");
	}

	/** {@inheritDoc} */
	@Override
	public boolean isAccessible(IWebRequest request) throws Exception {
		return false;
	}

	/** {@inheritDoc} */
	@Override
	public void logout(IWebRequest request) throws Exception {
		throw new LoginException("not authorized");
	}

	/** {@inheritDoc} */
	@Override
	public IWebCommandAccessibleContext getAccessibleContext() {
		return null;
	}
}
