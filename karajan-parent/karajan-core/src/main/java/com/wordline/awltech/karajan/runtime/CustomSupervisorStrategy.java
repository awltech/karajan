package com.wordline.awltech.karajan.runtime;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import scala.PartialFunction;
import scala.collection.Iterable;
import scala.concurrent.duration.Duration;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.AllForOneStrategy;
import akka.actor.ChildRestartStats;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.japi.Function;

import com.wordline.awltech.karajan.model.Action;
import com.wordline.awltech.karajan.model.ErrorHandling;
import com.wordline.awltech.karajan.model.ErrorStrategy;
import com.wordline.awltech.karajan.model.ExceptionElement;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkIsDone;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol;

public class CustomSupervisorStrategy extends SupervisorStrategy{
	
	public static List<ExceptionElement> errors;
	private static Throwable cause;
	public static ActorRef stepexcmanager;
	public static   ActorRef executor;
	public static   int  currentrestrart;
	
	
	 /**
	  * 
	  * @return
	  */
	private  static ExceptionElement matchExceptionToErrorHadling(){
		if(cause!=null){
			for(ExceptionElement error: errors){
				if(cause.getClass().getSimpleName().equals(error.getException())){
					return error;
				}
			}
		}
		
		return null;		
	}
		
    public static final SupervisorStrategy defaultStrategy() {
		final ExceptionElement exceptionElement=matchExceptionToErrorHadling();
		Function<Throwable, Directive> behavior=new Function<Throwable, Directive>(){
			@Override
			public Directive apply(Throwable t) throws Exception {
				ProcessorException e=(ProcessorException)t;
				if ( exceptionElement.getStategy()==ErrorStrategy.ONE && exceptionElement.getAction()==Action.SKIPPE) {
					
				    stepexcmanager.tell(new MasterWorkerProtocol.WorkFailed(e.getWorkerId(), e.getWorkId()), ActorRef.noSender());
				   return SupervisorStrategy.resume();
			    }
				else if( exceptionElement.getStategy()==ErrorStrategy.ONE && exceptionElement.getAction()==Action.RETRY){
					
					if(currentrestrart < exceptionElement.getTrynumber()-1){
						executor.tell(new StepExecutionManager.Work(UUID.randomUUID().toString(),3), stepexcmanager);
						return SupervisorStrategy.restart();
					}else{
						stepexcmanager.tell(new MasterWorkerProtocol.WorkFailed(e.getWorkerId(), e.getWorkId()), ActorRef.noSender());
						return SupervisorStrategy.resume();
					}
			    }
				else if(exceptionElement.getStategy()==ErrorStrategy.ALL && exceptionElement.getAction()==Action.SKIPPE){
					 stepexcmanager.tell(new OrchestratorMasterProtocol.BatchFail(Action.SKIPPE), ActorRef.noSender());
					 return SupervisorStrategy.resume();
				}
				else if(exceptionElement.getStategy()==ErrorStrategy.ALL && exceptionElement.getAction()==Action.RETRY){
					 stepexcmanager.tell(new OrchestratorMasterProtocol.BatchFail(Action.RETRY), ActorRef.noSender());
				}
				return SupervisorStrategy.escalate();
			}
			
		};
		
		if(exceptionElement!=null){
			//AllForOneStrategy: The strategy is applied to all the children
			if(exceptionElement.getStategy()==ErrorStrategy.ALL){
				return new AllForOneStrategy(exceptionElement.getTrynumber(),Duration.create(5,TimeUnit.SECONDS),behavior);
			}
			//OneForOneStrategy: The strategy is applied to only the children that fail
			else if(exceptionElement.getStategy()==ErrorStrategy.ONE){
				return new OneForOneStrategy(exceptionElement.getTrynumber(), Duration.create(5,TimeUnit.SECONDS),behavior);
			}
		}
		
		// The Manager does not know how to handle this error
		return SupervisorStrategy.defaultStrategy();
			
	 }

	@Override
	public PartialFunction<Throwable, Directive> decider() {
		 return defaultDecider();
	}

	@Override
	public void handleChildTerminated(ActorContext actorContext, ActorRef actorRef,
			Iterable<ActorRef> actorRefIterable) {
		  defaultStrategy().handleChildTerminated(actorContext, actorRef,  actorRefIterable);
	}

	@Override
	public void processFailure(ActorContext actorContext, boolean b, ActorRef actorRef,
			Throwable throwable, ChildRestartStats childRestartStats,
			Iterable<ChildRestartStats> childRestartStatsIterable) {
		   defaultStrategy().processFailure(actorContext,b,actorRef,throwable,childRestartStats,childRestartStatsIterable);
		
	}
	
	@Override
	public boolean handleFailure(ActorContext context, ActorRef child,
			Throwable caus, ChildRestartStats stats,
			Iterable<ChildRestartStats> children) {
		 cause=caus;
		 executor=child;
		 currentrestrart=stats.maxNrOfRetriesCount();
		 System.out.println("Retrying!!!!!!!"+stats.maxNrOfRetriesCount());
		return defaultStrategy().handleFailure(context, child, caus, stats, children);
	}
	
	
	
	

}
