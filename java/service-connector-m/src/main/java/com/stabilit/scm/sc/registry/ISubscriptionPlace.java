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
 * The Interface ISubscriptionPlace. Gives a client/server access to subscribe/unsubscribe/changeSubscription/add/poll
 * or listen on a subscriptionQueue.
 * 
 * @param <E>
 *            the element type
 * @author JTraber
 */
public interface ISubscriptionPlace<E> {

	/**
	 * Adds a new message to the subscription queue.
	 * 
	 * @param message
	 *            the message
	 */
	public abstract void add(E message);

	/**
	 * Poll a message from subscription place. If no message is available return is null.
	 * 
	 * @param sessionId
	 *            the session id
	 * @return the e
	 */
	public abstract E poll(String sessionId);

	/**
	 * Subscribe. Registers client as a subscriber on current subscription place.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param filterMask
	 *            the filter mask
	 * @param timerRun
	 *            the timer run
	 */
	public abstract void subscribe(String sessionId, IFilterMask<E> filterMask, IPublishTimerRun timerRun);

	/**
	 * Unsubscribe. Unsubscribe client from subscription place.
	 * 
	 * @param sessionId
	 *            the session id
	 */
	public abstract void unsubscribe(String sessionId);

	/**
	 * Listen. Current client is registered on subscription place to stay in a listening mode. Subscription place
	 * informs client at the time a new message arrives.
	 * 
	 * @param sessionId
	 *            the session id
	 * @param request
	 *            the request
	 * @param response
	 *            the response
	 */
	public abstract void listen(String sessionId, IRequest request, IResponse response);

}
