package com.wordline.awltech.karajan.orchestrator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import akka.actor.ActorRef;
import akka.actor.TypedActor;
import akka.actor.TypedActor.Receiver;



import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.Master.Batch;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.Master.BatchProcessFinished;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.Worker;
import com.wordline.awltech.karajan.orchestrator.model.ActorStep;
import com.wordline.awltech.karajan.orchestrator.model.OrchestrationMemory;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.EOFBatch;

/**
 * The orchestrator is responsible to manage interactions between actors that are
 * executing Steps methods. It has in his memory the graph that represent the batch 
 * model.
 * @author Thierno Saidou BARRY
 *
 */

public class OrchestratorImpl implements Receiver, Orchestrator {
	/**
	 * The first step of the batch model
	 */
	String initialStepId;
	/**
	 * Linked list that represent the orchestration of the Batch steps.
	 */
    List<ActorStep> steps=new LinkedList<ActorStep>();
    /**
     * orchestrator memory
     */
    OrchestrationMemory memory;
    
    public OrchestratorImpl(List<ActorStep> steps,String firstStepId) {
		this.steps=steps;
		this.initialStepId=firstStepId;
	}
    
	@Override
	public void onReceive(Object message, ActorRef sender) {
		// TODO Auto-generated method stub
		/**
		 * orchestrator receive BatchData from batchproducer
		 */
		if(message instanceof Batch){
			Batch batch=(Batch)message;
			// add BatchData to work
			memory.pushWork(0, batch.data);
		}
		/**
		 * Orchestrator receive from worker that indicate that he finish
		 * to process his BatchData
		 */
		else if(message instanceof  BatchProcessFinished){
			BatchProcessFinished msg=(BatchProcessFinished)message;
			//get Succesor Work reference
			int succId=msg.stepInfo.getSuccesor().getWorkRef();
			memory.pushWork(succId, msg.batchdata);
			// send work to worker
			int workId=msg.stepInfo.getWorkRef();
			BatchData<?> data=memory.pullWork(workId);
			sender.tell(new Batch(data), getSelf());
					
		}
		/**
		 * Orchestrator receive message from batchproducer that indicate that there is no
		 * data to process
		 */
		else if(message instanceof EOFBatch){
			//TODO wait for the end of all running process
			
		}
		
	}
	
	@Override
	public BatchData<?> getResult() {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Allow to get the reference of the Orchestrator in order to be able to send
	 * a message or to send the reference to receiver
	 * @return ActorRef
	 */
	ActorRef getSelf(){
		return TypedActor.context().self();
	}
	
	

}
