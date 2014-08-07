package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import akka.actor.SupervisorStrategy;

import com.wordline.awltech.karajan.model.Action;
import com.wordline.awltech.karajan.model.ErrorHandling;
import com.wordline.awltech.karajan.model.ErrorStrategy;
import com.wordline.awltech.karajan.runtime.StrategyFactory;

public class StrategyFactoryTest {
	private static ErrorHandling errorHandling=new ErrorHandling("Exception",
			ErrorStrategy.ALL, Action.RETRY, 3);
	
	@BeforeClass
	public static void setup() {
		
	}
	
	@AfterClass
	public static void teardown() {
	
	}
	
	
	@Test
	public void convertTest() {
		SupervisorStrategy strategy=StrategyFactory.convert(errorHandling);
		System.out.println(strategy.decider());
		Assert.assertTrue(strategy.equals(errorHandling));
		//Assert.assertNull(strategy);
	}
}
