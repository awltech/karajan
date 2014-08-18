package com.wordline.awltech.karajan.orchestrator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.AllForOneStrategy;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.actor.TypedActor;
import akka.actor.TypedActor.PreStart;
import akka.actor.TypedActor.Receiver;
import akka.actor.TypedActor.Supervisor;
import akka.japi.Function;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.model.Action;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager.BatchProcessFinished;
import com.wordline.awltech.karajan.orchestrator.model.ActorStep;
import com.wordline.awltech.karajan.orchestrator.model.OrchestrationMemory;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.BatchFail;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.BatchIsReady;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.EOFBatch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.ManagerRequestsBatch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.PullWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.PullWorkResponse;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.PushWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Run;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Started;
import com.wordline.awltech.karajan.runtime.BatchStatus;
import com.wordline.awltech.karajan.runtime.StepMetrics;

/**
 * The orchestrator is responsible to manage interactions between actors that are
 * executing Steps methods. It has in his memory the graph that represent the batch 
 * model.
 * @author Thierno Saidou BARRY
 *
 */

public class OrchestratorImpl  implements Receiver, Orchestrator, PreStart, Supervisor {

    /**
     * orchestrator memory
     */
    private ActorRef memory;
    protected  HashMap<String, ManagerState> managers = new HashMap<String, ManagerState>();
	protected  Set<String> batchDataIds = new LinkedHashSet<String>();
    private List<ActorStep> steps;
    private final ActorRef bathcproducer;
    private int startedmanager;
    private BatchStatus batchstatus;
    private Scheduler scheduler = TypedActor.context().system().scheduler();
    
    public OrchestratorImpl(List<ActorStep> steps,ActorRef b) {
		this.steps=steps;
		this.bathcproducer=b;
		memory=TypedActor.context().actorOf(Props.create(OrchestrationMemory.class,steps.size()));
		
		
	}
    // Manage ALL error handling
    private static SupervisorStrategy strategy =
    		   // new OneForOneStrategy(10, Duration.create("1 minute"),
    		    new AllForOneStrategy(10, Duration.create("1 minute"),
    		    new Function<Throwable, Directive>() {
    		    @Override
    		    public Directive apply(Throwable t) {
    			    if (t instanceof ArithmeticException) {
    			    	return SupervisorStrategy.resume() ; 
    			    } else if (t instanceof NullPointerException) {
    			    	return SupervisorStrategy.stop();
    			    } else if (t instanceof IllegalArgumentException) {
    			    	return SupervisorStrategy.stop();
    			    } else {
    			    	return SupervisorStrategy.escalate();
    			    }
    		    }
    		    });
    	     
