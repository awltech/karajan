package com.wordline.awltech.karajan.orchestrator.masterslavepullpatterntest;

import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import scala.concurrent.duration.Duration;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.testkit.JavaTestKit;

import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.orchestrator.OrchestratorImpl;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager;
import com.wordline.awltech.karajan.orchestrator.masterslavepullpattern.StepExecutionManager.BatchProcessFinished;
import com.wordline.awltech.karajan.orchestrator.orchestrationprotocol.MasterWorkerProtocol.WorkerRequestsWork;

public class MasterTest {
	public static class Orchestrator extends UntypedActor {
		public void onReceive(Object msg) {
	
		}
	}
	
		static ActorSystem system;
		ActorRef orchestrator=system.actorOf(Props.create(Orchestrator.class));
		@BeforeClass
		public static void setup() {
			system = ActorSystem.create();
			// Create Master
		//	final Props props = Props.create(StepExecutionManager.class,null,5);
		//	final ActorRef Master = system.actorOf(props);
			//Create worker
		
		}
		
		@AfterClass
		public static void teardown() {
			JavaTestKit.shutdownActorSystem(system);
		system = null;
		}
		
		
		@Test
		public void sendBatch() {
		new JavaTestKit(system) {{
				final Props props = Props.create(StepExecutionManager.class,getRef(),null,5);
				final ActorRef master= system.actorOf(props);
				BatchData<Integer> data=new BatchData<Integer>();
				master.tell(new OrchestratorImpl.Batch(data), getRef());
				expectMsgClass(OrchestratorImpl.BatchAck.class);
			}};
			  
		}
		
		@Test
		public void sendBatchAck() {
		new JavaTestKit(system) {{
				final Props props = Props.create(StepExecutionManager.class,getRef(),null,5);
				final ActorRef master= system.actorOf(props);
				BatchData<Integer> data=new BatchData<Integer>();
				master.tell(new OrchestratorImpl.Batch(data), getRef());
				receiveOne(Duration.Zero());
			 //  Assert.assertEquals(expected.workId, data.Id);
			}};
			  
		}
		
		@Test
		public void requestForWorkWhenThereIsNoWork() {
		new JavaTestKit(system) {{
				final Props props = Props.create(StepExecutionManager.class,getRef(),null,5);
				final JavaTestKit probe = new JavaTestKit(system);
				final ActorRef master= system.actorOf(props);
				BatchData<Integer> data=new BatchData<Integer>();
				//data.getData().add(5);
				master.tell(new OrchestratorImpl.Batch(data), probe.getRef());
				master.tell(new WorkerRequestsWork(UUID.randomUUID().toString()), getRef());
				//We expext Batch Process finish message
				expectMsgClass(BatchProcessFinished.class);
			}};
			  
		}
		
		@Test
		public void requestForWorkWhenThereIsWork() {
		new JavaTestKit(system) {{
			final Props props = Props.create(StepExecutionManager.class,getRef(),null,5);
			final ActorRef master= system.actorOf(props);
			BatchData<Integer> data=new BatchData<Integer>();
			data.getData().add(5);
			master.tell(new OrchestratorImpl.Batch(data), getRef());
			
			
			
				//We expect work
			  //expectMsgClass(Duration.create(10,TimeUnit.SECONDS),BatchData.class);
			}};
			  
		}
}
