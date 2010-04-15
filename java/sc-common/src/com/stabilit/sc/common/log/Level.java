package com.stabilit.sc.common.log;

public enum Level {
	ERROR("ERR"), EXCEPTION("EXC"), WARN("WRN"), INFO("INF"), DEBUG("DBG"), TRACE("TRC");

	private String level;
	
	private Level(String level) {
		this.level = level;
	}
	
	public String getLevel() {
		return level;
	}
}
