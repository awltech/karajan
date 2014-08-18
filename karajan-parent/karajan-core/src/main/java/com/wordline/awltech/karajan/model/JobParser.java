package com.wordline.awltech.karajan.model;

import static javax.xml.stream.XMLStreamConstants.END_ELEMENT;
import static javax.xml.stream.XMLStreamConstants.START_ELEMENT;

import java.io.InputStream;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public final class JobParser {
	
	 public static Job parseJob(final InputStream inputStream, final ClassLoader classLoader) throws XMLStreamException {
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
							 step.addErrorHandling(parseErrorHandling(reader));
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
		 return null;
	 }

}
