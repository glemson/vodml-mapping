package org.ivoa.vodml.xsd;


public class XMLIllegalAttributeValueException extends XMLIllegalAttributeException {
	public XMLIllegalAttributeValueException(XMLElement parent, String attrName,
			String typeName, String value)
	{
		super(String.format("'%s' is not a valid value for attribute %s on VOTable type %s. "
				+ "It must be a %s",
				value,attrName,(parent != null?parent.getClass().getName():"ERROR NO PARENT"),
				typeName));
	}
}
