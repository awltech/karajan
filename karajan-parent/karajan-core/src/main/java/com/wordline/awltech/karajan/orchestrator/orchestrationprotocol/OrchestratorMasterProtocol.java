package com.wordline.awltech.karajan.orchestrator.orchestrationprotocol;

import java.io.Serializable;

import com.wordline.awltech.karajan.orchestrator.model.ActorStep;

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
		private static final long serialVersionUID = 2785898030944263156L;
		public EOFBatch() {}

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
		private static final long serialVersionUID = 8792514851636013679L;
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
		private static final long serialVersionUID = -5522751629474891773L;
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

}
