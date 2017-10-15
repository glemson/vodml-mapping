
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


public class Constraint extends XMLElement
{
  public static final String E_description="description";
  private XMLTextElement _description;
  public Constraint(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_description() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._description = el;
    return this._description;
  }
  public XMLTextElement get_description() {
    return this._description;
  }
  public String get_description_value() {
    return (this._description == null?null:this._description.getValue());
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_description.equals(name)) return add_description();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
