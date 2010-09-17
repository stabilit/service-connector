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
package org.serviceconnector.sc.service;

import org.apache.log4j.Logger;
import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;


/**
 * @author JTraber
 */
public enum ServiceType implements IReversibleEnum<String, ServiceType> {

	SESSION_SERVICE("session"), PUBLISH_SERVICE("publish"), FILE_SERVICE("file"), UNDEFINED("undefined");

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(ServiceType.class);
	
	/** The value. */
	private String value;
	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, ServiceType> reverseMap = new ReverseEnumMap<String, ServiceType>(
			ServiceType.class);

	private ServiceType(String value) {
		this.value = value;
	}

	public static ServiceType getServiceType(String typeString) {
		ServiceType type = reverseMap.get(typeString);
		if (type == null) {
			// typeString doesn't match to a valid serviceType
			return ServiceType.UNDEFINED;
		}
		return type;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public ServiceType reverse(String typeString) {
		return ServiceType.getServiceType(typeString);
	}
}
