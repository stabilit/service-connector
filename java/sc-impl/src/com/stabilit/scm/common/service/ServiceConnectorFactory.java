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
package com.stabilit.scm.common.service;

import com.stabilit.scm.cln.service.IClientServiceConnector;
import com.stabilit.scm.srv.service.IPublishServiceConnector;
import com.stabilit.scm.srv.service.ISessionServiceConnector;

public class ServiceConnectorFactory {

	public static IClientServiceConnector newClientInstance(String host, int port) {
		return new ClientServiceConnector(host, port);
	}

	public static IPublishServiceConnector newPublishServerInstance(String host, int port) {
		return new PublishServerServiceConnector(host, port);
	}
	
	public static ISessionServiceConnector newSessionServerInstance(String host, int port) {
		return new SessionServerServiceConnector(host, port);
	}
}
