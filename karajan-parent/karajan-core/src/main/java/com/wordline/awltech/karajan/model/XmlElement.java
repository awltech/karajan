package com.wordline.awltech.karajan.model;

import java.util.HashMap;
import java.util.Map;

public enum XmlElement {
	 UNKNOWN(null),

	    //all elements from job xml, in alphabetical order
	    JOB("job"),
	    STEP("step"),
	    ERRORHNADLING("errorhandling"),
	    EXCEPTION("exception");
	    
	    
	    private final String name;

	    XmlElement(final String name) {
	        this.name = name;
	    }

	    /**
	* Get the local name of this element.
	*
	* @return the local name
	*/
	    public String getLocalName() {
	        return name;
	    }

	    private static final Map<String, XmlElement> MAP;

	    static {
	        final Map<String, XmlElement> map = new HashMap<String, XmlElement>();
	        for (final XmlElement element : values()) {
	            final String name = element.getLocalName();
	            if (name != null) {
	                map.put(name, element);
	            }
	        }
	        MAP = map;
	    }

	    public static XmlElement forName(final String localName) {
	        final XmlElement element = MAP.get(localName);
	        return element == null ? UNKNOWN : element;
	    }

}
