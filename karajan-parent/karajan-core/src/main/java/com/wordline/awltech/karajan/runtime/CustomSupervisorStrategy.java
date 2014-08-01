package com.wordline.awltech.karajan.runtime;

import java.util.List;

import com.wordline.awltech.karajan.model.ErrorHandling;

import scala.PartialFunction;
import scala.collection.Iterable;
import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.ChildRestartStats;
import akka.actor.SupervisorStrategy;

public class CustomSupervisorStrategy extends SupervisorStrategy{
	
	List<ErrorHandling> errors;
	Throwable cause;
	
	 public CustomSupervisorStrategy(List<ErrorHandling> errors) {
		super();
		this.errors=errors;
	}
	 public static final SupervisorStrategy defaultStrategy() {
	        return null;
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
			Throwable cause, ChildRestartStats stats,
			Iterable<ChildRestartStats> children) {
		this.cause=cause;
		return defaultStrategy().handleFailure(context, child, cause, stats, children);
	}

}
