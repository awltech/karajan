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
	public synchronized BatchData<?> pullWork(int i){
		return memory.get(i).remove();
	}
	/**
	 * 
	 * @param i
	 */
	public synchronized void  pushWork(int i,BatchData<?> data){
		 memory.get(i).add(data);
	}
	/**
	 * 
	 * @param workerId
	 * @return
	 */
	public synchronized boolean isAvailableWorkFor(int workerId){
		return !memory.get(workerId).isEmpty();
	}
	public boolean isEmpty(){
		for(int i=0;i<memory.size();i++){
			if(!memory.get(i).isEmpty()){
				return false;
			}
		}
		return true;
	}

}
