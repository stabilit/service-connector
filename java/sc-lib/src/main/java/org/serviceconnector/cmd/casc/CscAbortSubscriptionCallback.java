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
package org.serviceconnector.cmd.casc;

import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.SCMPMessage;
import org.serviceconnector.service.Subscription;

public class CscAbortSubscriptionCallback implements ISubscriptionCallback {

	private Subscription subscription;
	private IRequest request;

	public CscAbortSubscriptionCallback(IRequest request, Subscription subscription) {
		this.subscription = subscription;
		this.request = request;
	}

	/** {@inheritDoc} */
	@Override
	public void receive(SCMPMessage reply) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public void receive(Exception ex) {
		// nothing to do here
	}

	/** {@inheritDoc} */
	@Override
	public Subscription getSubscription() {
		return this.subscription;
	}

	/** {@inheritDoc} */
	@Override
	public IRequest getRequest() {
		return this.request;
	}
}
