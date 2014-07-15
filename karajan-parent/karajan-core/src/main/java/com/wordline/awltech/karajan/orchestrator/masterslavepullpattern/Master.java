package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;
import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import scala.Option;
import scala.concurrent.duration.Deadline;
import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.Scheduler;
import akka.actor.SupervisorStrategy;
import akka.actor.TypedActor;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.model.Step;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkFailed;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsDone;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsReady;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkerRequestsWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.CkeckForWorkersStatus;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Initialization;


public class Master extends UntypedActor {

	  LoggingAdapter log = Logging.getLogger(TypedActor.context().system(), this);
	  Step step;
	  
	
	  private HashMap<String, WorkerState> workers = new HashMap<String, WorkerState>();
	  private Queue<Object> pendingWork;
	  private Set<String> workIds = new LinkedHashSet<String>();
	  private BatchData<Object> batchresult;
	  private ActorRef orchestrator;
	  private Scheduler scheduler = getContext().system().scheduler();
	  private final SupervisorStrategy strategy;
	  private int nbworker;
	
	  public Master(ActorRef orchestrator,int nbworker,SupervisorStrategy strategy) {
		this.orchestrator=orchestrator;
	    this.nbworker=nbworker;
	    this.strategy=strategy;
	  }
	
	  
	    @Override
		public void preStart() {
			log.debug("Master is starting {}");
			
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
	    	for(int i=0;i<this.nbworker;i++){
		    	final ActorRef worker = getContext().actorOf(
		    			Props.create(Work.class), "worker"+i);
		    	String workerId=UUID.randomUUID().toString();
		    	workers.put(workerId, new WorkerState(worker,Idle.instance));
		    	 log.debug("Worker created: {}", workerId);
	    	}
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
	      }else{
	    	  //TODO Verify is all the workers has finished their works
	    	  getSelf().tell(new CkeckForWorkersStatus(), getSelf());	  
	      }
	    }
	    /**
	     * 
	     */
	    else if(message instanceof CkeckForWorkersStatus ){
	    	if(noWorkerIsBusy()){
	    		// There is no available work and all workers are idle
	    		// Send batch processFinisg msg to orchestrator
	    		orchestrator.tell(new BathcProcessFinished(batchresult), getSelf());
	    	}else{
	    		// Master will wait 5s and check again if workers is busy or not
	    		scheduler.scheduleOnce(Duration.create(5, TimeUnit.SECONDS),getSelf(),
		    			new CkeckForWorkersStatus() ,getContext().dispatcher(), getSelf());
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
	        batchresult.getData().add(result);
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
	    else if (message instanceof Work) {
	      Work work = (Work) message;
	      BatchData<?> batchdata=(BatchData<?>)work.job;
	      batchresult.clear();
	      if (workIds.contains(work.workId)) {
	    	  getSender().tell(new Ack(work.workId), getSelf());
	      } else {
	        log.debug("Accepted work: {}", work);
	        pendingWork=new LinkedList<Object>(batchdata.getData());
	        workIds.add(work.workId);
	        getSender().tell(new Ack(work.workId), getSelf());
	        notifyWorkers();
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
	  /**
	   * Verify that all workers has finished their work
	   * @return boolean
	   */
	  private boolean noWorkerIsBusy(){
		  for(Entry<String, WorkerState> entry : workers.entrySet()) {
				if( entry.getValue().status.isBusy()){
					return false;
				}
		   }
		  return true;
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
	  public static final class BathcProcessFinished implements Serializable {
		  
		private static final long serialVersionUID = -5126089546165365492L;
		public final BatchData<?> batchdata;

			public BathcProcessFinished(BatchData<?> batchdata) {
		          this.batchdata=batchdata;
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