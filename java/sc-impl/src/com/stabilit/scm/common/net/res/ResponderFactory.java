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
package com.stabilit.scm.common.net.res;

import com.stabilit.scm.common.conf.IResponderConfigItem;
import com.stabilit.scm.common.factory.Factory;
import com.stabilit.scm.common.factory.IFactoryable;
import com.stabilit.scm.common.res.IResponder;

/**
 * A factory for creating responder objects.
 */
public class ResponderFactory extends Factory {

	/**
	 * Instantiates a new ResponderFactory.
	 */
	public ResponderFactory() {
	}

	/**
	 * New instance.
	 * 
	 * @param responderConfig
	 *            the responder configuration
	 * 
	 * @return the responder
	 */
	public IResponder newInstance(IResponderConfigItem respConfig) {
		IFactoryable factoryInstance = this.newInstance();
		IResponder responder = (IResponder) factoryInstance;
		responder.setResponderConfig(respConfig);
		return responder;
	}
}
