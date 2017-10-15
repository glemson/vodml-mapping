
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


public class SubsettedRole extends Constraint
{
  public static final String E_role="role";
  public static final String E_datatype="datatype";
  public static final String E_semanticconcept="semanticconcept";
  private ElementRef _role;
  private ElementRef _datatype;
  private SemanticConcept _semanticconcept;
  public SubsettedRole(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public ElementRef add_role() throws XMLParsingException {
    ElementRef el = new ElementRef(this);
    this._role = el;
    return this._role;
  }
  public ElementRef get_role() {
    return this._role;
  }
  public ElementRef add_datatype() throws XMLParsingException {
    ElementRef el = new ElementRef(this);
    this._datatype = el;
    return this._datatype;
  }
  public ElementRef get_datatype() {
    return this._datatype;
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
    if (E_role.equals(name)) return add_role();
    else if (E_datatype.equals(name)) return add_datatype();
    else if (E_semanticconcept.equals(name)) return add_semanticconcept();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
