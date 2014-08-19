package com.wordline.awltech.karajan.runtime;

import akka.actor.ActorRef;

import com.wordline.awltech.karajan.batchmodel.Step;

public class StepExecutionImpl {
	/**
	 * Step that will be executed
	 */
	Step step;
	/**
	 * AKKA actor that will execute the Step
	 */
	ActorRef executor;
}
