package com.wordline.awltech.karajan.testmodel;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javax.xml.stream.XMLStreamException;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordline.awltech.karajan.batchmodel.ErrorHandling;
import com.wordline.awltech.karajan.batchmodel.ExceptionElement;
import com.wordline.awltech.karajan.batchmodel.Job;
import com.wordline.awltech.karajan.batchmodel.JobParser;
import com.wordline.awltech.karajan.batchmodel.Step;

public class TestParser {
	
	private static String file="D:\\Utilisateurs\\A577139\\workspace\\karajan2\\karajan-parent\\"
			+ "karajan-glue\\src\\test\\java\\com\\wordline\\awltech\\karajan\\testmodel\\myjob.xml";

	private static InputStream inputstream;
	
	@BeforeClass
	public static void setup() {
		
	}
	
	@AfterClass
	public static void teardown() {
	
	}
	
	
	@Test
	public void verifyJobElements() throws XMLStreamException {
		try {
			inputstream = new FileInputStream(file);
			 Job job=JobParser.parseJob(inputstream);
			 Assert.assertEquals("myjob", job.getId());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@Test
	public void verifyJobStepnumber() throws XMLStreamException {
		try {
			inputstream = new FileInputStream(file);
			Job job=JobParser.parseJob(inputstream);
			 Assert.assertNotNull(job);
			 Assert.assertEquals(2, job.getSteps().size());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	@Test
	public void verifyJobStepElements() throws XMLStreamException {
		try {
			inputstream = new FileInputStream(file);
			Job job=JobParser.parseJob(inputstream);
			Step step1=job.getSteps().get(0);
			Step step2=job.getSteps().get(1);
			Assert.assertNotNull(step1);
			Assert.assertNotNull(step2);
		    Assert.assertEquals("step1", step1.getId());
			Assert.assertEquals("step2", step2.getId());
			Assert.assertEquals("implementation1", step1.getRef());
			Assert.assertEquals("implementation2", step2.getRef()); 
			Assert.assertEquals("step2", step1.getNext()); 
			Assert.assertEquals(null, step2.getNext()); 
			Assert.assertEquals(5, step1.getParallelization());
			Assert.assertEquals(4, step2.getParallelization());
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	@Test
	public void verifyJobStepErrorHandlingSize() throws XMLStreamException {
		try {
			inputstream = new FileInputStream(file);
			Job job=JobParser.parseJob(inputstream);
			ErrorHandling handler1=job.getSteps().get(0).getErrorshandler();
			ErrorHandling handler2=job.getSteps().get(1).getErrorshandler();
		    Assert.assertEquals(2, handler1.getExceptionElements().size());
			Assert.assertEquals(1, handler2.getExceptionElements().size());
		
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	@Test
	public void verifyJobStepErrorHandlingException() throws XMLStreamException {
		try {
			inputstream = new FileInputStream(file);
			Job job=JobParser.parseJob(inputstream);
			ExceptionElement exception1=job.getSteps().get(0).getErrorshandler().getExceptionElements().get(0);
			ExceptionElement exception2=job.getSteps().get(0).getErrorshandler().getExceptionElements().get(1);
			ExceptionElement exception3=job.getSteps().get(1).getErrorshandler().getExceptionElements().get(0);
			Assert.assertNotNull(exception1);
			Assert.assertNotNull(exception2);
			Assert.assertNotNull(exception3);
			Assert.assertEquals("ProcessorException", exception1.getException());
			Assert.assertEquals("RuntimeException", exception2.getException());
			Assert.assertEquals("Exception", exception3.getException());
			Assert.assertEquals("ALL", exception1.getStategy().name());
			Assert.assertEquals("ONE", exception2.getStategy().name());
			Assert.assertEquals("ONE", exception3.getStategy().name());
			Assert.assertEquals("SKIP", exception1.getAction().name());
			Assert.assertEquals("RETRY", exception2.getAction().name());
			Assert.assertEquals("RETRY", exception3.getAction().name());
			Assert.assertEquals(5, exception1.getTrynumber());
			Assert.assertEquals(1, exception2.getTrynumber());
			Assert.assertEquals(2, exception3.getTrynumber());
			
		   
		;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}
	
	
}
