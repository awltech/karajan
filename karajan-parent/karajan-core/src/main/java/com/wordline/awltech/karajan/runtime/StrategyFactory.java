package com.wordline.awltech.karajan.runtime;

import java.util.List;
import java.util.concurrent.TimeUnit;

import scala.concurrent.duration.Duration;
import akka.actor.AllForOneStrategy;
import akka.actor.OneForOneStrategy;
import akka.actor.SupervisorStrategy;
import akka.actor.SupervisorStrategy.Directive;
import akka.japi.Function;

import com.wordline.awltech.karajan.model.Action;
import com.wordline.awltech.karajan.model.ErrorHandling;
import com.wordline.awltech.karajan.model.ErrorStrategy;

public class StrategyFactory {
	public static SupervisorStrategy convert(final ErrorHandling errorHandling){
		SupervisorStrategy strategy=null;
		Function<Throwable, Directive> behavior=new Function<Throwable, Directive>(){
			
			@Override
			public Directive apply(Throwable t) throws Exception {
					if(t.getClass().getName().equals(errorHandling.getException())){
						if ( errorHandling.getAction()==Action.SKIPPE) {	
	    			    	return SupervisorStrategy.resume() ; 
	    			    }
						else if(errorHandling.getAction()==Action.RETRY){
	    			    	return SupervisorStrategy.restart();
	    			    }
					}
				return null;
			}
			
		};
		//AllForOneStrategy: The strategy is applied to all the children
		if(errorHandling.getStategy()==ErrorStrategy.ALL){
			strategy=new AllForOneStrategy(errorHandling.getTrynumber(),Duration.create(5,TimeUnit.SECONDS),behavior);
		}
		//OneForOneStrategy: The strategy is applied to only the children that fail
		else if(errorHandling.getStategy()==ErrorStrategy.ONE){
			strategy=new OneForOneStrategy(errorHandling.getTrynumber(), Duration.create(5,TimeUnit.SECONDS),behavior);
		}
		return strategy;
		
	}
}
