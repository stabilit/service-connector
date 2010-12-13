/*
 *-----------------------------------------------------------------------------*
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
 *-----------------------------------------------------------------------------*
/*
/**
 * 
 */
package org.serviceconnector.test.sc;

import org.serviceconnector.conf.CommunicatorConfig;
import org.serviceconnector.net.req.RequesterContext;

/**
 * @author JTraber
 */
public class TestContext extends RequesterContext {

	public TestContext(CommunicatorConfig config) {
		super(config.getInterfaces().get(0), config.getPort(), config.getConnectionType(), 0);
	}

	public TestContext(CommunicatorConfig config, int maxConnections) {
		super(config.getInterfaces().get(0), config.getPort(), config.getConnectionType(), 0, maxConnections);
	}
}
