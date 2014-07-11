package com.wordline.awltech.karajan.api;


public interface ItemStepProcessor<I> {
	/**
	 * This is the enter point of the step. This function is called
	 * when the step receive the Batchdata from the precedent Step
	 * @param BatchData the input of the Step as BatchData.
	 * @throws Exception thrown for any errors. 
	 */
	public void onReceive(BatchData<I> batchdata) throws Exception;
	
	/**
	 * The processItem accepts an input item 
	 * and apply the step business logic and passe the result 
	 * to the item after process. Returning null 
     * indicates that the item should not be continued 
     * to be processed.  
	 * @param item specifies the input item to process.
	 * @return output item.
	 * @throws Exception thrown for any errors. 
	 */
	public I onProcessItem(I item) throws Exception;
	
	/**
	 * The afterProcess method receives control after an item 
	 * processor processes an item.  The method receives the item processed 
	 * as BatchData.
	 * @param BatchData the result of the processed step as a Batch Data.
	 * @throws Exception if an error occurs.
	 */
	public void afterProcess(BatchData<I> batchdata) throws Exception;
	/**
	 * 	Called before step restarted. Useful when you want to log you 
	 *  step Execution
	 * @throws Exception
	 */
	public void onRestartStep()throws Exception;
	/**
	 *  Called when before step resumed. This operation consists of canceling all the
	 *  processing. Useful when you want to log you step Execution
	 * @throws Exception
	 */
	public void onResumeStep() throws Exception;
	/**
	 * Called before step stopped. Useful when you want to log you 
	 * step Execution
	 * @throws Exception
	 */
	public void onStopStep() throws Exception;
	
	
}
