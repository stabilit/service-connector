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
package org.serviceconnector.cmd;

import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.serviceconnector.scmp.SCMPMsgType;

/**
 * A factory for creating FlyweightCommand objects. Factory is based on the Flyweight pattern
 * (http://www.allapplabs.com/java_design_patterns/flyweight_pattern.htm). Commands are only instantiated one time. Factory is
 * always
 * returning the same instance from a map.
 */
public abstract class FlyweightCommandFactory {

	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(FlyweightCommandFactory.class);
	/** The map stores base instances by a key. */
	protected static Map<String, ICommand> commands = new HashMap<String, ICommand>();

	public FlyweightCommandFactory() {
	}

	/**
	 * Adds the command.
	 * 
	 * @param messageType
	 *            the message type
	 * @param command
	 *            the command
	 */
	public void addCommand(SCMPMsgType messageType, ICommand command) {
		FlyweightCommandFactory.commands.put(messageType.getValue(), command);
	}

	/**
	 * Gets the command.
	 * 
	 * @param key
	 *            the key
	 * @return the command
	 */
	public ICommand getCommand(SCMPMsgType key) {
		ICommand command = FlyweightCommandFactory.commands.get(key.getValue());
		if (command == null) {
			LOGGER.error("key : " + key + " not found!");
			throw new InvalidParameterException("key : " + key + " not found!");
		}
		return command;
	}
}
