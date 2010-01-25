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
package com.stabilit.sc.msg;



/**
 * @author JTraber
 *
 */
public class Message implements IMessage{

	private RoutingInformation routingInformation;
	private String messageBody;
	
	public Message(RoutingInformation routingInformation, String messageBody) {
		super();
		this.routingInformation = routingInformation;
		this.messageBody = messageBody;
	}

	public RoutingInformation getRoutingInformation() {
		return routingInformation;
	}

	public void setRoutingInformation(RoutingInformation routingInformation) {
		this.routingInformation = routingInformation;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}	
}
