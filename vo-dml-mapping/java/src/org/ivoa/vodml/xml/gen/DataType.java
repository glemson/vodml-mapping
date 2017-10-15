
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


import java.util.ArrayList;
 
public class DataType extends ValueType
{
  public static final String E_attribute="attribute";
  public static final String E_reference="reference";
  private ArrayList<Attribute> _attribute;
  private ArrayList<Reference> _reference;
  public DataType(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public Attribute add_attribute() throws XMLParsingException {
    if(_attribute == null)_attribute = new ArrayList<Attribute>();
    Attribute el = new Attribute(this);
    _attribute.add(el);    return el;
  }
  public ArrayList<Attribute> get_attribute() {
    return this._attribute;
  }
  public Reference add_reference() throws XMLParsingException {
    if(_reference == null)_reference = new ArrayList<Reference>();
    Reference el = new Reference(this);
    _reference.add(el);    return el;
  }
  public ArrayList<Reference> get_reference() {
    return this._reference;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_attribute.equals(name)) return add_attribute();
    else if (E_reference.equals(name)) return add_reference();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
