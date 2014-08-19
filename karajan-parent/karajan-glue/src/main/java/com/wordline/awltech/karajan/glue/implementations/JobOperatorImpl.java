package com.wordline.awltech.karajan.glue.implementations;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.wordline.awltech.karajan.operations.JobExecutionAlreadyCompleteException;
import com.wordline.awltech.karajan.operations.JobExecutionIsRunningException;
import com.wordline.awltech.karajan.operations.JobExecutionNotMostRecentException;
import com.wordline.awltech.karajan.operations.JobExecutionNotRunningException;
import com.wordline.awltech.karajan.operations.JobOperator;
import com.wordline.awltech.karajan.operations.JobRestartException;
import com.wordline.awltech.karajan.operations.JobSecurityException;
import com.wordline.awltech.karajan.operations.JobStartException;
import com.wordline.awltech.karajan.operations.NoSuchJobException;
import com.wordline.awltech.karajan.operations.NoSuchJobExecutionException;
import com.wordline.awltech.karajan.operations.NoSuchJobInstanceException;
import com.wordline.awltech.karajan.runtime.JobExecution;
import com.wordline.awltech.karajan.runtime.JobInstance;
import com.wordline.awltech.karajan.runtime.StepExecution;

public class JobOperatorImpl implements JobOperator{

	@Override
	public Set<String> getJobNames() throws JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getJobInstanceCount(String jobName) throws NoSuchJobException,
			JobSecurityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<JobInstance> getJobInstances(String jobName, int start,
			int count) throws NoSuchJobException, JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getRunningExecutions(String jobName)
			throws NoSuchJobException, JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long start(String jobXMLName) throws JobStartException,
			JobSecurityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long restart(long executionId, Iterator<?> batchdata)
			throws JobExecutionAlreadyCompleteException,
			NoSuchJobExecutionException, JobExecutionNotMostRecentException,
			JobRestartException, JobSecurityException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void stop(long executionId) throws NoSuchJobExecutionException,
			JobExecutionNotRunningException, JobSecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void abandon(long executionId) throws NoSuchJobExecutionException,
			JobExecutionIsRunningException, JobSecurityException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public JobInstance getJobInstance(long executionId)
			throws NoSuchJobExecutionException, JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<JobExecution> getJobExecutions(JobInstance instance)
			throws NoSuchJobInstanceException, JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public JobExecution getJobExecution(long executionId)
			throws NoSuchJobExecutionException, JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<StepExecution> getStepExecutions(long jobExecutionId)
			throws NoSuchJobExecutionException, JobSecurityException {
		// TODO Auto-generated method stub
		return null;
	}

}
