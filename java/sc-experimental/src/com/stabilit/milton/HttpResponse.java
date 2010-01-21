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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.Map;

import com.bradmcevoy.http.AbstractResponse;

/**
 * @author JTraber
 * 
 */
public class HttpResponse extends AbstractResponse {
	
	OutputStream out;
	
	{
		try {
			out = new FileOutputStream("tmp.txt");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Response#getHeaders()
	 */
	@Override
	public Map<String, String> getHeaders() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Response#getNonStandardHeader(java.lang.String)
	 */
	@Override
	public String getNonStandardHeader(String code) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Response#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
		return out;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Response#getStatus()
	 */
	@Override
	public Status getStatus() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.bradmcevoy.http.Response#setNonStandardHeader(java.lang.String,
	 * java.lang.String)
	 */
	@Override
	public void setNonStandardHeader(String code, String value) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.bradmcevoy.http.Response#setStatus(com.bradmcevoy.http.Response.Status
	 * )
	 */
	@Override
	public void setStatus(Status status) {
		// TODO Auto-generated method stub

	}
}
