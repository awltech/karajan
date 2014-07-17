package com.wordline.awltech.karajan.orchestrator.model;

import akka.actor.ActorRef;

public class ActorStep {
	/**
	 * hold the adress that is used to access to the work in the Orchestrator memory
	 */
	private int workRef;
	private ActorRef actor;
	private ActorStep succesor;
	/**
	 * Name of the step as entered in the XML batch description file
	 */
	private String stepId;
	
	public ActorStep(String stepId,int workRef, ActorRef actor, ActorStep succ){
		this.workRef=workRef;
		this.stepId=stepId;
		this.actor=actor;
		this.succesor=succ;
	}
	
	public ActorStep(String stepId,int workRef, ActorStep succ){
		this.stepId=stepId;
		this.workRef=workRef;
		this.succesor=succ;
	}
	
	
	public int getWorkRef() {
		return workRef;
	}
    
	public String getStepId() {
		return stepId;
	}

	public void setStepId(String stepId) {
		this.stepId = stepId;
	}

	public void setWorkRef(int workRef) {
		this.workRef = workRef;
	}

	public ActorRef getActor() {
		return actor;
	}
	public void setActor(ActorRef actor) {
		this.actor = actor;
	}
	public ActorStep getSuccesor() {
		return succesor;
	}
	public void setSuccesor(ActorStep succesor) {
		this.succesor = succesor;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		  if (obj == null) return false;
		    if (obj == this) return true;
		    if (!(obj instanceof ActorStep))return false;
		    ActorStep otherActorStep = (ActorStep)obj;
		    if(otherActorStep.actor==this.actor && 
		    		otherActorStep.getWorkRef()==this.workRef &&
		    		otherActorStep.getStepId()==this.stepId)
		    	return true;
		    return false;
		   
	}
	
	
}
