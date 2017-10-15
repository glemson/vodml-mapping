
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


import java.util.ArrayList;
 
public class Enumeration extends PrimitiveType
{
  public static final String E_literal="literal";
  private ArrayList<EnumLiteral> _literal;
  public Enumeration(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public EnumLiteral add_literal() throws XMLParsingException {
    if(_literal == null)_literal = new ArrayList<EnumLiteral>();
    EnumLiteral el = new EnumLiteral(this);
    _literal.add(el);    return el;
  }
  public ArrayList<EnumLiteral> get_literal() {
    return this._literal;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_literal.equals(name)) return add_literal();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
