/*-----------------------------------------------------------------------------*
 *                                                                             *
 *       Copyright � 2010 STABILIT Informatik AG, Switzerland                  *
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

import org.serviceconnector.cmd.CommandFactory;
import org.serviceconnector.cmd.sc.ServiceConnectorCommandFactory;
import org.serviceconnector.cmd.srv.ServerCommandFactory;


/**
 * A factory for creating command objects. Unifies all commands used by publish, session server and service connector.
 * 
 * @author JTraber
 */
public class TestServerCommandFactory extends CommandFactory {
	@SuppressWarnings("unused")
	public TestServerCommandFactory() {
		ServiceConnectorCommandFactory serviceConnectorCommandFactory = new ServiceConnectorCommandFactory(this);
		ServerCommandFactory sessionServerCommandFactory = new ServerCommandFactory(this);
	}
}
