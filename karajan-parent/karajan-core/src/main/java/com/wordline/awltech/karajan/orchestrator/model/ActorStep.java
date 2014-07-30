package com.wordline.awltech.karajan.orchestrator.model;


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
	
	
	public ActorStep(String name,int capacity, ActorStep succ){
		this.name=name;
		this.succesor=succ;
		this.capacity=capacity;
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
