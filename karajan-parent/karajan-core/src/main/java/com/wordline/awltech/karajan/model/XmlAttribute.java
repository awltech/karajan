package com.wordline.awltech.karajan.model;

import java.util.HashMap;
import java.util.Map;

public enum XmlAttribute {
	UNKNOWN(null),

    //attributes from job xml & batch.xml, in alphabetical order
    ID("id"),
    NEXT("next"),
    ON("on"),
    STATEGY("value"),
	ACTION("action"),
	NUMBEROFTRY("numberoftry");

    private final String name;

    XmlAttribute(final String name) {
        this.name = name;
    }

    /**
* Get the local name of this attribute.
*
* @return the local name
*/
    public String getLocalName() {
        return name;
    }

    private static final Map<String, XmlAttribute> MAP;

    static {
        final Map<String, XmlAttribute> map = new HashMap<String, XmlAttribute>();
        for (final XmlAttribute attribute : values()) {
            final String name = attribute.getLocalName();
            if (name != null) {
                map.put(name, attribute);
            }
        }
        MAP = map;
    }

    public static XmlAttribute forName(final String localName) {
        final XmlAttribute attribute = MAP.get(localName);
        return attribute == null ? UNKNOWN : attribute;
    }

}
