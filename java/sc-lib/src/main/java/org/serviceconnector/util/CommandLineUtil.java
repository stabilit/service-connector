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

import org.apache.log4j.Logger;

/**
 * The Class CommandLineUtil. Helper class to extract content of virtual machine arguments.
 * 
 * @author JTraber
 */
public class CommandLineUtil {

	/** The Constant logger. */
	private final static Logger logger = Logger.getLogger(CommandLineUtil.class);
	
	/**
	 * Gets the arguments.
	 * 
	 * @param args
	 *            the arguments
	 * @param key
	 *            the key
	 * @return the arguments
	 */
	public static String getArg(String[] args, String key) {
		if (args == null || args.length <= 0 || key == null) {
			return null;
		}
		for (int i = 0; i < args.length; i++) {
			if (key.equals(args[i])) {
				if (i < args.length - 1) {
					return args[i + 1];
				}
				return null;
			}
		}
		return null;
	}
}
