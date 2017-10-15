
package org.ivoa.vodml.xml;

import org.ivoa.vodml.xml.gen.ElementRef;
import org.ivoa.vodml.xml.gen.Multiplicity;
import org.ivoa.vodml.xsd.*;


public abstract class Role extends ReferableElement
{
  public static final String E_datatype="datatype";
  public static final String E_multiplicity="multiplicity";
  public static final String E_subsets="subsets";
  private ElementRef _datatype;
  private Multiplicity _multiplicity;
  private ElementRef _subsets;
  public Role(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public ElementRef add_datatype() throws XMLParsingException {
    ElementRef el = new ElementRef(this);
    this._datatype = el;
    return this._datatype;
  }
  public ElementRef get_datatype() {
    return this._datatype;
  }
  public Multiplicity add_multiplicity() throws XMLParsingException {
    Multiplicity el = new Multiplicity(this);
    this._multiplicity = el;
    return this._multiplicity;
  }
  public Multiplicity get_multiplicity() {
    return this._multiplicity;
  }
  public ElementRef add_subsets() throws XMLParsingException {
    ElementRef el = new ElementRef(this);
    this._subsets = el;
    return this._subsets;
  }
  public ElementRef get_subsets() {
    return this._subsets;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_datatype.equals(name)) return add_datatype();
    else if (E_multiplicity.equals(name)) return add_multiplicity();
    else if (E_subsets.equals(name)) return add_subsets();
    else return super.addElement(name, xsiType);
  }


  /**    Put all hand modifications below this line */
  public String getDatatype(){
	return get_datatype().get_vodml_ref().getValue();
  }
 
}
