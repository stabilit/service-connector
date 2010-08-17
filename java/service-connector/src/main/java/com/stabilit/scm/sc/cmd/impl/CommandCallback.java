package com.stabilit.scm.sc.cmd.impl;

import com.stabilit.scm.common.net.req.netty.IdleTimeoutException;
import com.stabilit.scm.common.scmp.SCMPError;
import com.stabilit.scm.common.scmp.SCMPFault;
import com.stabilit.scm.common.scmp.SCMPMessage;
import com.stabilit.scm.common.util.SynchronousCallback;

public class CommandCallback extends SynchronousCallback {

	private static final String ERROR_STRING = "executing command timed out";

	@Override
	public void callback(Throwable th) {
		SCMPMessage fault = null;
		if (th instanceof IdleTimeoutException) {
			// operation timeout handling
			fault = new SCMPFault(SCMPError.GATEWAY_TIMEOUT, ERROR_STRING);
		} else {
			fault = new SCMPFault(SCMPError.SC_ERROR, ERROR_STRING);
		}
		super.callback(fault);
	}
}
