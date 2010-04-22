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
package com.stabilit.sc.sim.cmd.factory.impl;

import com.stabilit.sc.sim.cmd.impl.AllocateSessionCommand;
import com.stabilit.sc.sim.cmd.impl.DeAllocateSessionCommand;
import com.stabilit.sc.sim.cmd.impl.SrvDataCommand;
import com.stabilit.sc.srv.cmd.ICommand;
import com.stabilit.sc.srv.cmd.factory.CommandFactory;

public class SimulationServerCommandFactory extends CommandFactory {

	public SimulationServerCommandFactory() {
		init(this);
	}
	
	public SimulationServerCommandFactory(CommandFactory commandFactory) {
       init(commandFactory);
	}
	
	public void init(CommandFactory commandFactory) {
		ICommand allocateSessionCommand = new AllocateSessionCommand();
		commandFactory.add(allocateSessionCommand.getRequestKeyName(), allocateSessionCommand);
		ICommand deAllocateSessionCommand = new DeAllocateSessionCommand();
		commandFactory.add(deAllocateSessionCommand.getRequestKeyName(), deAllocateSessionCommand);
		ICommand srvDataCommand = new SrvDataCommand();
		commandFactory.add(srvDataCommand.getRequestKeyName(), srvDataCommand);
	}
}
