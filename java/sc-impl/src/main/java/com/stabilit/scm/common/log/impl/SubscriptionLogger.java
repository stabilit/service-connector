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
package com.stabilit.scm.common.log.impl;

import java.io.IOException;
import java.util.Formatter;

import com.stabilit.scm.common.conf.Constants;
import com.stabilit.scm.common.listener.ExceptionPoint;
import com.stabilit.scm.common.listener.ISubscriptionListener;
import com.stabilit.scm.common.listener.SubscriptionEvent;
import com.stabilit.scm.common.log.ILogger;
import com.stabilit.scm.common.log.ILoggerDecorator;

public class SubscriptionLogger implements ISubscriptionListener, ILoggerDecorator {

	/** The concrete logger implementation to use. */
	private ILogger logger;

	private String NO_DATA_TIMEOUT_EVENT_STR = "no data timeout by class %s - for sessionId %s";
	private String FIRE_POLL_EVENT_STR = "fire poll by class %s - for sessionId %s, polled message %s, now %s messages in queue";
	private String FIRE_ADD_EVENT_STR = "fire add by class %s - add message %s, now %s messages in queue";
	private String FIRE_REMOVE_EVENT_STR = "remove by class %s - remove message, now %s messages in queue";

	SubscriptionLogger(ILogger logger) {
		this.logger = logger.newInstance(this);
	}

	@Override
	public void noDataTimeoutEvent(SubscriptionEvent subEvent) {
		try {
			Formatter format = new Formatter();
			format
					.format(NO_DATA_TIMEOUT_EVENT_STR, subEvent.getSource().getClass().getName(), subEvent
							.getSessionId());
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	@Override
	public void firePoll(SubscriptionEvent subEvent) {
		try {
			Formatter format = new Formatter();
			format.format(FIRE_POLL_EVENT_STR, subEvent.getSource().getClass().getName(), subEvent.getSessionId(),
					subEvent.getQueueItem(), subEvent.getQueueSize());
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	@Override
	public void fireAdd(SubscriptionEvent subEvent) {
		try {
			Formatter format = new Formatter();
			format.format(FIRE_ADD_EVENT_STR, subEvent.getSource().getClass().getName(), subEvent.getQueueItem(),
					subEvent.getQueueSize());
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}
	}

	@Override
	public void fireRemove(SubscriptionEvent subEvent) {
		try {
			Formatter format = new Formatter();
			format.format(FIRE_REMOVE_EVENT_STR, subEvent.getSource().getClass().getName(), subEvent.getQueueSize());
			this.logger.log(format.toString());
			format.close();
		} catch (IOException e) {
			ExceptionPoint.getInstance().fireException(this, e);
		}

	}

	/** {@inheritDoc} */
	@Override
	public ILoggerDecorator newInstance() {
		return this;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogDir() {
		return Constants.LOG_DIR;
	}

	/** {@inheritDoc} */
	@Override
	public String getLogFileName() {
		return Constants.SUBSCRIPTION_LOG_FILE_NAME;
	}
}
