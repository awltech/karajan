package com.wordline.awltech.karajan.runtime;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ServiceLoader;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.wordline.awltech.karajan.operations.JobOperator;


/**
 * 
 * BatchRuntime provides factory access to the JobOperator interface.
 *
 */
public class BatchRuntime {

    private final static String sourceClass = BatchRuntime.class.getName();
    private final static Logger logger = Logger.getLogger(sourceClass);
    
	/**
	* The getJobOperator factory method returns
	* an instance of the JobOperator interface.
	*
	* @return JobOperator instance.
	*/
	
	public static JobOperator getJobOperator() {
		
		
		JobOperator operator = AccessController.doPrivileged(new PrivilegedAction<JobOperator> () {
            public JobOperator run() {
                
            	ServiceLoader<JobOperator> loader = ServiceLoader.load(JobOperator.class);
            	JobOperator returnVal = null;
            	for (JobOperator provider : loader) {
        			if (provider != null) {
        				if (logger.isLoggable(Level.FINE)) {
        					logger.fine("Loaded BatchContainerServiceProvider with className = " + provider.getClass().getCanonicalName());
        				}
        				// Use first one
        				returnVal = provider;
        				break;
        			}
        		}
            	
                return returnVal;
            }
        });
		

		if (operator == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("The ServiceLoader was unable to find an implemenation for JobOperator. Check classpath for META-INF/services/javax.batch.operations.JobOperator file.");
			}
		}
		return operator;
	} 
}
