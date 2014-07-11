package com.wordline.awltech.karajan.api;




public interface ItemStepWriter<I> {
	
	/**
	 * This is the enter point of the step. This function is called
	 * when the step receive the Batchdata from the precedent Step
	 * @param BatchData the input of the Step as BatchData.
	 * @throws Exception thrown for any errors. 
	 */
	public void onReceive(BatchData<I> batchdata) throws Exception;
	/**
	 * Receive data as BatchData then write it to the output in Batch way
	 * @param BatchData contents data to be wrote in the output.
	 * @throws Exception is thrown for any errors.
	 */
	public void writeItems(BatchData<I> batchdata) throws Exception;
	
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
