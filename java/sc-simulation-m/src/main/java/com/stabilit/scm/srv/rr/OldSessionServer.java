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
package com.stabilit.scm.srv.rr;

import java.util.List;

import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.ICommunicatorConfig;
import com.stabilit.scm.common.conf.RequesterConfigPool;
import com.stabilit.scm.common.conf.ResponderConfigPool;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.srv.SessionServerResponder;
import com.stabilit.scm.srv.rr.cmd.factory.impl.SessionServerCommandFactory;

public class OldSessionServer {

	public static void main(String[] args) throws Exception {
		run();
	}

	private static void run() throws Exception {
		ResponderConfigPool srvConfig = new ResponderConfigPool();
		srvConfig.load("session-server.properties");
		RequesterConfigPool clientConfig = new RequesterConfigPool();
		clientConfig.load("session-server.properties");

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new SessionServerCommandFactory());
		}
		List<ICommunicatorConfig> respConfigList = srvConfig.getResponderConfigList();

		for (ICommunicatorConfig respConfig : respConfigList) {
			IResponder resp = new SessionServerResponder(respConfig);
			try {
				resp.create();
				resp.runAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
