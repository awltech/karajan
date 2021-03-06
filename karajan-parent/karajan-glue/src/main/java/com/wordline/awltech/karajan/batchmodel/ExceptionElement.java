package com.wordline.awltech.karajan.batchmodel;

public class ExceptionElement {
	/**
	 * The name of the exception
	 */
	String exception;
	/**
	 * 
	 */
	ErrorStrategy stategy;
	/**
	 * 
	 */
	Action action;
	/**
	 * 
	 */
	int trynumber;
	
	public ExceptionElement() {}
	
	public ExceptionElement(String exception, ErrorStrategy strategy, Action action, int numtry){
		this.exception=exception;
		this.stategy=strategy;
		this.action=action;
		this.trynumber=numtry;
	}
	public String getException() {
		return exception;
	}
	public void setException(String exception) {
		this.exception = exception;
	}
	public ErrorStrategy getStategy() {
		return stategy;
	}
	public void setStategy(ErrorStrategy stategy) {
		this.stategy = stategy;
	}
	public Action getAction() {
		return action;
	}
	public void setAction(Action action) {
		this.action = action;
	}
	public int getTrynumber() {
		return trynumber;
	}
	public void setTrynumber(int trynumber) {
		this.trynumber = trynumber;
	}
	
}
