package com.wordline.awltech.karajan.model;

import java.util.ArrayList;
import java.util.List;

public class Job {
	/**
	 * Name of the job
	 */
	String id;
	/**
	 * List of steps that will be lunched by the job
	 */
	List<Step> steps=new ArrayList<Step>();
	// Constructor
	public Job(String id, List<Step> steps) {
		this.id=id;
		this.steps=steps;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<Step> getSteps() {
		return steps;
	}

	public void setSteps(List<Step> steps) {
		this.steps = steps;
	}
	
	

	
}
