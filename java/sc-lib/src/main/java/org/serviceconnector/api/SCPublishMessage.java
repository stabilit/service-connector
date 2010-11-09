package org.serviceconnector.api;

import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

public class SCPublishMessage extends SCMessage {

	private String mask;

	public SCPublishMessage() {
	}

	public SCPublishMessage(byte[] data) {
		super(data);
	}

	public SCPublishMessage(String data) {
		super(data);
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) throws SCMPValidatorException {
		ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
		this.mask = mask;
	}
}
