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
package com.stabilit.sc.cmd;


/**
 * @author JTraber
 *
 */
public abstract class CommandAdapter implements ICommand {	

	protected ICommandValidator commandValidator;
	
	public CommandAdapter() {
		commandValidator = NullCommandValidator.newInstance();  // www.refactoring.com Introduce NULL Object
	}

	@Override
	public ICommandValidator getCommandValidator() {
		return commandValidator;
	}
		
	@Override
	public String getRequestKeyName() {
		return this.getKey().getRequestName();
	}
	
	@Override
	public String getResponseKeyName() {
		return this.getKey().getResponseName();
	}
}
