package com.wordline.awltech.karajan.operations;

public class JobSecurityException extends BatchRuntimeException {

	/**
	 * SecurityException is thrown when an user is not authorized to run a JobOperator method, such as
	 * restart, stop, abandon, any getters, etc 
	 */
	public JobSecurityException() {
	}

	public JobSecurityException(String message) {
		super(message);
	}

	public JobSecurityException(Throwable cause) {
		super(cause);
	}

	public JobSecurityException(String message, Throwable cause) {
		super(message, cause);
	}


	private static final long serialVersionUID = 1L;

}
