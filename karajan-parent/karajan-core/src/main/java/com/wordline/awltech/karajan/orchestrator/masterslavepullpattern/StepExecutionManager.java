package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

import scala.Option;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.model.Step;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl.Batch;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl.BatchAck;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkFailed;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsDone;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsReady;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkerRequestsWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Initialization;


public class StepExecutionManager extends UntypedActor {

	 private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  Step step;
	  
	
	  private HashMap<String, WorkerState> workers = new HashMap<String, WorkerState>();
	  private Queue<Object> pendingWork;
	  private Set<String> workIds = new LinkedHashSet<String>();
	//  private BatchData<Object> batchresult=new BatchData<Object>();
	  private BatchData<Object> currentbatch;
	  List<Object> currentdata=new LinkedList<Object>();
	  private ActorRef orchestrator;
	  private String id;
	//  private final SupervisorStrategy strategy;
	  private int nbworker;
	  /**
	   * Number of worked that has already started
	   */
	  private  int startedworker=0;
	  /**
	   * the number of workers that has finished their work
	   */
	  private int finished=0;
	 
	//  public Master(ActorRef orchestrator, int nbworker, SupervisorStrategy strategy) {
	  public StepExecutionManager(ActorRef orchestrator,String id, int nbworker) {
		this.orchestrator=orchestrator;
	    this.nbworker=nbworker;
	    this.id=id;
	   
	   // this.strategy=strategy;
	  }
	
	  
	    @Override
		public void preStart() {
	    	 for(int i=0;i<this.nbworker;i++){
	 	    	String workerId=UUID.randomUUID().toString();
	 	    	final ActorRef worker = getContext().actorOf(
	 	    			Props.create(StepExecutor.class,getSelf(),workerId));
	 	    	workers.put(workerId, new WorkerState(worker,Idle.instance));
	 	    	 log.debug("Manager created: {}", workerId);
	     	}
			
		}
	  
		@Override
		public void postRestart(Throwable exeption) {
			log.debug("Master after restarting {}");
			
		}

		@Override
		public void preRestart(Throwable arg0, Option<Object> arg1) {
			log.debug("Master before restarting {}");
			
		}
		  
	
	  @Override
	  public void postStop() {
		  log.debug("Master after stopping {}");
	  }
	  
	 /**
	  * Master reacts as requested
	  */
	  @Override
	  public void onReceive(Object message) {
		  /**
		   * When the master receive a message for initialization, he creates
		   * his workers
		   */
	    if (message instanceof Initialization) {
	    	
	    }
	    /**
	     * Worker asks for work
	     */
	    else if (message instanceof WorkerRequestsWork) {
	      WorkerRequestsWork msg = (WorkerRequestsWork) message;
	      String workerId = msg.workerId;
	      if (!pendingWork.isEmpty()) {
	        WorkerState state = workers.get(workerId);
	        if (state != null && state.status.isIdle()) {
	           Object item= pendingWork.remove();
	           Work work=new Work(UUID.randomUUID().toString(),item);
	          log.debug("Giving worker {} some work {}", workerId, work.job);
	          getSender().tell(work, getSelf());
	          workers.put(workerId, state.copyWithStatus(new Busy(work, Duration.create(1,"second").fromNow())));
	        }
	        /**
	         * There is no available work. Master verify that all workers has
	         * finished their work in order to send batch finish message to 
	         * Orchestrator
	         */
	      }
	      /**
	       * We verify that we received all the notification of the worker
	       *  that mean they finished their work
	       */
	      else{
	    	  //TODO delete CkeckForWorkersStatus
	    	  if(this.finished==this.nbworker-1){
	    		  currentbatch.setData(currentdata);
	    		  orchestrator.tell(new BatchProcessFinished(currentbatch,id), getSelf());
	    	  }else{
	    		  this.finished+=1;
	    	  }  
	      }
	    }
	    /**
	     * Worker finished his task and he notify it to the master
	     */
	    else if (message instanceof WorkIsDone) {
	      WorkIsDone msg = (WorkIsDone) message;
	      String workerId = msg.workerId;
	      String workId = msg.workId;
	      WorkerState state = workers.get(workerId);
	      if (state != null && state.status.isBusy() && state.status.getWork().workId.equals(workId)) {
	        Work work = state.status.getWork();
	        Object result = msg.result;
	       // batchresult.getData().add(result);
	        currentdata.add(result);
	        log.debug("Work is done: {} => {} by worker {}", work, result, workerId);
	        workers.put(workerId, state.copyWithStatus(Idle.instance));
	        getSender().tell(new Ack(workId), getSelf());
	      } else {
	        if (workIds.contains(workId)) {
	          // previous Ack was lost, confirm again that this is done
	        	getSender().tell(new Ack(workId), getSelf());
	        }
	      }
	    }
	    /**
	     * if Worker fail while working he sends workfail message to master
	     */
	    else if (message instanceof WorkFailed) {
	      WorkFailed msg = (WorkFailed) message;
	      String workerId = msg.workerId;
	      String workId = msg.workId;
	      WorkerState state = workers.get(workerId);
	      if (state != null && state.status.isBusy() && state.status.getWork().workId.equals(workId)) {
	        log.debug("Work failed: {}", state.status.getWork());
	        workers.put(workerId, state.copyWithStatus(Idle.instance));
	        pendingWork.add(state.status.getWork());
	        notifyWorkers();
	      }
	    }
	    /**
	     * Master receive Work from Orchestrator as a BatchData
	     * He convert BatchData to Queue in order to worker to pull data
	     * And he notify worker that work is available
	     */
	    else if (message instanceof Batch) {
	      Batch batch = (Batch) message;
	    //  BatchData<?> batchdata=(BatchData<?>)batch.data;
	      currentbatch=new BatchData<Object>(batch.data);
	    //  batchresult.clear();
	      currentdata.clear();
	      this.finished=0;
	      log.debug("Accepted Batch: {}");
	     // pendingWork=new LinkedList<Object>(batchdata.getData());
	      pendingWork=new LinkedList<Object>(batch.data.getData());
	    //  getSender().tell(new BatchAck(batchdata.Id), getSelf());
	      notifyWorkers();
	      
	    }
	    /**
	     * We receive the message from the orchestrator that batch is ready
	     */
	    else if (message instanceof OrchestratorMasterProtocol.BatchIsReady || message instanceof BatchAck){
	    	    log.debug("Bathc Ready!!!!");
	 	        getSender().tell(new OrchestratorMasterProtocol.ManagerRequestsBatch(id),getSelf());
	    }
	    /**
	     * Verifying that all children are started
	     */
	    else if(message instanceof OrchestratorMasterProtocol.Started){
	    	this.startedworker+=1;
	    	if(startedworker==this.nbworker) {
	    		this.orchestrator.tell(new OrchestratorMasterProtocol.Started(), getSelf());
	    	
	    	}
	    }
	  }
	  /**
	   * Master notify all the workers that Works are available
	   */
	  private void notifyWorkers() {
	    if (!pendingWork.isEmpty()) {
	      for (WorkerState state: workers.values()) {
	        if (state.status.isIdle())
	          state.ref.tell(WorkIsReady.getInstance(), getSelf());
	      }
	    }
	  }
	 
	 
	  private static abstract class WorkerStatus {
	    protected abstract boolean isIdle();
	    private boolean isBusy() {
	      return !isIdle();
	    };
	    protected abstract Work getWork();
	    protected abstract Deadline getDeadLine();
	  }
	
