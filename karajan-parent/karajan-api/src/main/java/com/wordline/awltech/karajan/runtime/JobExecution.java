package com.wordline.awltech.karajan.runtime;

import java.util.Date;
import java.util.Properties;

public interface JobExecution {
	/**
	 * Get unique id for this JobExecution.
	 * @return execution id
	 */
	public long getExecutionId();
	/**
	 * Get job name.
	 * @return value of 'id' attribute from <job>
	 */
	public String getJobName(); 
	/**
	 * Get batch status of this execution.
	 * @return batch status value.
	 */
	public BatchStatus getBatchStatus();
	/**
	 * Get time execution entered STARTED status. 
	 * @return date (time)
	 */
	public Date getStartTime();
	/**
	 * Get time execution entered end status: COMPLETED, STOPPED, FAILED 
	 * @return date (time)
	 */
	public Date getEndTime();
	/**
	 * Get execution exit status.
	 * @return exit status.
	 */
	public String getExitStatus();
	/**
	 * Get time execution was created.
	 * @return date (time)
	 */
	public Date getCreateTime();


	
}
