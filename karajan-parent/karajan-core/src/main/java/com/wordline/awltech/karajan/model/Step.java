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
	Step successor;
	/**
	 * Class name that contain all Step processes
	 */
	String ref;
	/**
	 * Number of thread used to lunch process
	 */
	int parallelinstances;
	/**
	 * Errors management policy
	 */
	List<ErrorHandling> errorshandler=new ArrayList<ErrorHandling>();
	
	//Constructor
	public Step(String id,Step succ, String ref, int parallelinstances) {
	   this.id=id;
	   this.successor=succ;
	   this.ref=ref;
	   this.parallelinstances=parallelinstances;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Step getSuccessor() {
		return successor;
	}

	public void setSuccessor(Step successor) {
		this.successor = successor;
	}

	public String getRef() {
		return ref;
	}

	public void setRef(String ref) {
		this.ref = ref;
	}

	public int getParallelinstances() {
		return parallelinstances;
	}

	public void setParallelinstances(int parallelinstances) {
		this.parallelinstances = parallelinstances;
	}

	public List<ErrorHandling> getErrorshandler() {
		return errorshandler;
	}

	public void setErrorshandler(List<ErrorHandling> errorshandler) {
		this.errorshandler = errorshandler;
	}
	
	
	
	
}
