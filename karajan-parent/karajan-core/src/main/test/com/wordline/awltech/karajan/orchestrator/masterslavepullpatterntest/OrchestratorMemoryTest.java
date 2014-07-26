package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.orchestrator.model.OrchestrationMemory;

public class OrchestratorMemoryTest {
	private static OrchestrationMemory memory;
	
	@BeforeClass
	public static void setup() {
		memory=new OrchestrationMemory(5);
	}
	
	@AfterClass
	public static void teardown() {
	
	}
	
	
	@Test
	public void isEmptyTest() {
		 Assert.assertTrue(memory.isEmpty());
	}
	
	@Test
	public void notAvailableWork() {
		Assert.assertFalse(memory.isAvailableWorkFor(0));
		 Assert.assertFalse(memory.isAvailableWorkFor(1));
		 Assert.assertFalse(memory.isAvailableWorkFor(2));
		 Assert.assertFalse(memory.isAvailableWorkFor(3));
		 Assert.assertFalse(memory.isAvailableWorkFor(4));
		 
	}
	
	@Test
	public void push() {
		memory.pushWork(1, new BatchData<Integer>());
		Assert.assertTrue(memory.isAvailableWorkFor(1));	 
	}
	
	@Test
	public void pull() {
		memory.pushWork(1, new BatchData<Integer>());
		Assert.assertTrue(memory.isAvailableWorkFor(1));
		memory.pullWork(1);
		Assert.assertFalse(memory.isAvailableWorkFor(1));
		
	}
	
	@Test
	public void returnedType() {
		memory.pushWork(1, new BatchData<Integer>());
		BatchData<?> result= memory.pullWork(1);
		Assert.assertTrue(result instanceof BatchData);
		
	}
	
}
