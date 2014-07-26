package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordline.awltech.karajan.api.BatchData;

public class BatchDataTest {
	
	private static BatchData<Integer> data1;
	private static BatchData<Integer> data2;
	
	@BeforeClass
	public static void setup() {
		data1=new BatchData<Integer>();
		data2=new BatchData<Integer>(data1);
	}
	
	@AfterClass
	public static void teardown() {
	
	}
	
	
	@Test
	public void sameId() {
		  Assert.assertEquals(data1.getId(), data2.getId());
	}
	
	@Test
	public void notSameId() {
		  Assert.assertFalse(data1.equals("dsds"));
	}
	
}
