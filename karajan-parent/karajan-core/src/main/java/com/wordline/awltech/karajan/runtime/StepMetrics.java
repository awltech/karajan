package com.wordline.awltech.karajan.runtime;

import com.wordline.awltech.karajan.runtime.Metric.MetricType;



public class StepMetrics  {
  public static MetricImpl RECEIVED=new MetricImpl(MetricType.RECEIVED);
  public static MetricImpl PROCESSED=new MetricImpl(MetricType.PROCESSED);
 
}
