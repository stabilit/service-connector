package com.stabilit.scm.common.log;

public interface IPerformanceLogger {

	public abstract void begin(String source, String methodName);

	public abstract void end(String source, String methodName);
}
