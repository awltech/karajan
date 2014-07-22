package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;

import java.io.Serializable;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.ReceiveTimeout;
import akka.actor.Terminated;
import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager.Ack;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager.Work;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsDone;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsReady;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkerRequestsWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationutils.Behavior;

public class StepExecutor extends UntypedActor {

	  public static Props props(ActorRef master, String workerId) {
	    return Props.create(StepExecutor.class, master,workerId);
	  }
	 
	
	  private final ActorRef master;
	  private LoggingAdapter log = Logging.getLogger(getContext().system(), this);
	  private final String workerId;
	  private String currentWorkId = null;
	  
    
      
	  public StepExecutor(ActorRef master, String workerId) {
	    this.master = master;
	    this.workerId=workerId;
	    

	  }

	  private String workId() {
	    if (currentWorkId != null)
	      return currentWorkId;
	    else
	      throw new IllegalStateException("Not working");
	  }
	  @Override
		public void preStart() throws Exception {
			  master.tell(new OrchestratorMasterProtocol.Started(), getSelf());
		}
 
	  @Override
	  public void postStop() {
	  
	  }

	  public void onReceive(Object message) {
	    unhandled(message);
	  }

	  private final Behavior idle = new Behavior() {
	    public void apply(Object message) {
	    	//The master sends a message that mean Work is ready
	      if (message instanceof MasterWorkerProtocol.WorkIsReady)
	    	  // the worker react by requesting for a work
	        sendToMaster(new MasterWorkerProtocol.WorkerRequestsWork(workerId));
	      // The master sends a Work to the worker
	      else if (message instanceof Work) {
	        Work work = (Work) message;
	        log.debug("Got work: {}", work.job);
	        currentWorkId = work.workId;
	       
	     // TODO Do the work and send the WorkComplete message to itself use factory for loading implemented 
	        //method
	        Integer result=(Integer)work.job;
	        result*=5;
	        getSelf().tell(new StepExecutor.WorkComplete(result), getSelf());
	        // the worker become busy
	        getContext().become(working);
	     
	       
	      }
	      else unhandled(message);
	    }
	  };

	  private final Behavior working = new Behavior() {
	    public void apply(Object message) {
	    	// Worker finish to work
	      if (message instanceof WorkComplete) {
	        Object result = ((WorkComplete) message).result;
	        log.debug("Work is complete. Result {}.", result);
	        sendToMaster(new WorkIsDone(workerId, workId(), result));
	        getContext().setReceiveTimeout(Duration.create(5, "seconds"));
	        getContext().become(waitForWorkIsDoneAck(result));
	      }
	      else if (message instanceof Work) {
	        log.info("Master told me to do work, while I'm working.");
	      }
	      else {
	        unhandled(message);
	      }
	    }
	  };
      /**
       * After the worker sends the result of his work to the master he waits for 
       * the ACK of this message.
       * @param result
       * @return
       */
	  private Behavior waitForWorkIsDoneAck(final Object result) {
	    return new Behavior() {
	      public void apply(Object message) {
	    	// The receive ACK message that mean Master receive the result of his work
	        if (message instanceof Ack && ((Ack) message).workId.equals(workId())) {
	        	// Worker ask for new work
	          sendToMaster(new WorkerRequestsWork(workerId));
	          getContext().setReceiveTimeout(Duration.Undefined());
	          getContext().become(idle);
	        }
	        // if he does not receive ACK, worker resends a new WorkIsDone message
	        else if (message instanceof ReceiveTimeout) {
	          log.debug("No ack from master, retrying (" + workerId + " -> " + workId() + ")");
	          sendToMaster(new WorkIsDone(workerId, workId(), result));
	        }
	        else {
	          unhandled(message);
	        }
	      }
	    };
	  }

	  {
	    getContext().become(idle);
	  }

	  @Override
	  public void unhandled(Object message) {
		  // Actor stop himself when he finish to work
	    if (message instanceof Terminated && ((Terminated) message).getActor().equals(self())) {
	      getContext().stop(getSelf());
	    }
	    else if (message instanceof WorkIsReady) {
	      // do nothing
	    }
	    else {
	      super.unhandled(message);
	    }
	  }

	  private void sendToMaster(Object msg) {
	    master.tell(msg, getSelf());
	  }

	  public static final class WorkComplete implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 3367249240135540826L;
		public final Object result;

	    public WorkComplete(Object result) {
	      this.result = result;
	    }

	    @Override
	    public String toString() {
	      return "WorkComplete{" +
	        "result=" + result +
	        '}';
	    }
	  }
	}