package org.serviceconnector.api;

public class SCAppendMessage extends SCPublishMessage {
	
	/** {@inheritDoc} */
	@Override
	public boolean isManaged() {
		return true;
	}
}
