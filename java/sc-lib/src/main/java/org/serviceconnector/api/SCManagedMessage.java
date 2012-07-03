package org.serviceconnector.api;

import java.util.ArrayList;
import java.util.List;

public class SCManagedMessage extends SCMessage {

	private List<SCAppendMessage> appendices;
	
	public SCManagedMessage() {
		this.appendices = new ArrayList<SCAppendMessage>();
	}

	public List<SCAppendMessage> getAppendices() {
		return appendices;
	}

	public void addAppendix(SCAppendMessage appendix) {
		this.appendices.add(appendix);
	}

	public int getNrOfAppendices() {
		return this.appendices.size();
	}
}