	  private static final class Idle extends WorkerStatus {
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
	    protected Work getWork() {
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
	
	  private static final class Busy extends WorkerStatus {
	    private final Work work;
	    private final Deadline deadline;
	
	    private Busy(Work work, Deadline deadline) {
	      this.work = work;
	      this.deadline = deadline;
	    }
	
	    @Override
	    protected boolean isIdle() {
	      return false;
	    }
	
	    @Override
	    protected Work getWork() {
	      return work;
	    }
	
	    @Override
	    protected Deadline getDeadLine() {
	      return deadline;
	    }
	
	    @Override
	    public String toString() {
	      return "Busy{" +
	        "work=" + work +
	        ", deadline=" + deadline +
	        '}';
	    }
	  }
	
	  private static final class WorkerState {
	    public final ActorRef ref;
	    public final WorkerStatus status;
	
	    private WorkerState(ActorRef ref, WorkerStatus status) {
	      this.ref = ref;
	      this.status = status;
	    }
	
	    private WorkerState copyWithStatus(WorkerStatus status) {
	      return new WorkerState(this.ref, status);
	    }
	
	    @Override
	    public boolean equals(Object o) {
	      if (this == o) return true;
	      if (o == null || getClass() != o.getClass()) return false;
	
	      WorkerState that = (WorkerState) o;
	
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
	
	  @SuppressWarnings("unused")
	private static final Object CleanupTick = new Object() {
	    @Override
	    public String toString() {
	      return "CleanupTick";
	    }
	  };
	
	  public static final class Work implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 2915524725925205304L;
		public final String workId;
	    public final Object job;
	
	    public Work(String workId,Object job) {
	      this.workId = workId;
	      this.job = job;
	    }
	
	    @Override
	    public String toString() {
	      return "Work{" +
	        "workId='" + workId + '\'' +
	        ", job=" + job +
	        '}';
	    }
	  }
	 
	
	  public static final class WorkResult implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 5773386025740123479L;
		public final String workId;
	    public final Object result;
	
	    public WorkResult(String workId, Object result) {
	      this.workId = workId;
	      this.result = result;
	    }
	
	    @Override
	    public String toString() {
	      return "WorkResult{" +
	        "workId='" + workId + '\'' +
	        ", result=" + result +
	        '}';
	    }
	  }
	  
	 /**
	  * End of batch processing message
	 */
	  public static final class BatchProcessFinished implements Serializable {
		  
		private static final long serialVersionUID = -5126089546165365492L;
		public final BatchData<?> batchdata;
		public final String managerId;

			public BatchProcessFinished(BatchData<?> batchdata,String managerId) {
		          this.batchdata=batchdata;
		          this.managerId=managerId;
		    }
	
		    @Override
		    public String toString() {
		      return "Initialization Master{}" ;
		    }
		}
	  /**
	   * 
	   * The acquittal message
	   *
	   */
	  public static final class Ack implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -2765406912858083411L;
		public final String workId;
	
	    public Ack(String workId) {
	      this.workId = workId;
	    }
	
	    @Override
	    public String toString() {
	      return "Ack{" +
	        "workId='" + workId + '\'' +
	        '}';
	    }
	  }

	

}