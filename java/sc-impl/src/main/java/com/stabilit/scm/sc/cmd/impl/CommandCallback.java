package com.stabilit.scm.sc.cmd.impl;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.stabilit.scm.common.net.req.netty.IdleTimeoutException;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.SynchronousCallback;

/**
 * The Class CommandCallback. CommandCallback might be used in a command if executing the command jobs needs a callback.
 * Error handling is addressed by this callback.
 */
public class CommandCallback extends SynchronousCallback {

	/** The Constant logger. */
	protected final static Logger logger = Logger.getLogger(CommandCallback.class);

	/** The Constant ERROR_STRING. */
	private static final String ERROR_STRING_TIMEOUT = "executing command timed out";
	/** The Constant ERROR_STRING_CONNECTION. */
	private static final String ERROR_STRING_CONNECTION = "broken connection";
	/** The Constant ERROR_STRING_FAIL. */
	private static final String ERROR_STRING_FAIL = "executing command failed";

	public CommandCallback() {
		super();
	}

	public CommandCallback(boolean synchronous) {
		this.synchronous = synchronous;
	}

	/** {@inheritDoc} */
	@Override
	public void callback(Exception ex) {
		SCMPMessage fault = null;
		if (ex instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPFault(SCMPError.GATEWAY_TIMEOUT, ERROR_STRING_TIMEOUT);
		} else if (ex instanceof IOException) {
			fault = new SCMPFault(SCMPError.CONNECTION_EXCEPTION, ERROR_STRING_CONNECTION);
		} else {
			fault = new SCMPFault(SCMPError.SC_ERROR, ERROR_STRING_FAIL);
		}
		super.callback(fault);
	}
}
