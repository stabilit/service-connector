package com.stabilit.scm.common.log;

import org.apache.log4j.Logger;

public interface IExceptionLogger {

	public abstract void logDebugException(Logger logger, String source, Throwable throwable);

	public abstract void logErrorException(Logger logger, String source, Throwable throwable);
}
