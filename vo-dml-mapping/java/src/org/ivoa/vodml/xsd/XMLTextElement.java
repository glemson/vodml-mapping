package org.ivoa.vodml.xsd;


public class XMLTextElement extends XMLElement {

	private String _text;
	public XMLTextElement(XMLElement _parent)
			throws XMLParsingException {
		super(_parent);
	}
	@Override
	public void text(String _value){
		this._text = _value;
	}
	public String getValue() {
		return _text;
	}
}
