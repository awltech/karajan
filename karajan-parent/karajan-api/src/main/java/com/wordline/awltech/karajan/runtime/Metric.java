package com.wordline.awltech.karajan.runtime;
/**
  * 
  * The Metric interface defines job metrics recorded by
  * the batch runtime.
  *
  */
public interface Metric {
	public enum MetricType {READ_COUNT, WRITE_COUNT, READ_SKIP_COUNT, PROCESS_SKIP_COUNT, 
	       WRITE_SKIP_COUNT}
	/**
	 * The getName method returns the metric type. 
	 * @return metric type.
	 */
	public MetricType getType(); 
	/**
	 * The getValue method returns the metric value.
	 * @return metric value.
	 */
	public long getValue();
}
