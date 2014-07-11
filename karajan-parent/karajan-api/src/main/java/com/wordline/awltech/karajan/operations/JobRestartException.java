package com.wordline.awltech.karajan.operations;

public class JobRestartException extends BatchRuntimeException {
	/**
	 * JobRestartException is thrown when an error occurs during the JobOperator
	 * restart operation.
	 */
	public JobRestartException() {
	}

	public JobRestartException(String message) {
		super(message);
	}

	public JobRestartException(Throwable cause) {
		super(cause);
	}

	public JobRestartException(String message, Throwable cause) {
		super(message, cause);
	}
	
	private static final long serialVersionUID = 1L;

}
