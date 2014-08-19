package com.wordline.awltech.karajan.testmodel;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.wordline.awltech.karajan.akkamodel.ActorStep;
import com.wordline.awltech.karajan.akkamodel.CoreAPILinker;
import com.wordline.awltech.karajan.batchmodel.Job;
import com.wordline.awltech.karajan.batchmodel.Step;

public class TestLinker {
	
	private static List<Step> steps=new ArrayList<Step>();
	private static Job myjob;
	private static List<ActorStep> model;
	@BeforeClass
	public static void setup() {
	//	 Step(String id,String next, String ref, int parallelization)
		Step s1=new Step("step1","step2","impl1",2);
		Step s2=new Step("step2","step3","impl2",2);
		Step s3=new Step("step3","step4","impl3",2);
		Step s4=new Step("step4",null,"impl4",2);
		steps.add(s2);
		steps.add(s3);
		steps.add(s1);
		steps.add(s4);
		myjob=new Job("myjob", steps);
		model=CoreAPILinker.generateAKKAModel(myjob);
		
	}
	
	@AfterClass
	public static void teardown() {
	
	}
	
	
	@Test
	public void verifyElementOrder() {
		Assert.assertNotNull(model);
		Assert.assertEquals("step1", model.get(0).getName());
		Assert.assertEquals("step2", model.get(1).getName());
		Assert.assertEquals("step3", model.get(2).getName());
		Assert.assertEquals("step4", model.get(3).getName());
	}
	
	@Test
	public void verifyModelSize() {
		Assert.assertNotNull(model);
		Assert.assertEquals(4, model.size());
	}
	@Test
	public void verifyModelActorStepRef() {
		Assert.assertNotNull(model);
		Assert.assertEquals(0, model.get(0).getWorkRef());
		Assert.assertEquals(1, model.get(1).getWorkRef());
		Assert.assertEquals(2, model.get(2).getWorkRef());
		Assert.assertEquals(3, model.get(3).getWorkRef());
	}
	@Test
	public void verifyModelLastElementSucc() {
		Assert.assertNotNull(model);
		Assert.assertNull(model.get(3).getSuccesor());
	}
}
