package org.ivoa.vodml.xsd;


public class XMLIllegalMultiplicityException extends XMLParsingException {
	public XMLIllegalMultiplicityException(XMLElement parent, String name)
	{
		super(String.format("VOTable type %s may have at most 1 child element with name %s",
				(parent != null?parent.getClass().getName():"ERROR NO PARENT"),
				name));
	}
}
