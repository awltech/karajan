package com.wordline.awltech.karajan.akkamodel;

import java.util.ArrayList;
import java.util.List;

import com.wordline.awltech.karajan.batchmodel.Job;
import com.wordline.awltech.karajan.batchmodel.Step;

public class CoreAPILinker {
	/**
	 * This method receive one Job and return Akka Model that can be run by the Core
	 * @param job
	 * @return List<ActorStep>
	 */
	public static List<ActorStep> generateAKKAModel(Job job) throws RuntimeException{
		List<ActorStep> akkaModel=new ArrayList<ActorStep>();
		List<Step> steps=job.getSteps();
		// we find the initial step of the model
		int initialStepIndex=findInitialStep(steps);
		Step pendingStep;
		if(initialStepIndex<0){
			throw new RuntimeException();
		}else{
			pendingStep=steps.get(initialStepIndex);
			akkaModel.add(new ActorStep(pendingStep));
		}
		//We add the initial step to AKKAModel
		int i=0;
		/** We iterate through Job steps and for each step we convert it to AKKA Step
		  and set his successor
		 */
		while(pendingStep.getNext()!=null){
			// We find the Successor index of the pending element
			int succIndex=findStepByName(pendingStep.getNext(),steps);
			if(succIndex==-1){
				throw new RuntimeException();
			}
			else{
				pendingStep=steps.get(succIndex);
				ActorStep succ=new ActorStep(steps.get(succIndex));
				akkaModel.get(i).setSuccesor(succ);
				//We set the Memory address to the index of the element for Pulling Work in the memory
				akkaModel.get(i).setWorkRef(i);
				akkaModel.add(succ);
				i++;
				
			}
			// We set the memory address for the last element
			akkaModel.get(i).setWorkRef(i);
		
			
		}
		
		return akkaModel;
		
	}
	/**
	 * Receive a list of steps and return the id of the initial step of the model
	 * @param steps
	 * @return int
	 */
	private static int findInitialStep(List<Step> steps){
		int i=0;
		while(i<steps.size()){
			boolean jump=false;
			int j=0;
			while(!jump && j<steps.size()){
				//We verify if the current element is a successor of another
				if(j!=i && steps.get(i).getId().equals(steps.get(j).getNext())){
					// we jump to another element 
					jump=true;
					i++;
				}
				
				j++;
				
				if(j!=i && j>=steps.size()){
					/** We find the element that has not a precedent, this is the initial
					element
					*/
					return i;
				}
			}
			
			i++;
		}
		return -1;
		
	}
	/**
	 * 
	 * @param steps
	 * @return int
	 */
	private static int findStepByName(String name,List<Step> steps){
		int i=0;
		while(i<steps.size()){
			if(steps.get(i).getId().equals(name)){
				return i;
			}
			i++;
		}
		return -1;
		
	}
}
