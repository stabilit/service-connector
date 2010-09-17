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
package org.serviceconnector.srv.cmd.factory.impl;

import org.apache.log4j.Logger;
import org.serviceconnector.common.cmd.factory.CommandFactory;
import org.serviceconnector.srv.ps.cmd.factory.impl.PublishServerCommandFactory;
import org.serviceconnector.srv.rr.cmd.factory.impl.SessionServerCommandFactory;


/**
 * A factory for creating UnitServerCommand objects. Unifies all commands used by publish and session server.
 * 
 * @author JTraber
 */
public class UnitServerCommandFactory extends CommandFactory {
	
	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(UnitServerCommandFactory.class);
	
	@SuppressWarnings("unused")
	public UnitServerCommandFactory() {
		SessionServerCommandFactory sessionServerCommandFactory = new SessionServerCommandFactory(this);
		PublishServerCommandFactory publishServerCommandFactory = new PublishServerCommandFactory(this);
	}
}
