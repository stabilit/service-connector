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
package test.serviceconnector.unit;

import org.serviceconnector.common.cmd.factory.CommandFactory;
import org.serviceconnector.sc.cmd.factory.impl.ServiceConnectorCommandFactory;
import org.serviceconnector.srv.ps.cmd.factory.impl.PublishServerCommandFactory;
import org.serviceconnector.srv.rr.cmd.factory.impl.SessionServerCommandFactory;


/**
 * A factory for creating command objects. Unifies all commands used by publish, session server and service connector.
 * 
 * @author JTraber
 */
public class TestUnitServerCommandFactory extends CommandFactory {
	@SuppressWarnings("unused")
	public TestUnitServerCommandFactory() {
		ServiceConnectorCommandFactory serviceConnectorCommandFactory = new ServiceConnectorCommandFactory(this);
		SessionServerCommandFactory sessionServerCommandFactory = new SessionServerCommandFactory(this);
		PublishServerCommandFactory publishServerCommandFactory = new PublishServerCommandFactory(this);
	}
}
