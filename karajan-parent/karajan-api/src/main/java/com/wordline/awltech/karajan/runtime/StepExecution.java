
package com.wordline.awltech.karajan.runtime;

import java.util.Date;

public interface StepExecution {
	/**
	 * Get unique id for this StepExecution.
	 * @return StepExecution id 
	 */
	public long getStepExecutionId();
	/**
	 * Get step name.
	 * @return value of 'id' attribute from <step>
	 */
	public String getStepName();	
	/**
	 * Get batch status of this step execution.
	 * @return batch status.
	 */
	public BatchStatus getBatchStatus();
	/**
	 * Get time this step started.
	 * @return date (time)
	 */
	public Date getStartTime();
	/**
	 * Get time this step ended.
	 * @return date (time)
	 */
	public Date getEndTime();
	/**
	 * Get exit status of step.
	 * @return exit status
	 */
	public String getExitStatus();
	/**
	 * Get step metrics
	 * @return array of metrics 
	 */
	public Metric[] getMetrics();
}
