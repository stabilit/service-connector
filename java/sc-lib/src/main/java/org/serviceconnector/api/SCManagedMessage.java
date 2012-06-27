package org.serviceconnector.api;

import java.util.List;

public class SCManagedMessage extends SCPublishMessage {

	private List<SCAppendMessage> appendices;

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
