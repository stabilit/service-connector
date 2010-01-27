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
package com.stabilit.sc.msg;

/**
 * Message to receive/send.
 * 
 * @author JTraber
 */
public class Message implements IMessage {

	/** The routing information. */
	private String routingInformation;
	
	/** The message body. */
	private String messageBody;

	/**
	 * Instantiates a new message.
	 * 
	 * @param routingInformation the routing information
	 * @param messageBody the message body
	 */
	public Message(String routingInformation, String messageBody) {
		super();
		this.routingInformation = routingInformation;
		this.messageBody = messageBody;
	}

	/**
	 * Gets the routing information.
	 * 
	 * @return the routing information
	 */
	public String getRoutingInformation() {
		return routingInformation;
	}

	/**
	 * Sets the routing information.
	 * 
	 * @param routingInformation the new routing information
	 */
	public void setRoutingInformation(String routingInformation) {
		this.routingInformation = routingInformation;
	}

	/**
	 * Gets the message body.
	 * 
	 * @return the message body
	 */
	public String getMessageBody() {
		return messageBody;
	}

	/**
	 * Sets the message body.
	 * 
	 * @param messageBody the new message body
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
}
