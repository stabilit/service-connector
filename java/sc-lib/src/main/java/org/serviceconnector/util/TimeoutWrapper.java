package org.serviceconnector.util;

/**
 * The Class TimeoutWrapper. Wraps various Timeouts. An instance of TimeoutWrapper might be hand over to a Executer which runs the
 * Wrapper at the timeout.
 */
public class TimeoutWrapper implements Runnable {

	private ITimeout target;

	public TimeoutWrapper(ITimeout target) {
		this.target = target;
	}

	/** Time run out, call target. */
	@Override
	public void run() {
		// call target
		this.target.timeout();
	}
}
