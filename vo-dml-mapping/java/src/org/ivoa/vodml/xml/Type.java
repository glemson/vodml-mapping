
package org.ivoa.vodml.xml;

import org.ivoa.vodml.xml.gen.Constraint;
import org.ivoa.vodml.xml.gen.ElementRef;
import org.ivoa.vodml.xsd.*;

import java.util.ArrayList;
 
public abstract class Type extends ReferableElement
{
  public static final String E_extends="extends";
  public static final String E_constraint="constraint";
  public static final String A_abstract="abstract";
  private ElementRef _extends;
  private ArrayList<Constraint> _constraint;
  private String _abstract;
  public Type(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public ElementRef add_extends() throws XMLParsingException {
    ElementRef el = new ElementRef(this);
    this._extends = el;
    return this._extends;
  }
  public ElementRef get_extends() {
    return this._extends;
  }
  public Constraint add_constraint() throws XMLParsingException {
    if(_constraint == null)_constraint = new ArrayList<Constraint>();
    Constraint el = new Constraint(this);
    _constraint.add(el);    return el;
  }
  public ArrayList<Constraint> get_constraint() {
    return this._constraint;
  }
  public void set_abstract(String _v) {
    this._abstract = _v;
  }
  public String get_abstract(){
     return this._abstract;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_extends.equals(name)) return add_extends();
    else if (E_constraint.equals(name)) return add_constraint();
    else return super.addElement(name, xsiType);
  }



  /**    Put all hand modifications below this line */

  private ArrayList<Type> subclasses;
  private Type supertype = null;
  protected void setSuperType(Type t) {
	  if(t == null || this.get_extends() == null) return;
	  if(t.getVodmlref().equals(get_extends().get_vodml_ref_value()))
		  this.supertype = t;
  }
  protected void addSubClass(Type sc){
	  if(subclasses == null) subclasses = new ArrayList<Type>(); 
	  if(sc.get_extends().get_vodml_ref_value().equals(this.getVodmlref()))
		  subclasses.add(sc);
  }
public ArrayList<Type> getSubclasses() {
	return subclasses;
}
public void setSubclasses(ArrayList<Type> subclasses) {
	this.subclasses = subclasses;
}
}
