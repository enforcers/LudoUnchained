package com.appspot.ludounchained.exception;

public class InvalidLoginException extends RemoteException {
	private static final long serialVersionUID = 2797845062869962028L;
	private static final int CODE = 10;
	
	public InvalidLoginException(String message) {
		super(CODE, message);
	}
	
}
