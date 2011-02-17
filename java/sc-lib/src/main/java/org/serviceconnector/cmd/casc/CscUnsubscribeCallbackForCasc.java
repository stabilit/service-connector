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

import org.serviceconnector.net.res.IResponderCallback;
import org.serviceconnector.scmp.IRequest;
import org.serviceconnector.scmp.IResponse;
import org.serviceconnector.service.Subscription;

public class CscUnsubscribeCallbackForCasc extends ClnCommandCascCallback implements ISubscriptionCallback {

	private Subscription subscription;

	public CscUnsubscribeCallbackForCasc(IRequest request, IResponse response, IResponderCallback callback,
			Subscription subscription) {
		super(request, response, callback);
		this.subscription = subscription;
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
