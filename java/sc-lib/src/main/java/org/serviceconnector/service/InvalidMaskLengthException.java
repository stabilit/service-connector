package org.serviceconnector.service;

public class InvalidMaskLengthException extends Exception {

	private static final long serialVersionUID = -2870366081862019823L;

	public InvalidMaskLengthException(String message) {
		super(message);
	}
}
