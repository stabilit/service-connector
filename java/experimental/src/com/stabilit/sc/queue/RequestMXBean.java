package com.stabilit.sc.queue;

import java.util.Date;
import java.util.Map;

public interface RequestMXBean {

	public String getRequestString();

	public int getRequestNumber();

	public Date getRequestTime();

	public Map<String, String> getKeyValue();

	public String getRequestName();
}
