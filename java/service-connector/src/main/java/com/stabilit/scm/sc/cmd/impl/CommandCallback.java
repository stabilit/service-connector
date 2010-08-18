package com.stabilit.scm.sc.cmd.impl;

import com.stabilit.scm.common.net.req.netty.IdleTimeoutException;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.SynchronousCallback;

/**
 * The Class CommandCallback. CommandCallback might be used in a command if executing the command jobs needs a callback.
 * Error handling is adressed by this callback.
 */
public class CommandCallback extends SynchronousCallback {

	/** The Constant ERROR_STRING. */
	private static final String ERROR_STRING = "executing command timed out";

	/** {@inheritDoc} */
	@Override
	public void callback(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPFault(SCMPError.GATEWAY_TIMEOUT, ERROR_STRING);
		} else {
			fault = new SCMPFault(SCMPError.SC_ERROR, ERROR_STRING);
		}
		super.callback(fault);
	}
}
