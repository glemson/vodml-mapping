package org.ivoa.vodml.xsd;


public class XMLIllegalAttributeException extends XMLParsingException {
	public XMLIllegalAttributeException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public XMLIllegalAttributeException(XMLElement parent, String name)
	{
		super(String.format("VOTable type %s has no attribute with name %s",
				(parent != null?parent.getClass().getName():"ERROR NO PARENT"),
				name));
	}
}
