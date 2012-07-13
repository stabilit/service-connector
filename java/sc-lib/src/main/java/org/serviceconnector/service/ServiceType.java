/*
 * -----------------------------------------------------------------------------*
 * *
 * Copyright © 2010 STABILIT Informatik AG, Switzerland *
 * *
 * Licensed under the Apache License, Version 2.0 (the "License"); *
 * you may not use this file except in compliance with the License. *
 * You may obtain a copy of the License at *
 * *
 * http://www.apache.org/licenses/LICENSE-2.0 *
 * *
 * Unless required by applicable law or agreed to in writing, software *
 * distributed under the License is distributed on an "AS IS" BASIS, *
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. *
 * See the License for the specific language governing permissions and *
 * limitations under the License. *
 * -----------------------------------------------------------------------------*
 * /*
 * /**
 */
package org.serviceconnector.service;

import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

/**
 * The Enum ServiceType.
 * 
 * @author JTraber
 */
public enum ServiceType implements IReversibleEnum<String, ServiceType> {

	/** The SESSION_SERVICE. */
	SESSION_SERVICE("session"), //
	/** The PUBLISH_SERVICE. */
	PUBLISH_SERVICE("publish"), //
	/** The CACHE_GUARDIAN. */
	CACHE_GUARDIAN("cacheGuardian"), //
	/** The CASCADED_SESSION_SERVICE. */
	CASCADED_SESSION_SERVICE("cascadedSession"), //
	/** The CASCADED_PUBLISH_SERVICE. */
	CASCADED_PUBLISH_SERVICE("cascadedPublish"), //
	/** The CASCADED_FILE_SERVICE. */
	CASCADED_FILE_SERVICE("cascadedFile"), //
	/** The CASCADED_CACHE_GUARDIAN. */
	CASCADED_CACHE_GUARDIAN("cascadedCacheGuardian"), //
	/** The FILE_SERVICE. */
	FILE_SERVICE("file"), //
	/** The UNDEFINED. */
	UNDEFINED("undefined");

	/** The value. */
	private String value;
	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, ServiceType> REVERSE_MAP = new ReverseEnumMap<String, ServiceType>(
			ServiceType.class);

	/**
	 * Instantiates a new service type.
	 * 
	 * @param value
	 *            the value
	 */
	private ServiceType(String value) {
		this.value = value;
	}

	/**
	 * Gets the type.
	 * 
	 * @param typeString
	 *            the type string
	 * @return the type
	 */
	public static ServiceType getType(String typeString) {
		ServiceType type = REVERSE_MAP.get(typeString);
		if (type == null) {
			// typeString doesn't match to a valid serviceType
			return ServiceType.UNDEFINED;
		}
		return type;
	}

	/** {@inheritDoc} */
	public String getValue() {
		return this.value;
	}

	/** {@inheritDoc} */
	@Override
	public ServiceType reverse(String typeString) {
		return ServiceType.getType(typeString);
	}
}
