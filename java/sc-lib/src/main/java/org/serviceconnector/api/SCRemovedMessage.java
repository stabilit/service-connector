package org.serviceconnector.api;

public class SCRemovedMessage extends SCPublishMessage {
	
	/** {@inheritDoc} */
	@Override
	public boolean isManaged() {
		return true;
	}
}
