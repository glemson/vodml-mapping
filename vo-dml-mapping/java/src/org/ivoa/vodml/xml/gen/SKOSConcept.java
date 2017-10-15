
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


import java.util.ArrayList;
 
public class SKOSConcept extends XMLElement
{
  public static final String E_broadestSKOSConcept="broadestSKOSConcept";
  public static final String E_vocabularyURI="vocabularyURI";
  private XMLTextElement _broadestSKOSConcept;
  private ArrayList<XMLTextElement> _vocabularyURI;
  public SKOSConcept(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_broadestSKOSConcept() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._broadestSKOSConcept = el;
    return this._broadestSKOSConcept;
  }
  public XMLTextElement get_broadestSKOSConcept() {
    return this._broadestSKOSConcept;
  }
  public String get_broadestSKOSConcept_value() {
    return (this._broadestSKOSConcept == null?null:this._broadestSKOSConcept.getValue());
  }
  public XMLTextElement add_vocabularyURI() throws XMLParsingException {
    if(_vocabularyURI == null)_vocabularyURI = new ArrayList<XMLTextElement>();
    XMLTextElement el = new XMLTextElement(this);
    _vocabularyURI.add(el);    return el;
  }
  public ArrayList<XMLTextElement> get_vocabularyURI() {
    return this._vocabularyURI;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_broadestSKOSConcept.equals(name)) return add_broadestSKOSConcept();
    else if (E_vocabularyURI.equals(name)) return add_vocabularyURI();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
