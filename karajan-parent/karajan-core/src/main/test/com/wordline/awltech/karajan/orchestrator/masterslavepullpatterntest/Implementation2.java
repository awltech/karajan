package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.api.ItemStepProcessor;

public class Implementation2 implements ItemStepProcessor<Integer> {

	@Override
	public void onReceive(BatchData<Integer> batchdata) throws Exception {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer onProcessItem(Integer item) throws Exception {
		// TODO Auto-generated method stub
		return item+1;
	}

	@Override
	public void afterProcess(BatchData<Integer> batchdata) throws Exception {
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
