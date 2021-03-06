package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
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
import com.wordline.awltech.karajan.batchmodel.ErrorHandling;
import com.wordline.awltech.karajan.batchmodel.Step;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl.Batch;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl.BatchAck;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkFailed;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsDone;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsReady;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkerRequestsWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.BatchFail;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Initialization;
import com.wordline.awltech.karajan.runtime.CustomSupervisorStrategy;


public class StepExecutionManager extends UntypedActor {

	 private final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  Step step;
	  
	
	  private HashMap<String, WorkerState> workers = new HashMap<String, WorkerState>();
	  private Queue<Object> pendingWork;
	  private Set<String> workIds = new LinkedHashSet<String>();
	  private BatchData<Object> batchresult;
	  private String implementation;
	  private BatchData<Object> currentbatch;
	  private ActorRef orchestrator;
	  private String id;
	  private static  ErrorHandling errorhandling;
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
	  public StepExecutionManager(ActorRef orchestrator,String id, int nbworker,String implementation,ErrorHandling errorhandling) {
		this.orchestrator=orchestrator;
	    this.nbworker=nbworker;
	    this.id=id;
	    this.implementation=implementation;
	    this.errorhandling=errorhandling;
	   
	   // this.strategy=strategy;
	  }
	  
	  // Manager ONE ErrorHandling
	  private static CustomSupervisorStrategy strategy = new CustomSupervisorStrategy();

	    @Override
	    public CustomSupervisorStrategy supervisorStrategy() {
	    	CustomSupervisorStrategy.errors=errorhandling.getExceptionElements();
	    	CustomSupervisorStrategy.stepexcmanager=getSelf();
	        return strategy;
	    }
  
	    @Override
		public void preStart() {
	    	 for(int i=0;i<this.nbworker;i++){
	 	    	String workerId=UUID.randomUUID().toString();
	 	    	final ActorRef worker = getContext().actorOf(
	 	    			Props.create(StepExecutor.class,getSelf(),workerId,implementation),"executor"+i );
//	 	    	final ActorRef worker = getContext().watch(getContext().actorOf(
//	 	    			Props.create(StepExecutor.class,getSelf(),workerId,implementation),"executor"+i ));
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
	    	//System.out.println(id+" Finish value "+this.finished);
	      WorkerRequestsWork msg = (WorkerRequestsWork) message;
	      String workerId = msg.workerId;
	      if (!pendingWork.isEmpty()) {
		        WorkerState state = workers.get(workerId);
		        if (state != null && state.status.isIdle()) {
		           Object item= pendingWork.remove();
		           Work work=new Work(UUID.randomUUID().toString(),item);
		          log.debug("Giving worker {} some work {}", workerId, work.job);
		          getSender().tell(work, getSelf());
		          workers.put(workerId, state.copyWithStatus(new Busy(work, Duration.Zero().fromNow())));
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
	    	 // System.out.println("FINS!!!!!!!!!"+finished);
	    	  this.finished+=1;
	    	  //TODO delete CkeckForWorkersStatus
	    	  if(this.finished==this.nbworker){
	    		 // currentbatch.setData(currentdata);
	    		 // BatchData<Object> r=new BatchData<Object>(currentbatch);
	    		 // r.setData(currentdata);
	    		  orchestrator.tell(new BatchProcessFinished(batchresult,id), getSelf());
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
	      // System.out.println("Apr�s traitement: "+result+" Manager "+id);
	       batchresult.getData().add(result);
	       // currentdata.add(result);
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
	    //  log.info("Work failed: {}", state.status.isBusy());
	      workers.put(workerId, state.copyWithStatus(Idle.instance));
	      getSelf().tell(new WorkerRequestsWork(workerId),state.ref);
	     // log.info("Work failed: {}", state.status.isBusy());
	     // this.finished++;
	     // System.out.println("Failed!!!!!!!!!"+finished);
//	      if (state != null && state.status.isBusy() && state.status.getWork().workId.equals(workId)) {
//	        log.info("Work failed: {}", state.status.getWork());
//	        workers.put(workerId, state.copyWithStatus(Idle.instance));
//	        this.finished++;
////	        pendingWork.add(state.status.getWork());
////	        notifyWorkers();
//	        //state.ref.tell(new Ack(workId), getSelf());
//	      }
	    }
	    /**
	     * Master receive Work from Orchestrator as a BatchData
	     * He convert BatchData to Queue in order to worker to pull data
	     * And he notify worker that work is available
	     */
	    else if (message instanceof Batch) {
	      Batch batch = (Batch) message;
	      BatchData<?> batchdata=(BatchData<?>)batch.data;
	      this.currentbatch=new BatchData<Object>(batch.data);
	     // batchresult.clear();
	      batchresult=new BatchData<Object>();
	      batchresult.cloneId(batchdata);
	    //  currentdata.clear();
	      this.finished=0;
	      log.debug("Accepted Batch: {}");
	     pendingWork=new LinkedList<Object>(batchdata.getData());
	     System.out.println(id+" RECU "+batch.data.getData());
	     // pendingWork=new LinkedList<Object>(batch.data.getData());
	    //  System.out.println(id+" Pending "+pendingWork);
	      getSender().tell(new BatchAck(batch.data.getId(),id), getSelf());
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
	    /**
	     * Batch fail while processing
	     */
	    else if(message instanceof BatchFail){
	    	BatchFail msg=(BatchFail)message;
	    	orchestrator.tell(new BatchFail(msg.data,msg.action,id), getSelf());
	    	
	    }
	  }
	  /**
	   * Master notify all the workers that Works are available
	   */
	  private void notifyWorkers() {
		 // System.out.println(id+" Notification "+pendingWork.size());
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
