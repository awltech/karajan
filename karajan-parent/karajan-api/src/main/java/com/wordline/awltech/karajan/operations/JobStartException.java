package com.wordline.awltech.karajan.operations;

public class JobStartException extends BatchRuntimeException {

	/**
	 * JobStartException is thrown when an error occurs during the JobOperator
	 * start operation.
	 */
	public JobStartException() {
	}

	public JobStartException(String message) {
		super(message);
	}

	public JobStartException(Throwable cause) {
		super(cause);
	}

	public JobStartException(String message, Throwable cause) {
		super(message, cause);
	}


	private static final long serialVersionUID = 1L;

}
