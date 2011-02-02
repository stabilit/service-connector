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
package org.serviceconnector.server;

import org.apache.log4j.Logger;
import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

/**
 * @author JTraber
 */
public enum ServerType implements IReversibleEnum<String, ServerType> {

	STATEFUL_SERVER("statefulServer"), //
	FILE_SERVER("fileServer"), //
	WEB_SERVER("webServer"), //
	CASCADED_SC("cascadedSC"), //
	UNDEFINED("undefined");

	/** The Constant logger. */
	@SuppressWarnings("unused")
	private final static Logger logger = Logger.getLogger(ServerType.class);

	/** The value. */
	private String value;
	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, ServerType> reverseMap = new ReverseEnumMap<String, ServerType>(ServerType.class);

	private ServerType(String value) {
		this.value = value;
	}

	public static ServerType getType(String typeString) {
		ServerType type = reverseMap.get(typeString);
		if (type == null) {
			// typeString doesn't match to a valid type
			return ServerType.UNDEFINED;
		}
		return type;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public ServerType reverse(String typeString) {
		return ServerType.getType(typeString);
	}
}
