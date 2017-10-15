
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xml.Role;
import org.ivoa.vodml.xsd.*;


public class Attribute extends Role
{
  public static final String E_semanticconcept="semanticconcept";
  private SemanticConcept _semanticconcept;
  public Attribute(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public SemanticConcept add_semanticconcept() throws XMLParsingException {
    SemanticConcept el = new SemanticConcept(this);
    this._semanticconcept = el;
    return this._semanticconcept;
  }
  public SemanticConcept get_semanticconcept() {
    return this._semanticconcept;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_semanticconcept.equals(name)) return add_semanticconcept();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
