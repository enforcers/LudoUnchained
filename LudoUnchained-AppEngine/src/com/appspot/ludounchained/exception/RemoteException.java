package com.appspot.ludounchained.exception;

public class RemoteException extends Exception {
	private static final long serialVersionUID = -5169069860465251270L;
	private int errorCode;
	
	public RemoteException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return errorCode;
	}
	
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

}
