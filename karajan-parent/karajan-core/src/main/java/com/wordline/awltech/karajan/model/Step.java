package com.wordline.awltech.karajan.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author Thierno Saidou Barry
 *
 */
public class Step {
	/**
	 * Name of the step
	 */
	String id;
	/**
	 * 
	 */
	String next;
	/**
	 * Class name that contain all Step processes
	 */
	String ref;
	/**
	 * Number of thread used to lunch process
	 */
	int parallelization;
	/**
	 * Errors management policy
	 */
	ErrorHandling errorshandler;
	
	//Constructor
	public Step(String id,String next, String ref, int parallelization) {
	   this.id=id;
	   this.next=next;
	   this.ref=ref;
	   this.parallelization=parallelization;
	}
	
    public Step(String id){
    	this.id=id;
    }
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNext() {
		return next;
	}

	public void setNext(String next) {
		this.next = next;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public int getParallelization() {
		return parallelization;
	}

	public void setParallelization(int parallelization) {
		this.parallelization = parallelization;
	}

	public ErrorHandling getErrorshandler() {
		return errorshandler;
	}

	public void setErrorshandler(ErrorHandling errorshandler) {
		this.errorshandler = errorshandler;
	}
	
	


	
	
	
	
	
}
