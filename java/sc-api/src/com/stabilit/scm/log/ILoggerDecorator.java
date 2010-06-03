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
package com.stabilit.sc.log;

import com.stabilit.sc.factory.IFactoryable;

/**
 * The Interface ILoggerDecorator. For decorating a concrete logger.
 */
public interface ILoggerDecorator extends IFactoryable {

	/** {@inheritDoc} */
	@Override
	public ILoggerDecorator newInstance();

	/**
	 * Gets the log directory.
	 * 
	 * @return the log directory
	 */
	public String getLogDir();

	/**
	 * Gets the log file name.
	 * 
	 * @return the log file name
	 */
	public String getLogFileName();
}
