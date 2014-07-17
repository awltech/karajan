package com.wordline.awltech.karajan.runtime;

import com.wordline.awltech.karajan.model.Step;

import akka.actor.ActorRef;

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
