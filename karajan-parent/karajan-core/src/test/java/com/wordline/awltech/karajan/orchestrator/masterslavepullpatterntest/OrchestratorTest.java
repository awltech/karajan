package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import java.util.ArrayList;
import java.util.List;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

import com.wordline.awltech.karajan.orchestrator.Orchestrator;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.BatchProducer;
import com.wordline.awltech.karajan.orchestrator.model.ActorStep;
import com.wordline.awltech.karajan.runtime.BatchStatus;



public class OrchestratorTest {
	@SuppressWarnings("serial")
	public static void main(String[] args) {
	
		// instanciation of the actor model
		ActorSystem _system = ActorSystem.create("Karajan");
		// Data to be processed
		final List<Integer> data=new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			data.add(i+1);
		}
		
		String impl1="com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest.Implementation1";
		String impl2="com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest.Implementation2"; 
		
		// instanciation of some steps
		ActorStep s2=new ActorStep("step2",5, null,impl2);
		ActorStep s1=new ActorStep("step1",5, s2,impl1);
		//Model
		final List<ActorStep> model=new ArrayList<ActorStep>();
		s1.setWorkRef(0);model.add(s1);
		s2.setWorkRef(1);model.add(s2);
		  final ActorRef batchproducer =_system.actorOf(Props.create(BatchProducer.class,data.iterator(),5));
		// instanciation of the orchestration
				Orchestrator orchestrator =
						TypedActor.get(_system).typedActorOf(
						new TypedProps<OrchestratorImpl>(Orchestrator.class,
						new Creator<OrchestratorImpl>() {
						public OrchestratorImpl create() { return new OrchestratorImpl(model,batchproducer); }
						}),
						"orchestrator");	
		while(orchestrator.getBatchStatus()!=BatchStatus.COMPLETED){
			System.out.println(orchestrator.getBatchStatus());
		}
		if(orchestrator.getBatchStatus()==BatchStatus.COMPLETED){
			System.out.println("PROCESSED "+orchestrator.getStepMetrics("step1").PROCESSED);
			System.out.println("RECEIVED "+orchestrator.getStepMetrics("step1").RECEIVED);
			_system.shutdown();
		}
	
			
	
	

				
	
	}
}
