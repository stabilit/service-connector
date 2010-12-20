package org.serviceconnector;

import org.serviceconnector.util.SynchronousCallback;

public class TestCallback extends SynchronousCallback {
	// nothing to implement in this case - everything is done by super-class
	public TestCallback() {
		this.synchronous = true;
	}

	public TestCallback(boolean synchronous) {
		this.synchronous = synchronous;
	}
}
