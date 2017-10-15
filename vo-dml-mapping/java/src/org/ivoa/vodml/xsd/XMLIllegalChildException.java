package org.ivoa.vodml.xsd;


public class XMLIllegalChildException extends XMLParsingException {
	public XMLIllegalChildException(XMLElement parent, String name)
	{
		super(String.format("VOTable type %s has no child with name %s",
				(parent != null?parent.getClass().getName():"ERROR NO PARENT"),
				name));
	}
	public XMLIllegalChildException(XMLElement parent, String child, String xsiType)
	{
		super(String.format("VOTable type %s has no child with name %s and possible type %s",
				(parent != null?parent.getClass().getName():"ERROR NO PARENT"),
				child, xsiType));
	}
}
