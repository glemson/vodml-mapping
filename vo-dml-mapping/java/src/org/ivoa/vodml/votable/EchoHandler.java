package org.ivoa.vodml.votable;

import java.io.*;

import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;

public class EchoHandler extends DefaultHandler {
	private StringBuffer textBuffer;
	private Writer out;
	public EchoHandler(Writer out){
		this.out = out;
	}
	public static void main(String argv[])

	{
		if (argv.length != 1) {
			System.err.println("Usage: cmd filename");
			System.exit(1);
		}
        
		// Use an instance of ourselves as the SAX event handler
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			// Set up output stream
			Writer out = new OutputStreamWriter(System.out, "UTF8");
			DefaultHandler handler = new VOTableMetadataHandler(out);
			// Parse the input
			SAXParser saxParser = factory.newSAXParser();
			saxParser.parse(new File(argv[0]), handler);
		} catch (Throwable t) {
			t.printStackTrace();
		}

	}

	private void emit(String s) throws SAXException {
		try {
			out.write(s);
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	private void nl() throws SAXException {
		String lineEnd = System.getProperty("line.separator");
		try {
			out.write(lineEnd);
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	public void startDocument() throws SAXException {
		emit("<?xml version='1.0' encoding='UTF-8'?>");
		nl();
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		String s = new String(buf, offset, len);
		if (textBuffer == null) {
			textBuffer = new StringBuffer(s);
		} else {
			textBuffer.append(s);
		}
	}
	private void echoText()
			throws SAXException
			{
			  if (textBuffer == null) return;
			  String s = ""+textBuffer;
			  emit(s);
			  textBuffer = null;
			} 

	public void endDocument() throws SAXException {
		try {
			nl();
			out.flush();
		} catch (IOException e) {
			throw new SAXException("I/O error", e);
		}
	}

	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		echoText();
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware
		emit("<" + eName);
		if (attrs != null) {
			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getLocalName(i); // Attr name
				if ("".equals(aName))
					aName = attrs.getQName(i);
				emit(" ");
				emit(aName + "=\"" + attrs.getValue(i) + "\"");
			}
		}
		emit(">");
	}

	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) throws SAXException {
		echoText();
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware
		emit("</" + eName + ">");
	}

}
