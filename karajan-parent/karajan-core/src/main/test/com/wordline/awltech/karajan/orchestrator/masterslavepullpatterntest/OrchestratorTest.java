package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import java.util.ArrayList;
import java.util.LinkedList;
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
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.Master;
import com.wordline.awltech.karajan.orchestrator.model.ActorStep;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;



public class OrchestratorTest {
	@SuppressWarnings("serial")
	public static void main(String[] args) {
	
		// instanciation of the actor model
		ActorSystem _system = ActorSystem.create("Karajan");
		// Data to be processed
		List<Integer> data=new ArrayList<Integer>();
		for(int i=0;i<10;i++){
			data.add(i+1);
		}
		// instanciation of some steps
		ActorStep s2=new ActorStep("step2",1, null);
		final ActorStep s1=new ActorStep("step1",2, s2);
		
		ActorRef actor1 =_system.actorOf(Props.create(Master.class,s1,5), s1.getStepId());
		ActorRef actor2 =_system.actorOf(Props.create(Master.class,s2,5), s2.getStepId());
		s1.setActor(actor1);
		s2.setActor(actor2);
		 
		// The model
		final List<ActorStep> model=new LinkedList<ActorStep>();
		model.add(s1);model.add(s2);
        // instanciation of the orchestration
		Orchestrator orchestrator =
				TypedActor.get(_system).typedActorOf(
				new TypedProps<OrchestratorImpl>(Orchestrator.class,
				new Creator<OrchestratorImpl>() {
				public OrchestratorImpl create() { return new OrchestratorImpl(model,s1.getStepId()); }
				}),
				"orchestrator");
		// getting orchestrator reference
				ActorRef orchestratorRef = TypedActor.get(_system)
						.getActorRefFor(orchestrator);
		// instanciation of the bathc producer
		ActorRef batchproducer =_system.actorOf(Props.create(BatchProducer.class,data.iterator(),2));
		// activating batchproducer
		batchproducer.tell(new OrchestratorMasterProtocol.Run(), orchestratorRef);
		

	
				
	
	

				
	
	}
}
