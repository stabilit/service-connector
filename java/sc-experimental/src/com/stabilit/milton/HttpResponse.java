/*
 *-----------------------------------------------------------------------------*
 *                            Copyright © 2010 by                              *
 *                    STABILIT Informatik AG, Switzerland                      *
 *                            ALL RIGHTS RESERVED                              *
 *                                                                             *
 * Valid license from STABILIT is required for possession, use or copying.     *
 * This software or any other copies thereof may not be provided or otherwise  *
 * made available to any other person. No title to and ownership of the        *
 * software is hereby transferred. The information in this software is subject *
 * to change without notice and should not be construed as a commitment by     *
 * STABILIT Informatik AG.                                                     *
 *                                                                             *
 * All referenced products are trademarks of their respective owners.          *
 *-----------------------------------------------------------------------------*
 */
/**
 * 
 */
package com.stabilit.milton;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.bradmcevoy.http.AbstractResponse;

/**
 * @author JTraber
 * 
 */
public class HttpResponse extends AbstractResponse {
	
	private OutputStream out;
	private Map<String, String> headers;
	private Status status;
		
	public HttpResponse(OutputStream out) {
		super();
		this.out = out;
		this.headers = new HashMap<String, String>();
	}

	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	@Override
	public String getNonStandardHeader(String code) {
		return null;
	}

	@Override
	public OutputStream getOutputStream() {
		return out;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	@Override
	public void setNonStandardHeader(String code, String value) {
	}
	
	@Override
	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void setAuthenticateHeader(List<String> arg0) {
	}
}
