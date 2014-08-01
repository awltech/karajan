package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.api.ItemStepProcessor;
import com.wordline.awltech.karajan.operations.ProcessorException;

public class Implementation1 implements ItemStepProcessor<Integer> {

	@Override
	public void onReceive(BatchData<Integer> batchdata) throws ProcessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer onProcessItem(Integer item) throws ProcessorException {
		// TODO Auto-generated method stub
		return item*2;
	}

	@Override
	public void afterProcess(BatchData<Integer> batchdata) throws ProcessorException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRestartStep() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResumeStep() throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopStep() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
