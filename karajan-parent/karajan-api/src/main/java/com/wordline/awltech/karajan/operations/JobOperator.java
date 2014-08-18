package com.wordline.awltech.karajan.operations;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wordline.awltech.karajan.runtime.JobExecution;
import com.wordline.awltech.karajan.runtime.JobInstance;
import com.wordline.awltech.karajan.runtime.StepExecution;

public interface JobOperator {
	/**
	 * Returns a set of all job names known to the batch runtime.
	 * 
	 * @return a set of job names.
	 * @throws JobSecurityException
	 */
	public Set<String> getJobNames() throws JobSecurityException;
	/**
	 * Returns number of instances of a job with a particular name.
	 * 
	 * @param jobName
	 *            specifies the name of the job.
	 * @return count of instances of the named job.
	 * @throws NoSuchJobException
	 * @throws JobSecurityException
	 */
	public int getJobInstanceCount(String jobName) throws 
         NoSuchJobException,
         JobSecurityException;

	/**
	 * Returns all JobInstances belonging to a job with a particular name 
       * in reverse chronological order.
	 * 
	 * @param jobName
	 *            specifies the job name.
	 * @param start
	 *            specifies the relative starting number (zero based) to 
       *            return from the	 
	 *            maximal list of job instances.
	 * @param count
	 *            specifies the number of job instances to return from the
	 *            starting position of the maximal list of job instances.
	 * @return list of JobInstances. 
	 * @throws NoSuchJobException
	 * @throws JobSecurityException
	 */
	public List<JobInstance> getJobInstances(String jobName, int start, 
        int count)throws NoSuchJobException, JobSecurityException;

	/**
	 * Returns execution ids for job instances with the specified
	 * name that have running executions. 
	 *
	 * @param jobName
	 *            specifies the job name.
	 * @return a list of execution ids. 
	 * @throws NoSuchJobException
	 * @throws JobSecurityException
	 */
	public List<Long> getRunningExecutions(String jobName) throws 
        NoSuchJobException, JobSecurityException;

	/**
	 * Creates a new job instance and starts the first execution of that
	 * instance.
	 * @param jobXMLName
	 *            specifies the name of the Job XML describing the job.
	 * @param jobParameters
	 *            specifies the keyword/value pairs for attribute 
	 *            substitution in the Job XML.
	 * @return executionId for the job execution.
	 * @throws JobStartException
	 * @throws JobSecurityException
	 */
	public long start(String jobXMLName) throws 
        JobStartException, JobSecurityException;

	/**
	 * Restarts a failed or stopped job instance.
	 * 
	 * @param executionId
	 *            specifies the execution to to restart. This execution 
	 *            must be the most recent execution that ran.
	 * @param restartParameters
	 *            specifies the keyword/value pairs for attribute 
	 *            substitution in the Job XML.            
	 * @return new executionId
	 * @throws JobExecutionAlreadyCompleteException
	 * @throws NoSuchJobExecutionException
	 * @throws JobExecutionNotMostRecentException,
	 * @throws JobRestartException
	 * @throws JobSecurityException
	 */
	public long restart(long executionId, Iterator<?> batchdata)			
			throws JobExecutionAlreadyCompleteException,
			NoSuchJobExecutionException, 
			JobExecutionNotMostRecentException, 
			JobRestartException,
			JobSecurityException;

	/**
	 * Request a running job execution stops. This
	 * method notifies the job execution to stop 
	 * and then returns. The job execution normally 
	 * stops and does so asynchronously. Note 
	 * JobOperator cannot guarantee the jobs stops: 
	 * it is possible a badly behaved batch application 
	 * does not relinquish control. 
	 * <p>
	 * @param executionId
	 *            specifies the job execution to stop. 
	 *            The job execution must be running.
	 * @throws NoSuchJobExecutionException
	 * @throws JobExecutionNotRunningException
	 * @throws JobSecurityException
	 */
	public void stop(long executionId) throws NoSuchJobExecutionException,
			JobExecutionNotRunningException, JobSecurityException;

	/**
	 * Set batch status to ABANDONED.  The instance must have 
	 * no running execution. 
	 * <p>
	 * Note that ABANDONED executions cannot be restarted.
	 * 
	 * @param executionId
	 *            specifies the job execution to abandon.
	 * @throws NoSuchJobExecutionException
	 * @throws JobExecutionIsRunningException
	 * @throws JobSecurityException
	 */
	public void abandon(long executionId) throws 
                  NoSuchJobExecutionException, 
			JobExecutionIsRunningException, JobSecurityException;
	
	
	/**
	 * Return the job instance for the specified execution id.
	 * 
	 * @param executionId
	 *            specifies the job execution.
	 * @return job instance
	 * @throws NoSuchJobExecutionException
	 * @throws JobSecurityException
	 */
	public JobInstance getJobInstance(long executionId) throws 
        NoSuchJobExecutionException, JobSecurityException;

	/**
	 * Return all job executions belonging to the specified job instance.
	 * 
	 * @param jobInstance
	 *            specifies the job instance.
	 * @return list of job executions
	 * @throws NoSuchJobInstanceException
	 * @throws JobSecurityException 
	 */
	public List<JobExecution> getJobExecutions(JobInstance instance) throws 
        NoSuchJobInstanceException, JobSecurityException;

	/**
	 * Return job execution for specified execution id
	 * 
	 * @param executionId
	 *            specifies the job execution.
	 * @return job execution
	 * @throws NoSuchJobExecutionException
	 * @throws JobSecurityException
	 */
	public JobExecution getJobExecution(long executionId) throws 
        NoSuchJobExecutionException, JobSecurityException;

	/**
	 * Return StepExecutions for specified execution id. 
	 * 
	 * @param executionId
	 *            specifies the job execution.
	 * @return step executions (order not guaranteed)
	 * @throws NoSuchJobExecutionException
	 * @throws JobSecurityException 
	 */
	public List<StepExecution> getStepExecutions(long jobExecutionId) 
        throws NoSuchJobExecutionException, JobSecurityException;	

}
