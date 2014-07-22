package com.wordline.awltech.karajan.orchestrator;

import akka.actor.ActorRef;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.runtime.BatchStatus;

public interface Orchestrator {
	BatchData<?> getResult();
	void run(ActorRef producer);
	int getSuccessData();
	BatchStatus getBatchStatus();
}
