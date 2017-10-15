
package org.ivoa.vodml.xml.gen;

import org.ivoa.vodml.xsd.*;


public class ModelImport extends XMLElement
{
  public static final String E_name="name";
  public static final String E_version="version";
  public static final String E_url="url";
  public static final String E_documentationURL="documentationURL";
  private XMLTextElement _name;
  private XMLTextElement _version;
  private XMLTextElement _url;
  private XMLTextElement _documentationURL;
  public ModelImport(XMLElement _parent) throws XMLParsingException {
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
  public XMLTextElement add_url() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._url = el;
    return this._url;
  }
  public XMLTextElement get_url() {
    return this._url;
  }
  public String get_url_value() {
    return (this._url == null?null:this._url.getValue());
  }
  public XMLTextElement add_documentationURL() throws XMLParsingException {
    XMLTextElement el = new XMLTextElement(this);
    this._documentationURL = el;
    return this._documentationURL;
  }
  public XMLTextElement get_documentationURL() {
    return this._documentationURL;
  }
  public String get_documentationURL_value() {
    return (this._documentationURL == null?null:this._documentationURL.getValue());
  }
  @Override
  	public XMLElement addElement(String name,	String xsiType) throws XMLParsingException {
    if (E_name.equals(name)) return add_name();
    else if (E_version.equals(name)) return add_version();
    else if (E_url.equals(name)) return add_url();
    else if (E_documentationURL.equals(name)) return add_documentationURL();
    else return super.addElement(name, xsiType);
  }



 /**    Put all hand modifications below this line */
 
}
