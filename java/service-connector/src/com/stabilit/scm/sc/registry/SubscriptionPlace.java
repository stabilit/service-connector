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

import java.util.TimerTask;

import com.stabilit.scm.common.scmp.IRequest;
import com.stabilit.scm.common.scmp.IResponse;
import com.stabilit.scm.common.scmp.SCMPMessage;

/**
 * @author JTraber
 */
public class SubscriptionPlace implements ISubscriptionPlace {
	private SubscriptionQueue subscriptionQueue;
	
	public SubscriptionPlace() {
		this.subscriptionQueue = new SubscriptionQueue();
	}

	@Override
	public void add(SCMPMessage message) {
		subscriptionQueue.add(message);        		
	}
	
	@Override
	public Object poll(SCMPMessage message) {
		// check if data for me is available
		String mask = null; // TODO
		String sessionId = message.getSessionId();
        if (subscriptionQueue.hasNext(sessionId,  mask) == false) {
        	// nothing to poll, maybe later
        	return null;
        }
		Object data = subscriptionQueue.poll(sessionId, mask);
		return data;
	}

	@Override
	public void listen(String sessionId, IRequest request, IResponse response) {
		this.subscriptionQueue.listen(sessionId, request, response);
	}

	@Override
	public void subscribe(String sessionId, TimerTask timerTask) {
		this.subscriptionQueue.subscribe(sessionId, timerTask);
	}

	@Override
	public void unsubscribe(String sessionId) {
		this.subscriptionQueue.unsubscribe(sessionId);
		
	}

}
