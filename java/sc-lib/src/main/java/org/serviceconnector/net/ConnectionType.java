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
package org.serviceconnector.net;

import org.serviceconnector.util.IReversibleEnum;
import org.serviceconnector.util.ReverseEnumMap;

/**
 * The Enum ConnectionType.
 */
public enum ConnectionType implements IReversibleEnum<String, ConnectionType> {

	/** The NETTY_TCP. */
	NETTY_TCP("netty.tcp"), //
	/** The NETTY_HTTP. */
	NETTY_HTTP("netty.http"), //
	/** The NETTY_WEB. */
	NETTY_WEB("netty.web"), //
	/** The NETTY_PROXY_HTTP. */
	NETTY_PROXY_HTTP("netty-proxy.http"), //
	/** The DEFAULT_CLIENT_CONNECTION_TYPE. */
	DEFAULT_CLIENT_CONNECTION_TYPE(ConnectionType.NETTY_TCP.getValue()), //
	/** The DEFAULT_SERVER_CONNECTION_TYPE. */
	DEFAULT_SERVER_CONNECTION_TYPE(ConnectionType.NETTY_TCP.getValue()), //
	/** The UNDEFINED. */
	UNDEFINED("undefined");

	/** The value. */
	private String value;

	/** The reverseMap, to get access to the enum constants by string value. */
	private static final ReverseEnumMap<String, ConnectionType> REVERSE_MAP = new ReverseEnumMap<String, ConnectionType>(
			ConnectionType.class);

	/**
	 * The Connection type.
	 * 
	 * @param value
	 *            the value
	 */
	private ConnectionType(String value) {
		this.value = value;
	}

	/**
	 * Gets the type.
	 * 
	 * @param typeString
	 *            the type string
	 * @return the type
	 */
	public static ConnectionType getType(String typeString) {
		ConnectionType type = REVERSE_MAP.get(typeString);
		if (type == null) {
			// typeString doesn't match to a valid type
			return ConnectionType.UNDEFINED;
		}
		return type;
	}

	/** {@inheritDoc} */
	@Override
	public String getValue() {
		return this.value;
	}

	/** {@inheritDoc} */
	@Override
	public ConnectionType reverse(String typeString) {
		return ConnectionType.getType(typeString);
	}
}
