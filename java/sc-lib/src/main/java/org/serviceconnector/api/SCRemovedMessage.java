package org.serviceconnector.api;

import org.serviceconnector.cache.SC_CACHING_METHOD;

public class SCRemovedMessage extends SCPublishMessage {

	public SCRemovedMessage() {
		this.setCachingMethod(SC_CACHING_METHOD.REMOVE);
	}

	/** {@inheritDoc} */
	@Override
	public boolean isManaged() {
		return true;
	}
}
