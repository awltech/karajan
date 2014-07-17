package com.wordline.awltech.karajan.orchestrator.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.wordline.awltech.karajan.api.BatchData;

public class OrchestrationMemory {
	/**
	 * the size of the memory
	 */
	private int size;
	/**
	 * 
	 */
	private List<Queue<BatchData<?> > > memory=new ArrayList<Queue<BatchData<?> > >();
	
	public OrchestrationMemory(int size) {
		this.size=size;
		for(int i=0;i<this.size;i++){
			memory.add(new LinkedList<BatchData<?>>());
		}
	}
	/**
	 * 
	 * @param i
	 * @return
	 */
	public BatchData<?> pullWork(int i){
		return memory.get(i).remove();
	}
	/**
	 * 
	 * @param i
	 */
	public void  pushWork(int i,BatchData<?> data){
		 memory.get(i).add(data);
	}
}
