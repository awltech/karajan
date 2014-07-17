package com.wordline.awltech.karajan.orchestrator.orchestrationprotocol;

import java.io.Serializable;

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
	 /**
	  * 
	  * Message that master sent to himself in order to verify if his workers has finished to work
	  *
	  */
	 public static final class CkeckForWorkersStatus implements Serializable {
		 
	
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
	 
	 

}