    @Override
    public SupervisorStrategy supervisorStrategy() {
    return strategy;
    }
    
    
    
    
	@Override
	public void onReceive(Object message, ActorRef sender) {
		// TODO Auto-generated method stub
		/**
		 * orchestrator receive BatchData from batchproducer
		 */
		if(message instanceof Batch){
			//if(!batchstatus.name().equals(BatchStatus.RNNING)){
				batchstatus=BatchStatus.RUNNING;
			//}
			Batch batch=(Batch)message;
			// add BatchData to work
			//memory.pushWork(0, batch.data);
			memory.tell(new PushWork(0, batch.data), getSelf());
			sender.tell(new BatchAck(batch.data.getId()), getSelf());
			ManagerState state=managers.get(steps.get(0).getName());
			if(state.status.isIdle()&&!state.waiting){
				notifyManagers(state.stepInfo.getName());
				state.waiting=true;
			}
		}
		/**
		 * Orchestrator receive from worker that indicate that he finish
		 * to process his BatchData
		 */
		else if(message instanceof  BatchProcessFinished){
			BatchProcessFinished msg=(BatchProcessFinished)message;
			//get Successor Work reference
			 ManagerState state = managers.get(msg.managerId);
			 state.metrics.PROCESSED++;
			 System.out.println(msg.managerId+" BatchProcessFinished "+msg.batchdata.getData());
			 if (state != null && state.status.isBusy()&&
					 state.status.getBatch().data.getId().equals(msg.batchdata.getId()) ) {
			    ActorStep succInfo=state.stepInfo.getSuccesor();
					if(succInfo!=null ){
						BatchData<?> succbatch= msg.batchdata;
						memory.tell(new PushWork(succInfo.getWorkRef(), succbatch), getSelf());
						//Notify to next step worker that work is available if it is idle
						ManagerState succstate=managers.get(succInfo.getName());
						if(succstate.status.isIdle()&&!succstate.waiting){
							notifyManagers(succInfo.getName());
							succstate.waiting=true;
						}
					}else{
						//Update statistics about processed BatchData
					}
			        managers.put(msg.managerId, state.copyWithStatus(Idle.instance));
			        sender.tell(new BatchAck(msg.batchdata.getId()), getSelf());
			  } 
//			 else if (batchDataIds.contains(msg.batchdata.getId())) {
//			          // previous Ack was lost, confirm again that this is done
//			        	sender.tell(new BatchAck(msg.batchdata.getId()), getSelf());
//			   
//			  }		
		}
		/**
		 * Orchestrator receive message from batchproducer that indicate that there is no
		 * data to process
		 */
		else if(message instanceof EOFBatch){
			//verify the memory if it is empty then wait for termination of worker
			//then job is finish
			EOFBatch msg=(EOFBatch)message;
			if(msg.endofbatch && noManagerIsBusy()){
				batchstatus=BatchStatus.COMPLETED;
			}else{
				//wait 5 s and check again if work is finished
				scheduler.schedule(Duration.Zero(),Duration.create(1, "seconds"), memory, 
						new EOFBatch(),TypedActor.context().dispatcher(), getSelf());
			}
			
		}
		/**
		 * 
		 */else if(message instanceof BatchAck){
			 BatchAck msg=(BatchAck)message;
			 System.out.println("ACK from "+msg.managerId);
			 ManagerState state=managers.get(msg.managerId);
			 if(state!=null){
				 state.metrics.RECEIVED++;
			 }
		 }
		/**
		 * 
		 */
		 else if(message instanceof ManagerRequestsBatch){
			 ManagerRequestsBatch msg=(ManagerRequestsBatch)message;
			 ManagerState state=managers.get(msg.masterId);
			 if (state != null && state.status.isIdle()) {
				 state.waiting=true;
				// managers.put(msg.masterId, state.copyWithStatus(new Busy(null, Duration.Zero().fromNow())));
				// System.out.println(msg.masterId+" Request!!!!Status "+state.status.toString()+"Ref "+state.stepInfo.getWorkRef());
				 memory.tell(new PullWork(state.stepInfo.getWorkRef(), msg.masterId), getSelf()); 
			 }
			
		}
		 else if(message instanceof PullWorkResponse){
			 PullWorkResponse msg=(PullWorkResponse)message;
			 ManagerState state=managers.get(msg.manager);
			 BatchData<?> data=msg.data;
			 if(data!=null && state.waiting){
				 Batch batch=new Batch(data);
				 System.out.println(msg.manager+" Request!!!!Status "+state.status.toString()+"Data "+batch.data.getData());
				 state.ref.tell(batch, getSelf());
			     managers.put(msg.manager, state.copyWithStatus(new Busy(batch, Duration.Zero().fromNow())));
			     state.waiting=false;
			     
			 }else{
				// managers.put(msg.manager, state.copyWithStatus(Idle.instance));
				 state.waiting=false;
				 
			 }
		 }
		 /**
	     * The StepManager send a message that mean Batch processing has failed
	     */
	    else if(message instanceof BatchFail){
	    	BatchFail msg=(BatchFail)message;
	    	//TODO send a message to the ErrorReplicator for saving the batch
	    	 ManagerState state = managers.get(msg.managerId);
			 if (state != null && state.status.isBusy() ) { 
				 if(msg.action==Action.SKIP){
					 managers.put(msg.managerId, state.copyWithStatus(Idle.instance));
					System.out.println("------------->"+state.ref.path());
					 sender.tell(new BatchAck(UUID.randomUUID().toString()), getSelf());
				 }else if(msg.action==Action.RETRY){
					// TODO add the batch to the orchestrator memory in order to be reprocessed 
				 }
				 
				 
			 }
	    	
	    		
	    }
		 else if(message instanceof Started){
			 
		    	this.startedmanager+=1;
		    	if(this.startedmanager==this.steps.size()) bathcproducer.tell(new Run(),getSelf());
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
	
	  /**
	   * Orchestrator notify to manager that is at index that there is an available work
	   */
	  private void notifyManagers(String ref) {
		  ManagerState state=managers.get(ref);
	      state.ref.tell(BatchIsReady.getInstance(), getSelf());
	    
	     
	  }
	  
	  /**
	   * Verify that all workers has finished their work
	   * @return boolean
	   */
	  private boolean noManagerIsBusy(){
		  for(Entry<String, ManagerState> entry : managers.entrySet()) {
				if( entry.getValue().status.isBusy()||entry.getValue().waiting==true){
					return false;
				}
		   }
		  return true;
	  }
	
	  private static abstract class ManagerStatus {
		    protected abstract boolean isIdle();
		    private boolean isBusy() {
		      return !isIdle();
		    };
		    protected abstract Batch getBatch();
		    protected abstract Deadline getDeadLine();
		  }
		
		  private static final class Idle extends ManagerStatus {
		    private static final Idle instance = new Idle();
		    @SuppressWarnings("unused")
			public static Idle getInstance() {
		      return instance;
		    }
		
		    @Override
		    protected boolean isIdle() {
		      return true;
		    }
		
		    @Override
		    protected Batch getBatch() {
		      throw new IllegalAccessError();
		    }
		
		    @Override
		    protected Deadline getDeadLine() {
		      throw new IllegalAccessError();
		    }
		
		    @Override
		    public String toString() {
		      return "Idle";
		    }
		  }
		
		  private static final class Busy extends ManagerStatus {
		    private final Batch batch;
		    private final Deadline deadline;
		
		    private Busy(Batch batch, Deadline deadline) {
		      this.batch = batch;
		      this.deadline = deadline;
		    }
		
		    @Override
		    protected boolean isIdle() {
		      return false;
		    }
		
		    @Override
		    protected Batch getBatch() {
		      return batch;
		    }
		
		    @Override
		    protected Deadline getDeadLine() {
		      return deadline;
		    }
		
		    @Override
		    public String toString() {
		      return "Busy{" +
		        "batch=" + batch +
		        ", deadline=" + deadline +
		        '}';
		    }
		  }
		
		  private static final class ManagerState {
		    public final ActorRef ref;
		    public final ManagerStatus status;
		    public final ActorStep stepInfo;
		    public boolean waiting=false;
		    public StepMetrics metrics=new StepMetrics();

		
		    private ManagerState(ActorRef ref, ManagerStatus status,ActorStep stepinfo) {
		      this.ref = ref;
		      this.status = status;
		      this.stepInfo=stepinfo;
		    }
		    private ManagerState(ActorRef ref, ManagerStatus status,ActorStep stepinfo,StepMetrics metrics) {
			      this.ref = ref;
			      this.status = status;
			      this.stepInfo=stepinfo;
			      this.metrics=metrics;
			    }
		    private ManagerState copyWithStatus(ManagerStatus status) {
		      return new ManagerState(this.ref, status,this.stepInfo,this.metrics);
		    }
		    
		  
		    @Override
		    public boolean equals(Object o) {
		      if (this == o) return true;
		      if (o == null || getClass() != o.getClass()) return false;
		
		      ManagerState that = (ManagerState) o;
		
		      if (!ref.equals(that.ref)) return false;
		      if (!status.equals(that.status)) return false;
		
		      return true;
		    }
		
		    @Override
		    public int hashCode() {
		      int result = ref.hashCode();
		      result = 31 * result + status.hashCode();
		      return result;
		    }
		
		    @Override
		    public String toString() {
		      return "WorkerState{" +
		        "ref=" + ref +
		        ", status=" + status +
		        '}';
		    }
		  }
		  
		  public static final class Batch implements Serializable {
				/**
			 * 
			 */
			private static final long serialVersionUID = -1549048184198441840L;
				/**
				 * 
				 */
				    public final BatchData<?> data;
				
				    public Batch(BatchData<?> data) {
				      this.data=data; 
				    
				    }
				
				    @Override
				    public String toString() {
				      return "Batch{}" ;
				       
				    }
		}
		  /**
		  * 
		   * The acquittal message of the Batch
		   *
		   */
		  public static final class BatchAck implements Serializable {
		    /**
			 * 
			 */
			private static final long serialVersionUID = -2765406912858083411L;
			public final String batchId;
			public final String managerId;
		
		    public BatchAck(String batchId,String stepid) {
		      this.batchId = batchId;
		      this.managerId=stepid;
		    }
		    public BatchAck(String batchId) {
			      this.batchId = batchId;
			      this.managerId=null;
			    }
		    @Override
		    public String toString() {
		      return "Ack{" +
		        "workId='" + batchId + '\'' +
		        '}';
		    }
		  }
		@Override
		public void run(ActorRef producer) {
			producer.tell(new OrchestratorMasterProtocol.Run(), getSelf());
			
		}

		@Override
		public void preStart() {
			this.batchstatus=BatchStatus.STARTING;
			for(int i=0;i<steps.size();i++){
				ActorStep step=steps.get(i);
				ActorRef manager=TypedActor.context().actorOf(Props.create(StepExecutionManager.class,
						getSelf(),step.getName(),step.getCapacity(),step.getImplementation(),step.getErrorsHandler()));
				 managers.put(step.getName(), new ManagerState(manager,Idle.instance,step));
			}
			
		}

		
		@Override
		public StepMetrics getStepMetrics(String step) {
			ManagerState manager=managers.get(step);
			return manager.metrics;
		}


		@Override
		public BatchStatus getBatchStatus() {
			// TODO Auto-generated method stub
			return this.batchstatus;
		}
	
}
