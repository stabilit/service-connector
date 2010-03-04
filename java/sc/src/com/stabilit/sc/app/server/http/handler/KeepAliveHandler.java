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
package com.stabilit.sc.app.server.http.handler;

import org.jboss.netty.util.Timer;


/**
 * @author JTraber
 * 
 */
public class KeepAliveHandler implements IKeepAliveHandler {

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#getAllIdleTimeSeconds()
	 */
	@Override
	public int getAllIdleTimeSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#getReaderIdleTimeSeconds()
	 */
	@Override
	public int getReaderIdleTimeSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#getTimer()
	 */
	@Override
	public Timer getTimer() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#getWriterIdleTimeSeconds()
	 */
	@Override
	public int getWriterIdleTimeSeconds() {
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#setAllIdleTimeSeconds(int)
	 */
	@Override
	public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#setReaderIdleTimeSeconds(int)
	 */
	@Override
	public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#setTimer(org.jboss.netty.util.Timer)
	 */
	@Override
	public void setTimer(Timer timer) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.stabilit.sc.serviceserver.IKeepAliveHandler#setWriterIdleTimeSeconds(int)
	 */
	@Override
	public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
		// TODO Auto-generated method stub
		
	}
}
