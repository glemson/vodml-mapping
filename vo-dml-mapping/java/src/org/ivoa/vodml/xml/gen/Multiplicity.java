
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


public class Multiplicity extends XMLElement
{
  public static final String E_minOccurs="minOccurs";
  public static final String E_maxOccurs="maxOccurs";
  private XMLTextElement _minOccurs;
  private XMLTextElement _maxOccurs;
  public Multiplicity(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_minOccurs() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._minOccurs = el;
    return this._minOccurs;
  }
  public XMLTextElement get_minOccurs() {
    return this._minOccurs;
  }
  public String get_minOccurs_value() {
    return (this._minOccurs == null?null:this._minOccurs.getValue());
  }
  public XMLTextElement add_maxOccurs() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._maxOccurs = el;
    return this._maxOccurs;
  }
  public XMLTextElement get_maxOccurs() {
    return this._maxOccurs;
  }
  public String get_maxOccurs_value() {
    return (this._maxOccurs == null?null:this._maxOccurs.getValue());
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_minOccurs.equals(name)) return add_minOccurs();
    else if (E_maxOccurs.equals(name)) return add_maxOccurs();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
