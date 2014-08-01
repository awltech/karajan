package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import akka.actor.UntypedActor;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.operations.ReaderException;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl.Batch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.EOFBatch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Run;

public class BatchProducer<T> extends UntypedActor {
	private Iterator<T> iterator;
	private int batchCapacity;
    BatchProducer(Iterator<T> iterator,int batchCapacity){
    	this.iterator=iterator;
    	this.batchCapacity=batchCapacity;
    }
    
	@Override
	public void onReceive(Object message) throws RuntimeException {
		if(message instanceof Run || message instanceof OrchestratorImpl.BatchAck){	
			// There are no Data to extract
			if(!iterator.hasNext()){
				getSender().tell(new EOFBatch(), getSelf());
			}else{
				getSender().tell(new Batch(extractBatch(batchCapacity)), getSelf());
			}
		}
	
	}
	 private BatchData<?> extractBatch(int size) throws ReaderException{
		 List<T> data=new LinkedList<T>();
		 int numofextrateditem=0;
		 while(numofextrateditem<size && iterator.hasNext()){
				data.add(iterator.next());
				numofextrateditem+=1;
		 }	 
		 BatchData<T> batchdata=new BatchData<T>(data);
		 return batchdata;
	 }

}
