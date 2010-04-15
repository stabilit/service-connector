package com.stabilit.sc.common.log;

import java.io.IOException;

import com.stabilit.sc.common.factory.IFactoryable;
import com.stabilit.sc.common.io.SCMP;

public class SCMPLogger extends SimpleLogger {

	public SCMPLogger(String dir, String fileName) throws Exception {
		super(dir, fileName);
	}
	
	public void log(SCMP scmp) throws IOException {
		super.log(scmp.toString());
	}
	
	@Override
	public IFactoryable newInstance() {
		return this;
	}

}