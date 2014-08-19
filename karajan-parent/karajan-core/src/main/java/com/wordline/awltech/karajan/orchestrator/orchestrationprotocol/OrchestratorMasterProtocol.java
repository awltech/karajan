package com.wordline.awltech.karajan.orchestrator.orchestrationprotocol;

import java.io.Serializable;

import com.wordline.awltech.karajan.akkamodel.ActorStep;
import com.wordline.awltech.karajan.api.BatchData;
import com.wordline.awltech.karajan.batchmodel.Action;

public class OrchestratorMasterProtocol {
	/**
	 * 
	 * Initialization send by orchestrator to the master in order to create his workers
	 *
	 */
	 public static final class Initialization implements Serializable {
		 
		private static final long serialVersionUID = -1600162281455100052L;

		    public Initialization() {}

		    @Override
		    public String toString() {
		      return "Initialization Master{}" ;
		    }
	}
	 
	 public static final class CkeckForWorkersStatus implements Serializable {
		 /**
		  * 
		  * Message that master sent to himself in order to verify if his workers has finished to work
		  *
		  */
		private static final long serialVersionUID = 1234117882829983417L;
		public CkeckForWorkersStatus() {}

	    @Override
	    public String toString() {
	      return "Initialization Master{}" ;
	    }
		}
	 
	 public static final class Run implements Serializable {
		 /**
		  * 
		  */
		private static final long serialVersionUID = 2785898030944263156L;
		public Run() {}

	    @Override
	    public String toString() {
	      return "Initialization Master{}" ;
	    }
	  }
	 
	 public static final class EOFBatch implements Serializable {
		 /**
		 * 
		 */
		private static final long serialVersionUID = 506415911739165041L;
		/**
		  * 
		  */
	
		public final boolean endofbatch;
		public EOFBatch(){
			endofbatch=false;
		}
		public EOFBatch(boolean endofbatch) {
			this.endofbatch=endofbatch;
		}

	    @Override
	    public String toString() {
	      return "Initialization Master{}" ;
	    }
	  }
	 
	 public static final class Waiting implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5347565219262268210L;

		/**
		 * 
		 */


		public Waiting() {}

	    @Override
	    public String toString() {
	      return "Waiting {}" ;
	    }
	  }
	 
	 public static final class RegisterManager implements Serializable {
		    /**
		 * 
		 */
		private static final long serialVersionUID = -2136154922331869786L;
			/**
		 * 
		 */

			public final String workerId;
			public final ActorStep stepInfo;

		    public RegisterManager(String workerId, ActorStep stepinfo) {
		      this.workerId = workerId;
		      this.stepInfo=stepinfo;
		    }

		    @Override
		    public String toString() {
		      return "RegisterWorker{" +
		        "workerId='" + workerId + '\'' +
		        '}';
		    }
		  }
	 
	  public static final class BatchIsReady implements Serializable {
			/**
		 * 
		 */
		private static final long serialVersionUID = -7619762607672268332L;
			/**
		 * 
		 */

			private static final BatchIsReady instance = new BatchIsReady();
		    public static BatchIsReady getInstance() {
		      return instance;
		    }
		  }
	  
	  public static final class ManagerRequestsBatch implements Serializable {
		
			/**
		 * 
		 */
		private static final long serialVersionUID = -5522751629474891773L;
			/**
		 * 
		 */

			public final String masterId;

		    public ManagerRequestsBatch(String masterId) {
		      this.masterId = masterId;
		    }

		    @Override
		    public String toString() {
		      return "MasterRequestsWork{" +
		        "MasterId='" + masterId + '\'' +
		        '}';
		    }
		  }
	  
	  public static final class Started implements Serializable {
			
			/**
		 * 
		 */
		private static final long serialVersionUID = -2972401945326144043L;

			/**
		 * 
		 */

			//public final String masterId;

		    public Started() {
		     // this.masterId = masterId;
		    }

		    @Override
		    public String toString() {
		      return "MasterRequestsWork{" +
		        "MasterId='"  + '\'' +
		        '}';
		    }
		  }
	  
	  public static final class PullWork implements Serializable {
			
	
			/**
		 * 
		 */
		private static final long serialVersionUID = -3717018561848790314L;
			/**
		 * 
		 */

			public final int id;
			public final String manager;

		    public PullWork(int id,String manager) {
		      this.id = id;
		      this.manager=manager;
		    }

		    @Override
		    public String toString() {
		      return "PullWork{" +
		        "MasterId='"  + '\'' +
		        '}';
		    }
		  }
	  
	  public static final class PushWork implements Serializable {	
		/**
		 * 
		 */
		private static final long serialVersionUID = -4995943077536167832L;
		/**
		 * 
		 */

			public final int id;
			public final BatchData<?> data;
			

		    public PushWork(int id,BatchData<?> data) {
		      this.id = id;
		      this.data=data;
		    }

		    @Override
		    public String toString() {
		      return "PushWork{" +
		        "MasterId='"  + '\'' +
		        '}';
		    }
		  }
	  public static final class PullWorkResponse implements Serializable {	
			/**
		 * 
		 */
		private static final long serialVersionUID = -5336188236710815055L;
			/**
			 * 
			 */

				public final int id;
				public final BatchData<?> data;
				public final  String manager;

			    public PullWorkResponse(int id,BatchData<?> data,String manager) {
			      this.id = id;
			      this.data=data;
			      this.manager=manager;
			    }

			    @Override
			    public String toString() {
			      return "PushWork{" +
			        "MasterId='"  + '\'' +
			        '}';
			    }
			  }
	  public static final class isAvailableWork implements Serializable {	
			/**
		 * 
		 */
		private static final long serialVersionUID = 329049270260587924L;
			/**
			 * 
			 */

				public final int id;

			    public isAvailableWork(int id) {
			      this.id = id;
			    }

			    @Override
			    public String toString() {
			      return "PushWork{" +
			        "MasterId='"  + '\'' +
			        '}';
			    }
	  }
	  
	  public static final class BatchFail implements Serializable{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public final BatchData<?> data;
		public final String managerId;
		public final Action action;
		
		public BatchFail(Action action) {
			this.data=null;
			this.managerId=null;
			this.action=action;
		}
		public BatchFail(BatchData<?> data,Action action,String id){
			this.data=data;
			this.managerId=id;
			this.action=action;
		}
		  
	  }
	  

}
