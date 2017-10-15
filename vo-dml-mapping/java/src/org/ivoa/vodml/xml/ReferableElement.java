
package org.ivoa.vodml.xml;

import org.ivoa.vodml.xsd.*;


public abstract class ReferableElement extends XMLElement
{
  public static final String E_vodml_id="vodml-id";
  public static final String E_name="name";
  public static final String E_description="description";
  public static final String A_id="id";
  private XMLTextElement _vodml_id;
  private XMLTextElement _name;
  private XMLTextElement _description;
  private String _id;
  public ReferableElement(XMLElement _parent) throws XMLParsingException {
    super(_parent);
  }
  public XMLTextElement add_vodml_id() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._vodml_id = el;
    return this._vodml_id;
  }
  public XMLTextElement get_vodml_id() {
    return this._vodml_id;
  }
  public String get_vodml_id_value() {
    return (this._vodml_id == null?null:this._vodml_id.getValue());
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
  public void set_id(String _v) {
    this._id = _v;
  }
  public String get_id(){
     return this._id;
  }
  @Override
  public void addAttribute(String name, String value) throws XMLIllegalAttributeException {
    if (A_id.equals(name)) set_id(value);
    else super.addAttribute(name, value);
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_vodml_id.equals(name)) return add_vodml_id();
    else if (E_name.equals(name)) return add_name();
    else if (E_description.equals(name)) return add_description();
    else return super.addElement(name, xsiType);
  }


  /**    Put all hand modifications below this line */
    public String getModelPrefix()
    {
 	   if(this.getParent() instanceof Model)
 		   return ((Model)getParent()).getName();
 	   else if(this.getParent() instanceof ReferableElement)
 		   return ((ReferableElement)getParent()).getModelPrefix();
 	   else 
 		   return null;
    }
    public String getVodmlref(){
 	   return String.format("%s:%s",getModelPrefix(), get_vodml_id_value());
    }
    
}
