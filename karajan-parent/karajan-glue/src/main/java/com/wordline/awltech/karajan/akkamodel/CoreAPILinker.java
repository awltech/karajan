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
		Step pendingStep=steps.get(initialStepIndex);
		//We add the initial step to AKKAModel
		akkaModel.add(new ActorStep(pendingStep));
		
		int i=0;
		/** We iterate through Job steps and for each step we convert it to AKKA Step
		  and set his successor
		 */
		while(pendingStep.getNext()!=null){
			int succIndex=findStepByName(pendingStep.getNext(),steps);
			if(succIndex==-1){
				throw new RuntimeException();
			}
			else{
				pendingStep=steps.get(succIndex);
				ActorStep succ=new ActorStep(steps.get(succIndex));
				akkaModel.get(i).setSuccesor(succ);
				akkaModel.add(succ);
				i++;
			}
		
			
		}
		
		return akkaModel;
		
	}
	/**
	 * Receive a list of steps and return the id of the initial step of the model
	 * @param steps
	 * @return int
	 */
	private static int findInitialStep(List<Step> steps){
		boolean jump=false;
		int i=0;
		while(i<steps.size()){
			int j=0;
			while(!jump&&j<steps.size()){
				if(steps.get(i).getId().equals(steps.get(j).getNext())){
					// we jump to another element 
					jump=true;
					i++;
				}
				j++;
				if(j>steps.size()){
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
		}
		return -1;
		
	}
}
