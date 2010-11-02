package org.serviceconnector.api;

import org.serviceconnector.Constants;
import org.serviceconnector.cmd.SCMPValidatorException;
import org.serviceconnector.scmp.SCMPError;
import org.serviceconnector.util.ValidatorUtility;

public class SCSubscribeMessage extends SCMessage {

	private String mask;
	private int noDataIntervalInSeconds;

	public SCSubscribeMessage() {
		this.noDataIntervalInSeconds = Constants.DEFAULT_NO_DATA_INTERVAL_SECONDS;
	}

	public SCSubscribeMessage(byte[] data) {
		super(data);
	}

	public SCSubscribeMessage(String data) {
		super(data);
	}

	public String getMask() {
		return mask;
	}

	public void setMask(String mask) throws SCMPValidatorException {
		ValidatorUtility.validateStringLength(1, mask, 256, SCMPError.HV_WRONG_MASK);
		if (mask.indexOf('%') != -1) {
			throw new SCMPValidatorException(SCMPError.HV_WRONG_MASK, "Percent sign not allowed in mask.");
		}
		this.mask = mask;
	}

	public int getNoDataIntervalInSeconds() {
		return noDataIntervalInSeconds;
	}

	public void setNoDataIntervalInSeconds(int noDataIntervalInSeconds) throws SCMPValidatorException {
		ValidatorUtility.validateInt(1, noDataIntervalInSeconds, 3600, SCMPError.HV_WRONG_NODATA_INTERVAL);
		this.noDataIntervalInSeconds = noDataIntervalInSeconds;
	}
}
