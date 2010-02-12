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
package com.stabilit.sc.pool;

/**
 * @author JTraber
 * 
 */
public abstract class Connection {

	private IResponseHandler respHandler;
	private IKeepAliveHandler kAHandler;
	private ConnectionState state = ConnectionState.FREE;

	public ConnectionState getState() {
		return this.state;
	}

	public void setState(ConnectionState state) {
		this.state = state;
	}

	public IResponseHandler getRespHandler() {
		return respHandler;
	}

	public void setRespHandler(IResponseHandler respHandler) {
		this.respHandler = respHandler;
	}

	public abstract void write(Object msg);

	public boolean available() {
		return state.equals(ConnectionState.FREE) ? true : false;
	}

	public IKeepAliveHandler getKAHandler() {
		return kAHandler;
	}

	public void setKAHandler(IKeepAliveHandler handler) {
		kAHandler = handler;
	}	
}
