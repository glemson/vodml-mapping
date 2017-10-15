
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


public class Composition extends Relation
{
  public static final String E_isOrdered="isOrdered";
  private XMLTextElement _isOrdered;
  public Composition(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_isOrdered() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._isOrdered = el;
    return this._isOrdered;
  }
  public XMLTextElement get_isOrdered() {
    return this._isOrdered;
  }
  public String get_isOrdered_value() {
    return (this._isOrdered == null?null:this._isOrdered.getValue());
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_isOrdered.equals(name)) return add_isOrdered();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
