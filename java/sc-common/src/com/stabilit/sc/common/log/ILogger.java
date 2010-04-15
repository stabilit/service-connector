package com.stabilit.sc.common.log;

import java.io.IOException;

import com.stabilit.sc.common.factory.IFactoryable;

public interface ILogger extends IFactoryable {

	public void log(Object obj) throws IOException;

	public void log(String msg) throws IOException;

	public void log(Throwable t) throws IOException;

	public void log(Level level, String msg) throws IOException;
}
