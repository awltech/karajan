package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;

import scala.concurrent.duration.Deadline;
import akka.actor.ActorRef;

import com.wordline.awltech.karajan.akkamodel.ActorStep;
import com.wordline.awltech.karajan.api.BatchData;

public abstract class WorkManager {
	// protected final LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	 protected  HashMap<String, WorkerState> workers = new HashMap<String, WorkerState>();
	 protected  Set<String> workIds = new LinkedHashSet<String>();
	 protected  String id=UUID.randomUUID().toString();
	  
	  
	
	  /**
	   * Verify that all workers has finished their work
	   * @return boolean
	   */
	  protected boolean noWorkerIsBusy(){
		  for(Entry<String, WorkerState> entry : workers.entrySet()) {
				if( entry.getValue().status.isBusy()){
					return false;
				}
		   }
		  return true;
	  }
	  
	 
		
	  
	  protected static abstract class WorkerStatus {
		    protected abstract boolean isIdle();
		    protected boolean isBusy() {
		      return !isIdle();
		    };
		    protected abstract Work getWork();
		    protected abstract Deadline getDeadLine();
		  }
		
	  protected static final class Idle extends WorkerStatus {
		    protected static final Idle instance = new Idle();
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
		
	  protected static final class Busy extends WorkerStatus {
		    private final Work work;
		    private final Deadline deadline;
		
		    protected Busy(Work work, Deadline deadline) {
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
		
	  protected static final class WorkerState {
		    public final ActorRef ref;
		    public final WorkerStatus status;
		
		    protected WorkerState(ActorRef ref, WorkerStatus status) {
		      this.ref = ref;
		      this.status = status;
		    }
		
		    protected WorkerState copyWithStatus(WorkerStatus status) {
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
			public final ActorStep  stepInfo;

				public BatchProcessFinished(BatchData<?> batchdata,ActorStep  stepInfo) {
			          this.batchdata=batchdata;
			          this.stepInfo=stepInfo;
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
