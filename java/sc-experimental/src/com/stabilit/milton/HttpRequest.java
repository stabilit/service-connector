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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.bradmcevoy.http.AbstractRequest;
import com.bradmcevoy.http.Auth;
import com.bradmcevoy.http.FileItem;
import com.bradmcevoy.http.RequestParseException;

/**
 * @author JTraber
 *
 */
public class HttpRequest extends AbstractRequest {

	private InputStream in;
	private static HashMap<String,String> headers = new HashMap<String,String>();
	
	{
		headers.put(Header.HOST.code, "localhost");
		headers.put(Header.RANGE.code, "null");
		headers.put(Header.IF.code, "null");
		try {
			in = new FileInputStream("tmpIn.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.AbstractRequest#getRequestHeader(com.bradmcevoy.http.Request.Header)
	 */
	@Override
	public String getRequestHeader(Header header) {
		return headers.get(header.code);
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#getAbsoluteUrl()
	 */
	@Override
	public String getAbsoluteUrl() {
		return "http://localhost/webdav/SocketSniff.cfg";
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#getAuthorization()
	 */
	@Override
	public Auth getAuthorization() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#getFromAddress()
	 */
	@Override
	public String getFromAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() {
		return headers;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#getInputStream()
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		return in;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#getMethod()
	 */
	@Override
	public Method getMethod() {
		return Method.GET;
	}

	/* (non-Javadoc)
	 * @see com.bradmcevoy.http.Request#parseRequestParameters(java.util.Map, java.util.Map)
	 */
	@Override
	public void parseRequestParameters(Map<String, String> params,
			Map<String, FileItem> files) throws RequestParseException {
		// TODO Auto-generated method stub

	}

}
