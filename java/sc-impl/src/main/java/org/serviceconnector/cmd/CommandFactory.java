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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.serviceconnector.factory.IFactoryable;
import org.serviceconnector.scmp.SCMPMsgType;


/**
 * A factory for creating Command objects.
 */
public abstract class CommandFactory {

	/** The Constant logger. */
	protected static final Logger logger = Logger.getLogger(CommandFactory.class);
	
	/** The command factory. */
	protected static CommandFactory commandFactory = null;
	/** The map stores base instances by a key. */
	protected Map<String, IFactoryable> baseInstances = new ConcurrentHashMap<String, IFactoryable>();
	
	/**
	 * Instantiates a new command factory.
	 */
	public CommandFactory() {
	}

	/**
	 * Gets the current command factory.
	 * 
	 * @return the current command factory
	 */
	public static CommandFactory getCurrentCommandFactory() {
		return commandFactory;
	}

	/**
	 * Sets the current command factory.
	 * 
	 * @param commandFactory
	 *            the new current command factory
	 */
	public static void setCurrentCommandFactory(CommandFactory commandFactory) {
		CommandFactory.commandFactory = commandFactory;
	}

	/**
	 * Adds the.
	 * 
	 * @param key
	 *            the key
	 * @param factoryInstance
	 *            the factory instance
	 */
	protected void add(String key, IFactoryable factoryInstance) {
		baseInstances.put(key, factoryInstance);
	}
	
	/**
	 * Adds the command.
	 * 
	 * @param messageType
	 *            the message type
	 * @param factoryInstance
	 *            the factory instance
	 */
	public void addCommand(SCMPMsgType messageType, IFactoryable factoryInstance) {
		this.add(messageType.getValue(), factoryInstance);
	}

	/**
	 * Get command.
	 * 
	 * @param key
	 *            the key
	 * @return the command
	 * @throws Exception
	 *             the exception
	 */
	public ICommand getCommand(SCMPMsgType key) {
		IFactoryable factoryInstance = this.newInstance(key.getValue());
		return (ICommand) factoryInstance;
	}
	
	public IFactoryable newInstance(Object key) {
		IFactoryable factoryInstance = this.getInstance(key);
		if (factoryInstance == null) {
			// if key is not found return default TODO TRN => throw exception !! -> DONE by JOT
			logger.fatal("key : " + key + " not found in baseInstances of factory, returned default instance");
		}
		// invoke the base instance constructor
		return factoryInstance.newInstance();
	}
	
	public IFactoryable getInstance(Object key) {
		IFactoryable factoryInstance = baseInstances.get(key);
		return factoryInstance;
	}
}
