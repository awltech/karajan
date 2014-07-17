package com.wordline.awltech.karajan.orchestrator.masterslavepullpattern;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import akka.actor.UntypedActor;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.Master.Batch;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.*;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.OrchestratorMasterProtocol.Run;

public class BatchProducer<T> extends UntypedActor {
	private Iterator<T> iterator;
	private int batchCapacity;
    BatchProducer(Iterator<?> iterator,int batchCapacity){
    	
    }
    
	@Override
	public void onReceive(Object message) throws Exception {
		if(message instanceof Run){
			int numofextrateditem=0;
			List<T> data=new LinkedList<T>();
			// There are no Data to extract
			if(!iterator.hasNext()){
				getSender().tell(new EOFBatch(), getSelf());
			}else{
				while(iterator.hasNext()){
					data.add(iterator.next());
					numofextrateditem+=1;
				  if(numofextrateditem==batchCapacity){
					  numofextrateditem=0;
					  BatchData<T> batchdata=new BatchData<T>(data);
					  Batch batch=new Batch(batchdata);
					  getSelf().tell(batch, getSelf());
					  data.clear();
				  }
				}
			}
		}
	}
	

}
