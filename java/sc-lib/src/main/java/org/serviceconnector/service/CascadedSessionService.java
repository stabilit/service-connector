package org.serviceconnector.service;

import org.serviceconnector.server.CascadedSC;

public class CascadedSessionService extends Service {

	protected CascadedSC cascadedSC;

	public CascadedSessionService(String name, CascadedSC cascadedSC) {
		super(name, ServiceType.CASCADED_SESSION_SERVICE);
		this.cascadedSC = cascadedSC;
	}

	public void setCascadedSC(CascadedSC cascadedSC) {
		this.cascadedSC = cascadedSC;
	}

	public CascadedSC getCascadedSC() {
		return cascadedSC;
	}
}
