
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


import java.util.ArrayList;
 
public class SemanticConcept extends XMLElement
{
  public static final String E_topConcept="topConcept";
  public static final String E_vocabularyURI="vocabularyURI";
  private XMLTextElement _topConcept;
  private ArrayList<XMLTextElement> _vocabularyURI;
  public SemanticConcept(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_topConcept() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._topConcept = el;
    return this._topConcept;
  }
  public XMLTextElement get_topConcept() {
    return this._topConcept;
  }
  public String get_topConcept_value() {
    return (this._topConcept == null?null:this._topConcept.getValue());
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
    if (E_topConcept.equals(name)) return add_topConcept();
    else if (E_vocabularyURI.equals(name)) return add_vocabularyURI();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
