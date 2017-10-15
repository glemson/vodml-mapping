package org.ivoa.vodml.votable;

import java.io.Writer;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class VOTableMetadataHandler extends EchoHandler {

	public VOTableMetadataHandler(Writer out) {
		super(out);
	}

	private boolean inData = false;

	@Override
	public void characters(char[] chars, int start, int length)
			throws SAXException {
		if (inData)
			return;
		super.characters(chars, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if (!inData) {
			super.endElement(uri, localName,qName);
		}
		if (qName.equals("DATA"))
			inData = false;
	}

	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException{
		if (qName.equals("DATA"))
			inData = true;
		if (!inData) {
			super.startElement(uri, localName, qName, attributes);
		}
	}

}
