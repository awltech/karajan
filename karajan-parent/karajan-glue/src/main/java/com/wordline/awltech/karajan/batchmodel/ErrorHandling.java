package com.wordline.awltech.karajan.batchmodel;

import java.util.ArrayList;
import java.util.List;

public class ErrorHandling {

	/**
	 * 
	 */
	List<ExceptionElement> exceptionElements;
	
	public ErrorHandling() {
		exceptionElements=new ArrayList<ExceptionElement>();
	}
	
	public void addExceptionElement(ExceptionElement exception){
		exceptionElements.add(exception);
	}

	public List<ExceptionElement> getExceptionElements() {
		return exceptionElements;
	}
	
	

}
