package com.wordline.awltech.karajan.akkamodel;

import com.wordline.awltech.karajan.batchmodel.ErrorHandling;





public class ActorStep {
	/**
	 * hold the adress that is used to access to the work in the Orchestrator memory
	 */
	private int workRef;
	/**
	 * The successor of the step regarding the graph of the batch
	 */
	private ActorStep succesor;
	/**
	 * The number of the worker that will run this step
	 */
	private int capacity;
	/**
	 * Name of the step as entered in the XML batch description file
	 */
	private String name;
	
	private String implementation;
	/**
	 * 
	 */
	private ErrorHandling errorsHandler;
	/**
	 * 
	 * @param name
	 * @param capacity
	 * @param succ
	 * @param implementation
	 */
	
	public ActorStep(String name,int capacity, ActorStep succ, String implementation,ErrorHandling handler){
		this.name=name;
		this.succesor=succ;
		this.capacity=capacity;
		this.implementation=implementation;
		this.errorsHandler=handler;
	}
	
	
	public int getWorkRef() {
		return workRef;
	}
	public void setWorkRef(int workRef) {
		this.workRef = workRef;
	}
	
	

	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public ActorStep getSuccesor() {
		return succesor;
	}
	public void setSuccesor(ActorStep succesor) {
		this.succesor = succesor;
	}
	
	public int getCapacity() {
		return capacity;
	}


	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}


	public String getImplementation() {
		return implementation;
	}


	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public ErrorHandling getErrorsHandler() {
		return errorsHandler;
	}


	public void setErrorsHandler(ErrorHandling errorsHandler) {
		this.errorsHandler = errorsHandler;
	}


	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		  if (obj == null) return false;
		    if (obj == this) return true;
		    if (!(obj instanceof ActorStep))return false;
		    ActorStep otherActorStep = (ActorStep)obj;
		    if(	otherActorStep.getWorkRef()==this.workRef &&
		    		otherActorStep.getName()==this.name)
		    	return true;
		    return false;
		   
	}
	
	
}
