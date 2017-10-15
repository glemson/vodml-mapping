
package org.ivoa.vodml.xml;

import org.ivoa.vodml.xml.gen.DataType;
import org.ivoa.vodml.xml.gen.Enumeration;
import org.ivoa.vodml.xml.gen.ModelImport;
import org.ivoa.vodml.xml.gen.Package;
import org.ivoa.vodml.xml.gen.PrimitiveType;
import org.ivoa.vodml.xsd.*;

import java.util.ArrayList;
 
public class Model extends XMLElement
{
  public static final String E_name="name";
  public static final String E_description="description";
  public static final String E_title="title";
  public static final String E_author="author";
  public static final String E_version="version";
  public static final String E_previousVersion="previousVersion";
  public static final String E_lastModified="lastModified";
  public static final String E_import="import";
  public static final String E_primitiveType="primitiveType";
  public static final String E_enumeration="enumeration";
  public static final String E_dataType="dataType";
  public static final String E_objectType="objectType";
  public static final String E_package="package";
  private XMLTextElement _name;
  private XMLTextElement _description;
  private XMLTextElement _title;
  private ArrayList<XMLTextElement> _author;
  private XMLTextElement _version;
  private XMLTextElement _previousVersion;
  private XMLTextElement _lastModified;
  private ArrayList<ModelImport> _import;
  private ArrayList<PrimitiveType> _primitiveType;
  private ArrayList<Enumeration> _enumeration;
  private ArrayList<DataType> _dataType;
  private ArrayList<ObjectType> _objectType;
  private ArrayList<Package> _package;
  public Model(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_name() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._name = el;
    return this._name;
  }
  public XMLTextElement get_name() {
    return this._name;
  }
  public String get_name_value() {
    return (this._name == null?null:this._name.getValue());
  }
  public XMLTextElement add_description() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._description = el;
    return this._description;
  }
  public XMLTextElement get_description() {
    return this._description;
  }
  public String get_description_value() {
    return (this._description == null?null:this._description.getValue());
  }
  public XMLTextElement add_title() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._title = el;
    return this._title;
  }
  public XMLTextElement get_title() {
    return this._title;
  }
  public String get_title_value() {
    return (this._title == null?null:this._title.getValue());
  }
  public XMLTextElement add_author() throws XMLParsingException {
    if(_author == null)_author = new ArrayList<XMLTextElement>();
    XMLTextElement el = new XMLTextElement(this);
    _author.add(el);    return el;
  }
  public ArrayList<XMLTextElement> get_author() {
    return this._author;
  }
  public XMLTextElement add_version() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._version = el;
    return this._version;
  }
  public XMLTextElement get_version() {
    return this._version;
  }
  public String get_version_value() {
    return (this._version == null?null:this._version.getValue());
  }
  public XMLTextElement add_previousVersion() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._previousVersion = el;
    return this._previousVersion;
  }
  public XMLTextElement get_previousVersion() {
    return this._previousVersion;
  }
  public String get_previousVersion_value() {
    return (this._previousVersion == null?null:this._previousVersion.getValue());
  }
  public XMLTextElement add_lastModified() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._lastModified = el;
    return this._lastModified;
  }
  public XMLTextElement get_lastModified() {
    return this._lastModified;
  }
  public String get_lastModified_value() {
    return (this._lastModified == null?null:this._lastModified.getValue());
  }
  public ModelImport add_import() throws XMLParsingException {
    if(_import == null)_import = new ArrayList<ModelImport>();
    ModelImport el = new ModelImport(this);
    _import.add(el);    return el;
  }
  public ArrayList<ModelImport> get_import() {
    return this._import;
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
    if (E_name.equals(name)) return add_name();
    else if (E_description.equals(name)) return add_description();
    else if (E_title.equals(name)) return add_title();
    else if (E_author.equals(name)) return add_author();
    else if (E_version.equals(name)) return add_version();
    else if (E_previousVersion.equals(name)) return add_previousVersion();
    else if (E_lastModified.equals(name)) return add_lastModified();
    else if (E_import.equals(name)) return add_import();
    else if (E_primitiveType.equals(name)) return add_primitiveType();
    else if (E_enumeration.equals(name)) return add_enumeration();
    else if (E_dataType.equals(name)) return add_dataType();
    else if (E_objectType.equals(name)) return add_objectType();
    else if (E_package.equals(name)) return add_package();
    else return super.addElement(name, xsiType);
  }


  /**    Put all hand modifications below this line */
  public String getName(){
	  return (_name != null?_name.getValue():null);
  }
}
