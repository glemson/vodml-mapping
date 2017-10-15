package org.ivoa.vodml.xsd;

import java.util.ArrayList;

import javax.naming.OperationNotSupportedException;

import org.xmlpull.v1.XmlPullParser;

/**
 * Common base class of all classes representing types in an XML schema.
 * @author gerard
 *
 */
public abstract class XMLElement {

	private XMLElement parent = null;
	private int rankInDocument=0;

	/** It is assumed that class name is based on XSD type name, 
	 * if not this method must be overridden*/
	public String getXSDType(){
		return this.getClass().getName();
	}
	public void text(String _value) throws XMLParsingException {
		throw new XMLParsingException(String.format("%s has no text content", this.getClass().getName()));
	}
	protected XMLElement(XMLElement _parent) throws XMLParsingException
	{
		this.parent = _parent;
	}
	
	public int getRankInDocument() {
		return rankInDocument;
	}
	public void setRankInDocument(int rankInDocument) {
		this.rankInDocument = rankInDocument;
	}

	public XMLElement addElement(String name) throws XMLParsingException 
	{
		return addElement(name, null);
	}
	public XMLElement addElement(String name, String xsiType) throws XMLParsingException {
		throw new XMLParsingException(String.format("Illegal attempt made to add child with name '%s' on type %s",name,getClass().getName()));
	}
	
	public void setAttributes(XmlPullParser parser) throws XMLParsingException{
		for(int i = 0; i < parser.getAttributeCount(); i++)
			addAttribute(parser.getAttributeName(i),parser.getAttributeValue(i));
	}
	public void addAttribute(String name, String value)  throws XMLIllegalAttributeException
	{
		if(name == null)
			return;
		if(name.startsWith("xmlns:")) // OK
			addNamespaceMapping(name,value);
		else if(name.startsWith("xsi:")) // OK but ignore, should have been dealt with when creating type itself
			return;
		else
			throw new XMLIllegalAttributeException(this,name);
	}
	
	/**
	 * @param name
	 * @param value
	 */
	protected void addNamespaceMapping(String name, String value){
		 // TODO do something interesting with namespaces.
	}
	public XMLElement getParent() {
		return parent;
	}
}
