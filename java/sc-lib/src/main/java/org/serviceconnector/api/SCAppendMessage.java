package org.serviceconnector.api;

import org.serviceconnector.cache.SC_CACHING_METHOD;

public class SCAppendMessage extends SCPublishMessage {
	
	public SCAppendMessage() {
		this.setCachingMethod(SC_CACHING_METHOD.APPEND);
	}
	
	/** {@inheritDoc} */
	@Override
	public boolean isManaged() {
		return true;
	}
}
