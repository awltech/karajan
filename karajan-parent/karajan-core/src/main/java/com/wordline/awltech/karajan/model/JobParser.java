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
	 
	 private static Step parseStep(final XMLStreamReader reader, final Job job){
		return null;
		 
	 }
	 
	 private static String getAttributeValue(final XMLStreamReader reader, final XmlAttribute attribute, final boolean required) {
		 final String val = reader.getAttributeValue(null, attribute.getLocalName());
		 if (val == null && required) {
			 //MESSAGES.failToGetAttribute
		 throw new RuntimeException();
		 }
		 return val;
		 
	 }

}
