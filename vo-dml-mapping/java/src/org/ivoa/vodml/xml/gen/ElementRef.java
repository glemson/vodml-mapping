
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


public class ElementRef extends XMLElement
{
  public static final String E_vodml_ref="vodml-ref";
  private XMLTextElement _vodml_ref;
  public ElementRef(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_vodml_ref() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._vodml_ref = el;
    return this._vodml_ref;
  }
  public XMLTextElement get_vodml_ref() {
    return this._vodml_ref;
  }
  public String get_vodml_ref_value() {
    return (this._vodml_ref == null?null:this._vodml_ref.getValue());
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_vodml_ref.equals(name)) return add_vodml_ref();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
