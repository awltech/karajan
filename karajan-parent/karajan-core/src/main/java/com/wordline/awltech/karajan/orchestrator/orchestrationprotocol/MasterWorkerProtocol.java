package com.wordline.awltech.karajan.orchestrator.orchestrationprotocol;

import java.io.Serializable;

public abstract class MasterWorkerProtocol {

	  // Messages from/to Workers

	  public static final class RegisterWorker implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 8558865759740489107L;
		public final String workerId;

	    public RegisterWorker(String workerId) {
	      this.workerId = workerId;
	    }

	    @Override
	    public String toString() {
	      return "RegisterWorker{" +
	        "workerId='" + workerId + '\'' +
	        '}';
	    }
	  }

	  public static final class WorkerRequestsWork implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 2031781447402449154L;
		public final String workerId;

	    public WorkerRequestsWork(String workerId) {
	      this.workerId = workerId;
	    }

	    @Override
	    public String toString() {
	      return "WorkerRequestsWork{" +
	        "workerId='" + workerId + '\'' +
	        '}';
	    }
	  }

	  public static final class WorkIsDone implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -8471963233761734993L;
		public final String workerId;
	    public final String workId;
	    public final Object result;

	    public WorkIsDone(String workerId, String workId, Object result) {
	      this.workerId = workerId;
	      this.workId = workId;
	      this.result = result;
	    }

	    @Override
	    public String toString() {
	      return "WorkIsDone{" +
	        "workerId='" + workerId + '\'' +
	        ", workId='" + workId + '\'' +
	        ", result=" + result +
	        '}';
	    }
	  }

	  public static final class WorkFailed implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -4463255024408260225L;
		public final String workerId;
	    public final String workId;

	    public WorkFailed(String workerId, String workId) {
	      this.workerId = workerId;
	      this.workId = workId;
	    }

	    @Override
	    public String toString() {
	      return "WorkFailed{" +
	        "workerId='" + workerId + '\'' +
	        ", workId='" + workId + '\'' +
	        '}';
	    }
	  }

	  // Messages to Workers

	  public static final class WorkIsReady implements Serializable {
	    /**
		 * 
		 */
		private static final long serialVersionUID = -5494735178985798810L;
		private static final WorkIsReady instance = new WorkIsReady();
	    public static WorkIsReady getInstance() {
	      return instance;
	    }
	  }
	}