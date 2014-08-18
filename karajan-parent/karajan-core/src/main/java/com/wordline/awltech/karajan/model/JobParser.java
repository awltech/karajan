package com.wordline.awltech.karajan.model;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class JobParser {
	
	 public static Job parseJob(final InputStream inputStream) throws XMLStreamException {
		 final XMLStreamReader reader = XMLInputFactory.newInstance().createXMLStreamReader(inputStream);
			 Job job = null;
			 try {
				 while (reader.hasNext()) {
					 final int eventType = reader.next();
					 if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
						 continue;
					 }
					 final XmlElement element = XmlElement.forName(reader.getLocalName());
					 switch (eventType) {
						 case START_ELEMENT:
						 switch (element) {
							 case JOB:
								 job = new Job(getAttributeValue(reader, XmlAttribute.ID, true));
								 break;
							 
							 case STEP:
								 job.addStep((parseStep(reader, job)));
								 break;
							 default:
								 //UnexpectedXmlElement
								 throw new RuntimeException();
						 }
						 
						 break;
						 case END_ELEMENT:
						 if (element != XmlElement.JOB) {
							 throw new RuntimeException();
						 }
					 }
				 }
			 } finally {
			 reader.close();
			 }
			 return job;
		 }
	 
	 private static Step parseStep(final XMLStreamReader reader, final Job job) throws XMLStreamException{
		 final Step step = new Step(getAttributeValue(reader, XmlAttribute.ID, true));
		 step.setNext(getAttributeValue(reader, XmlAttribute.NEXT, false));
		 step.setRef(getAttributeValue(reader, XmlAttribute.REF, false));
		 step.setParallelization(Integer.parseInt(getAttributeValue(reader, XmlAttribute.PARALLELIZE, false)));
		
		 while (reader.hasNext()) {
			 final int eventType = reader.next();
			 if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
				 continue;
			 }
			 final XmlElement element = XmlElement.forName(reader.getLocalName());
			 switch (eventType) {
				 case START_ELEMENT:
					 switch (element) {
						 case ERRORHNADLING:
							 step.setErrorshandler(parseErrorHandling(reader));
							 break;
						 default:
							// unexpectedXmlElement
							 throw new RuntimeException();
							 
					 }
				 break;
				 case END_ELEMENT:
				 switch (element) {
					 case STEP:
					 return step;
					 default:
					// unexpectedXmlElement
					 throw new RuntimeException();
				 }
				 
			 }
			
			 
		 }
		// unexpectedXmlElement
		 throw new RuntimeException();
		 
	 }
	 
	 private static String getAttributeValue(final XMLStreamReader reader, final XmlAttribute attribute, final boolean required) {
		 final String val = reader.getAttributeValue(null, attribute.getLocalName());
		 if (val == null && required) {
			 //MESSAGES.failToGetAttribute
			 throw new RuntimeException();
		 }
		 return val;
		 
	 }
	 
	 private static ErrorHandling parseErrorHandling(final XMLStreamReader reader) throws XMLStreamException{
		 final ErrorHandling errorHandling=new ErrorHandling();
		 
		 while (reader.hasNext()) {
			 final int eventType = reader.next();
			 if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
				 continue;
			 }
			 final XmlElement element = XmlElement.forName(reader.getLocalName());
			 switch (eventType) {
				 case START_ELEMENT:
					 switch (element) {
						 case EXCEPTION:
							 errorHandling.addExceptionElement((parseException(reader)));
							 break;
						 default:
							// unexpectedXmlElement
							 throw new RuntimeException();
							 
					 }
				 break;
				 case END_ELEMENT:
				 switch (element) {
					 case ERRORHNADLING:
					 return errorHandling;
					 default:
					// unexpectedXmlElement
					 throw new RuntimeException();
				 }
				 
			 }
			
			 
		 }
		// unexpectedXmlElement
		 throw new RuntimeException();
		 
	 }
	 
	 private static ExceptionElement parseException(XMLStreamReader reader)throws XMLStreamException{
		 final ExceptionElement exception = new ExceptionElement();
		 exception.setException(getAttributeValue(reader, XmlAttribute.ON, false));
		 exception.setStategy(getStratety(getAttributeValue(reader, XmlAttribute.STRATEGY, false)));
		 exception.setAction(getAction(getAttributeValue(reader, XmlAttribute.ACTION, false)));
		 exception.setTrynumber(Integer.parseInt(getAttributeValue(reader, XmlAttribute.TRY, false)));
		
		
		 while (reader.hasNext()) {
			 final int eventType = reader.next();
			 if (eventType != START_ELEMENT && eventType != END_ELEMENT) {
				 continue;
			 }
			 final XmlElement element = XmlElement.forName(reader.getLocalName());
			 switch (eventType) {
				 case START_ELEMENT:
					 break;
				 case END_ELEMENT:
				 switch (element) {
					 case EXCEPTION:
					 return exception;
					 default:
					// unexpectedXmlElement
					 throw new RuntimeException();
				 }
				 
			 }
			
			 
		 }
		// unexpectedXmlElement
		 throw new RuntimeException();
	 }
	 
	 private static ErrorStrategy getStratety(String value){
		 if(value.equalsIgnoreCase(ErrorStrategy.ALL.name())){
			 return ErrorStrategy.ALL;
		 }
		 else if(value.equalsIgnoreCase(ErrorStrategy.ONE.name())){
			 return ErrorStrategy.ONE;
		 }
		 throw new RuntimeException();
	 }
	 
	 private static Action getAction(String value){
		 if(value.equalsIgnoreCase(Action.RETRY.name())){
			 return Action.RETRY;
		 }
		 else if(value.equalsIgnoreCase(Action.SKIP.name())){
			 return Action.SKIP;
		 }
		 throw new RuntimeException();
	 }

}
