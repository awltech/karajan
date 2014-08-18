package com.wordline.awltech.karajan.orchestrator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import akka.actor.UntypedActor;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.EOFBatch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.PullWork;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.PullWorkResponse;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.PushWork;

public class OrchestrationMemory extends UntypedActor{
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
	public  BatchData<?> pullWork(int i){
		java.util.Iterator<BatchData<?>> it= memory.get(i).iterator();
		System.out.println("===========Before PULL Memory State "+i+" =================== SIZE"+ memory.get(i).size());
		while(it.hasNext()){
			System.out.println(it.next().getData());
		}
		if(!memory.get(i).isEmpty() && memory.get(i).element().getData().size()!=0){
			System.out.println("PULL RESULT"+i+" ----------->: "+memory.get(i).element().getData());
			return memory.get(i).remove();
		}
		return null;
	}
	/**
	 * 
	 * @param i
	 */
	public void  pushWork(int i,BatchData<?> data){
		 System.out.println("===========>>>>> DATA BEFORE INSERT"+ data.getData());
		 memory.get(i).add(data);
		// java.util.Iterator<BatchData<?>> it= memory.get(i).iterator();
		 System.out.println("===========AFTER PUSH Memory State "+i+" ===================SIZE: "+ memory.get(i).size());
			for(BatchData<?> d:memory.get(i)){
				//System.out.println(System.identityHashCode(d.getData()));
				System.out.println(d.getData());
				
				
			}
	}
	/**
	 * 
	 * @param i
	 * @return
	 */
	public synchronized boolean isAvailableWorkFor(int i){
		return (!memory.get(i).isEmpty());
	}
	public boolean isEmpty(){
		for(int i=0;i<memory.size();i++){
			if(!memory.get(i).isEmpty()){
				return false;
			}
		}
		return true;
	}
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof PushWork){
			PushWork msg=(PushWork)message;
			pushWork(msg.id, msg.data);
		}else if(message instanceof PullWork){
			PullWork msg=(PullWork)message;
			//BatchData<?> data=pullWork(msg.id);
			getSender().tell(new PullWorkResponse(msg.id, pullWork(msg.id), msg.manager), getSelf());
		}else if(message instanceof EOFBatch){
			getSender().tell(new EOFBatch(isEmpty()), getSelf());
		}
	}

}
