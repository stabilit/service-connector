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
package com.stabilit.sc.service;

import com.stabilit.sc.client.IClient;
import com.stabilit.sc.io.SCMP;

/**
 * @author JTraber
 *
 */
public abstract class SCMPCallAdapter implements ISCMPCall {

	protected IClient client;
	protected SCMP call;
	protected SCMP result;

	/**
	 * 
	 */
	public SCMPCallAdapter() {
		super();
		this.call = new SCMP();
	}

	@Override
	public SCMP getCall() {
		return call;
	}
	
	@Override
	public SCMP getResult() {
		return result;
	}


}