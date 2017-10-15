
package org.ivoa.vodml.xml;

import org.ivoa.vodml.xml.gen.Attribute;
import org.ivoa.vodml.xml.gen.Composition;
import org.ivoa.vodml.xml.gen.Reference;
import org.ivoa.vodml.xsd.*;

import java.util.ArrayList;
 
public class ObjectType extends Type
{
  public static final String E_attribute="attribute";
  public static final String E_collection="collection";
  public static final String E_reference="reference";
  private ArrayList<Attribute> _attribute;
  private ArrayList<Composition> _collection;
  private ArrayList<Reference> _reference;
  public ObjectType(XMLElement _parent) throws XMLParsingException {
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
  public Composition add_collection() throws XMLParsingException {
    if(_collection == null)_collection = new ArrayList<Composition>();
    Composition el = new Composition(this);
    _collection.add(el);    return el;
  }
  public ArrayList<Composition> get_collection() {
    return this._collection;
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
    else if (E_collection.equals(name)) return add_collection();
    else if (E_reference.equals(name)) return add_reference();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
  private Reference container;
  public Reference getContainer() {
	return container;
  }
  public void setContainer(ObjectType container) {
	  try {
		  this.container = new Reference(this);
		  this.container.add_datatype().add_vodml_ref().text(container.getVodmlref());
	  } catch(Exception e)
	  {
		  // swallow
	  }
  }
}
