package com.wordline.awltech.karajan.orchestrator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.TypedActor;
import akka.actor.TypedActor.PreStart;
import akka.actor.TypedActor.Receiver;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager.BatchProcessFinished;
import com.wordline.awltech.karajan.orchestrator.model.ActorStep;
import com.wordline.awltech.karajan.orchestrator.model.OrchestrationMemory;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.BatchIsReady;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.EOFBatch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.ManagerRequestsBatch;
import com.wordline.awltech.karajan.runtime.BatchStatus;

/**
 * The orchestrator is responsible to manage interactions between actors that are
 * executing Steps methods. It has in his memory the graph that represent the batch 
 * model.
 * @author Thierno Saidou BARRY
 *
 */

public class OrchestratorImpl  implements Receiver, Orchestrator, PreStart {

    /**
     * orchestrator memory
     */
    private OrchestrationMemory memory;
    protected  HashMap<String, ManagerState> managers = new HashMap<String, ManagerState>();
	protected  Set<String> batchDataIds = new LinkedHashSet<String>();
    private List<ActorStep> steps;
    private final ActorRef bathcproducer;
    private int succesbatch;
    private int startedmanager;
    BatchStatus batchstatus;
    public OrchestratorImpl(List<ActorStep> steps,ActorRef b) {
		this.steps=steps;
		this.bathcproducer=b;
		succesbatch=0;
		memory=new OrchestrationMemory(steps.size());
		
		
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
			sender.tell(new BatchAck(batch.data.getId()), getSelf());
			notifyManagers(steps.get(0).getName());
		}
		/**
		 * Orchestrator receive from worker that indicate that he finish
		 * to process his BatchData
		 */
		else if(message instanceof  BatchProcessFinished){
			BatchProcessFinished msg=(BatchProcessFinished)message;
			//get Successor Work reference
			 ManagerState state = managers.get(msg.managerId);
			 if (state != null && state.status.isBusy()&& 
					 state.status.getBatch().data.getId().equals(msg.batchdata.getId()) ) {
				//
				ActorStep succInfo=state.stepInfo.getSuccesor();
					if(succInfo!=null){
						memory.pushWork(succInfo.getWorkRef(), msg.batchdata);
						//Notify to next step worker that work is available
						notifyManagers(succInfo.getName());
					}else{
						//Update statistics about processed BatchData
						this.succesbatch++;
					}
			        managers.put(msg.managerId, state.copyWithStatus(Idle.instance));
			        sender.tell(new BatchAck(msg.batchdata.getId()), getSelf());
			  } else if (batchDataIds.contains(msg.batchdata.getId())) {
			          // previous Ack was lost, confirm again that this is done
			        	sender.tell(new BatchAck(msg.batchdata.getId()), getSelf());
			   
			  }		
		}
		/**
		 * Orchestrator receive message from batchproducer that indicate that there is no
		 * data to process
		 */
		else if(message instanceof EOFBatch){
			//verify the memory if it is empty then wait for termination of worker
			//then job is finish
			//this.eofbatch=true;
			
			if(memory.isEmpty() && noManagerIsBusy()){
				batchstatus=BatchStatus.COMPLETED;
			}else{
				//wait 5 s and check again if work is finished
			}
			
		}
		/**
		 * 
		 */else if(message instanceof BatchAck){
			 
		 }
		/**
		 * 
		 */else if(message instanceof ManagerRequestsBatch){
			 ManagerRequestsBatch msg=(ManagerRequestsBatch)message;
			 ManagerState state=managers.get(msg.masterId);
			 if (state != null && // state.status.isIdle()&&
					 (memory.isAvailableWorkFor(state.stepInfo.getWorkRef()))) {
				 BatchData<?> data=memory.pullWork(state.stepInfo.getWorkRef());
				 Batch batch=new Batch(data);
				 sender.tell(batch, getSelf());
			     managers.put(msg.masterId, state.copyWithStatus(new Busy(batch, Duration.create(1,"second").fromNow())));
				 
			 }else{
				 System.out.println("Fin de ^^^^^^"+msg.masterId);
			 }
			
		}
		 else if(message instanceof OrchestratorMasterProtocol.Started){
			 
		    	this.startedmanager+=1;
		    	if(this.startedmanager==this.steps.size()) bathcproducer.tell(new OrchestratorMasterProtocol.Run(),getSelf());
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
	      if ( memory.isAvailableWorkFor(state.stepInfo.getWorkRef())){
	          state.ref.tell(BatchIsReady.getInstance(), getSelf());
	      }
	     
	  }
	  
	  /**
	   * Verify that all workers has finished their work
	   * @return boolean
	   */
	  private boolean noManagerIsBusy(){
		  for(Entry<String, ManagerState> entry : managers.entrySet()) {
				if( entry.getValue().status.isBusy()){
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
		
		    private ManagerState(ActorRef ref, ManagerStatus status,ActorStep stepinfo) {
		      this.ref = ref;
		      this.status = status;
		      this.stepInfo=stepinfo;
		    }
		
		    private ManagerState copyWithStatus(ManagerStatus status) {
		      return new ManagerState(this.ref, status,this.stepInfo);
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
				private static final long serialVersionUID = 3925833229453951L;
			
					
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
		
		    public BatchAck(String batchId) {
		      this.batchId = batchId;
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
			for(int i=0;i<steps.size();i++){
				ActorStep step=steps.get(i);
				ActorRef manager=TypedActor.context().actorOf(Props.create(StepExecutionManager.class,
						getSelf(),step.getName(),step.getCapacity()));
				 managers.put(step.getName(), new ManagerState(manager,Idle.instance,step));
			}
			
		}

		@Override
		public int getSuccessData() {
			// TODO Auto-generated method stub
			return this.succesbatch;
		}

		@Override
		public BatchStatus getBatchStatus() {
			// TODO Auto-generated method stub
			return null;
		}
	
}
