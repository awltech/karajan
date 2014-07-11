package com.wordline.awltech.karajan.runtime;

public interface JobInstance {
	/**
	 * Get unique id for this JobInstance.
	 * @return instance id
	 */
	public long getInstanceId();
	/**
	 * Get job name.
	 * @return value of 'id' attribute from <job>
	 */
	public String getJobName();	
}
