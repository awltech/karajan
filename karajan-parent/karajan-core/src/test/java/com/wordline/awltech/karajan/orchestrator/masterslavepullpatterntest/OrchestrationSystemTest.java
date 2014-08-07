package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.actor.TypedProps;
import akka.japi.Creator;

import com.wordline.awltech.karajan.model.Action;
import com.wordline.awltech.karajan.model.ErrorHandling;
import com.wordline.awltech.karajan.model.ErrorStrategy;
import com.wordline.awltech.karajan.orchestrator.Orchestrator;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.BatchProducer;
import com.wordline.awltech.karajan.orchestrator.model.ActorStep;
import com.wordline.awltech.karajan.runtime.BatchStatus;



public class OrchestrationSystemTest {
	

	private static ActorSystem _system = ActorSystem.create("Karajan");
	// Data to be processed
	private static List<Integer> data=new ArrayList<Integer>();
	private static String impl1="com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest";
	private static String impl2="com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest"; 
	
	// Somme Error Handling
	private static ErrorHandling e1=new ErrorHandling("", ErrorStrategy.ONE, Action.RETRY, 5);
	private static ErrorHandling e2=new ErrorHandling("", ErrorStrategy.ONE, Action.RETRY, 5);
	private static List<ErrorHandling> errors=new ArrayList<ErrorHandling>();
	
	// instanciation of some steps
	private static ActorStep s2=new ActorStep("step2",5, null,impl2,errors);
	private static ActorStep s1=new ActorStep("step1",5, s2,impl1,errors);
	//Model
	private static List<ActorStep> model=new ArrayList<ActorStep>();
	
	private static ActorRef batchproducer =_system.actorOf(Props.create(BatchProducer.class,data.iterator(),5));
	// instanciation of the orchestration
	@SuppressWarnings("serial")
	private static		Orchestrator orchestrator =
					TypedActor.get(_system).typedActorOf(
					new TypedProps<OrchestratorImpl>(Orchestrator.class,
					new Creator<OrchestratorImpl>() {
					public OrchestratorImpl create() { return new OrchestratorImpl(model,batchproducer); }
					}),
					"orchestrator");	
	
	public boolean batchEnds(){
		while(orchestrator.getBatchStatus()!=BatchStatus.COMPLETED){
		}
		return true;
	}
	@BeforeClass
	public static void setup() {
		errors.add(e1);
		errors.add(e2);
		s1.setWorkRef(0);model.add(s1);
		s2.setWorkRef(1);model.add(s2);
		for(int i=0;i<10;i++){
			data.add(i+1);
		}
		
	}
	
	@AfterClass
	public static void teardown() {
		_system.shutdown();
	}
	
	

	@Test
	public void BatchFinished() {
	  
	}
	

	
	

}
