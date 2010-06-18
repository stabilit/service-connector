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
package com.stabilit.scm.common.service;

import com.stabilit.scm.common.conf.IConstants;
import com.stabilit.scm.srv.service.IPublishServiceConnector;

/**
 * The Class PublishServerServiceConnector.
 * 
 * @author JTraber
 */
class PublishServerServiceConnector extends ServiceConnector implements IPublishServiceConnector {

	public PublishServerServiceConnector(String host, int port) {
		super(host, port, IConstants.DEFAULT_SERVER_CON, IConstants.DEFAULT_NR_OF_THREADS);
	}

	@Override
	public void publish(String mask, Object data) {
		throw new UnsupportedOperationException();
	}
}
