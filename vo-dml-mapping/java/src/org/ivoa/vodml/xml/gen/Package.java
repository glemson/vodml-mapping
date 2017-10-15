
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xml.ObjectType;
import org.ivoa.vodml.xml.ReferableElement;
import org.ivoa.vodml.xsd.*;

import java.util.ArrayList;
 
public class Package extends ReferableElement
{
  public static final String E_primitiveType="primitiveType";
  public static final String E_enumeration="enumeration";
  public static final String E_dataType="dataType";
  public static final String E_objectType="objectType";
  public static final String E_package="package";
  private ArrayList<PrimitiveType> _primitiveType;
  private ArrayList<Enumeration> _enumeration;
  private ArrayList<DataType> _dataType;
  private ArrayList<ObjectType> _objectType;
  private ArrayList<Package> _package;
  public Package(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public PrimitiveType add_primitiveType() throws XMLParsingException {
    if(_primitiveType == null)_primitiveType = new ArrayList<PrimitiveType>();
    PrimitiveType el = new PrimitiveType(this);
    _primitiveType.add(el);    return el;
  }
  public ArrayList<PrimitiveType> get_primitiveType() {
    return this._primitiveType;
  }
  public Enumeration add_enumeration() throws XMLParsingException {
    if(_enumeration == null)_enumeration = new ArrayList<Enumeration>();
    Enumeration el = new Enumeration(this);
    _enumeration.add(el);    return el;
  }
  public ArrayList<Enumeration> get_enumeration() {
    return this._enumeration;
  }
  public DataType add_dataType() throws XMLParsingException {
    if(_dataType == null)_dataType = new ArrayList<DataType>();
    DataType el = new DataType(this);
    _dataType.add(el);    return el;
  }
  public ArrayList<DataType> get_dataType() {
    return this._dataType;
  }
  public ObjectType add_objectType() throws XMLParsingException {
    if(_objectType == null)_objectType = new ArrayList<ObjectType>();
    ObjectType el = new ObjectType(this);
    _objectType.add(el);    return el;
  }
  public ArrayList<ObjectType> get_objectType() {
    return this._objectType;
  }
  public Package add_package() throws XMLParsingException {
    if(_package == null)_package = new ArrayList<Package>();
    Package el = new Package(this);
    _package.add(el);    return el;
  }
  public ArrayList<Package> get_package() {
    return this._package;
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_primitiveType.equals(name)) return add_primitiveType();
    else if (E_enumeration.equals(name)) return add_enumeration();
    else if (E_dataType.equals(name)) return add_dataType();
    else if (E_objectType.equals(name)) return add_objectType();
    else if (E_package.equals(name)) return add_package();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
