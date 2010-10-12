package org.serviceconnector.api.cln;

import java.io.InputStream;
import java.io.OutputStream;

import org.serviceconnector.api.SCService;

//TODO JOT whole class
public class SCFileService extends SCService {

	public SCFileService(String serviceName, SCContext scContext) {
		super(serviceName, scContext);
	}

	public void uploadFile(String targetFileName, InputStream inStream) {
	}

	public void downloadFile(String sourceFileName, OutputStream outStream) {
	}
}
