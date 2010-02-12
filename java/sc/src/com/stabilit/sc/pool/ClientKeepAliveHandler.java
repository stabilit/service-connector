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

import org.jboss.netty.util.Timer;

public class ClientKeepAliveHandler implements IKeepAliveHandler {

	private Timer timer;
	private int readerIdleTimeSeconds;
	private int writerIdleTimeSeconds;
	private int allIdleTimeSeconds;
	
	public Timer getTimer() {
		return timer;
	}
	public void setTimer(Timer timer) {
		this.timer = timer;
	}
	public int getReaderIdleTimeSeconds() {
		return readerIdleTimeSeconds;
	}
	public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
		this.readerIdleTimeSeconds = readerIdleTimeSeconds;
	}
	public int getWriterIdleTimeSeconds() {
		return writerIdleTimeSeconds;
	}
	public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
		this.writerIdleTimeSeconds = writerIdleTimeSeconds;
	}
	public int getAllIdleTimeSeconds() {
		return allIdleTimeSeconds;
	}
	public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
		this.allIdleTimeSeconds = allIdleTimeSeconds;
	}	
}
