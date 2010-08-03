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
package com.stabilit.scm.sc.registry;

import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.service.IFilterMask;
import com.stabilit.scm.sc.service.IPublishTimerRun;

/**
 * The Class SubscriptionPlace. SubscriptionPlace gives access to a subscriptionQueue. Encapsulates subscription queue
 * construct.
 * 
 * @param <E>
 *            the element type to handle in queue
 * @author JTraber
 */
public class SubscriptionPlace<E> implements ISubscriptionPlace<E> {

	/** The subscription queue for this subscription place. */
	private SubscriptionQueue<E> subscriptionQueue;

	public SubscriptionPlace() {
		this.subscriptionQueue = new SubscriptionQueue<E>();
	}

	/** {@inheritDoc} */
	@Override
	public void add(E message) {
		subscriptionQueue.add(message);
	}

	/** {@inheritDoc} */
	@Override
	public E poll(String sessionId) {
		// check if data is available
		if (subscriptionQueue.hasNext(sessionId) == false) {
			// nothing to poll at this time
			return null;
		}
		E data = subscriptionQueue.poll(sessionId);
		return data;
	}

	/** {@inheritDoc} */
	@Override
	public void listen(String sessionId, IRequest request, IResponse response) {
		this.subscriptionQueue.listen(sessionId, request, response);
	}

	/** {@inheritDoc} */
	@Override
	public void subscribe(String sessionId, IFilterMask<E> filterMask, IPublishTimerRun timerRun) {
		this.subscriptionQueue.subscribe(sessionId, filterMask, timerRun);
	}

	/** {@inheritDoc} */
	@Override
	public void unsubscribe(String sessionId) {
		this.subscriptionQueue.unsubscribe(sessionId);
	}
}
