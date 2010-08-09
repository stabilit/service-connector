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
package com.stabilit.scm.common.listener;

import java.util.EventObject;

public class StatisticsEvent extends EventObject {

	private static final long serialVersionUID = -8925639053404385202L;
	
	private EventObject eventObject;
	
	private StatisticsEnum eventType;

	public StatisticsEvent(Object source, EventObject eventObject) {
		super(source);
		this.eventObject = eventObject;
	}

	public EventObject getEventObject() {
		return eventObject;
	}

	public StatisticsEnum getEventType() {
		return eventType;
	}
	
	public void setEventType(StatisticsEnum eventType) {
		this.eventType = eventType;
	}

}
