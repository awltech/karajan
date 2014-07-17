package com.wordline.awltech.karajan.orchestrator.orchestrationutils;

import akka.japi.Procedure;


public abstract class Behavior implements Procedure<Object> {
  public abstract void apply(Object message);
}