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

import java.io.IOException;
import java.util.List;

import com.stabilit.scm.common.cmd.factory.CommandFactory;
import com.stabilit.scm.common.conf.IResponderConfigItem;
import com.stabilit.scm.common.conf.RequesterConfig;
import com.stabilit.scm.common.conf.ResponderConfig;
import com.stabilit.scm.common.conf.ResponderConfig.ResponderConfigItem;
import com.stabilit.scm.common.ctx.IResponderContext;
import com.stabilit.scm.common.res.IResponder;
import com.stabilit.scm.srv.ServerResponder;
import com.stabilit.scm.srv.rr.cmd.factory.impl.SessionServerCommandFactory;

public class SessionServer {

	public static void main(String[] args) throws IOException {
		run();
	}

	private static void run() throws IOException {
		ResponderConfig srvConfig = new ResponderConfig();
		srvConfig.load("sc-sim.properties");
		RequesterConfig clientConfig = new RequesterConfig();
		clientConfig.load("sc-sim.properties");

		CommandFactory commandFactory = CommandFactory.getCurrentCommandFactory();
		if (commandFactory == null) {
			CommandFactory.setCurrentCommandFactory(new SessionServerCommandFactory());
		}
		List<ResponderConfigItem> respConfigList = srvConfig.getResponderConfigList();

		for (IResponderConfigItem respConfigItem : respConfigList) {
			IResponder resp = new ServerResponder();
			resp.setResponderConfig(respConfigItem);
			IResponderContext respContext = resp.getResponderContext();
			respContext.setAttribute(RequesterConfig.class.getName(), clientConfig);
			try {
				resp.create();
				resp.runAsync();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
