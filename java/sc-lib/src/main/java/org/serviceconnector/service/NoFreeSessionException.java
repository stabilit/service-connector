package org.serviceconnector.service;

import org.serviceconnector.scmp.HasFaultResponseException;
import org.serviceconnector.scmp.SCMPError;

public class NoFreeSessionException extends HasFaultResponseException {

	private static final long serialVersionUID = -6743108698413624462L;

	public NoFreeSessionException(SCMPError error, String additionalInfo) {
		super(error, additionalInfo);
	}
}
