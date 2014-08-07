package com.wordline.awltech.karajan.runtime;

import akka.actor.ActorRef;

import com.wordline.awltech.karajan.operations.BatchRuntimeException;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager.Work;

public class ProcessorException extends BatchRuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String workerId;
	private  String workId;

	public ProcessorException(String workerId, String workId) {
		this.workerId=workerId;
		this.workId=workId;
	}

	public ProcessorException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public ProcessorException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public ProcessorException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public String getWorkerId() {
		return workerId;
	}

	public void setWorkerId(String workerId) {
		this.workerId = workerId;
	}

	public String getWorkId() {
		return workId;
	}

	public void setWorkId(String workId) {
		this.workId = workId;
	}
	
	
	

}