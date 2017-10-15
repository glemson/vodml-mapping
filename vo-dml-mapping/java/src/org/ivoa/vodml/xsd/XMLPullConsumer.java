package org.ivoa.vodml.xsd;

import org.xmlpull.v1.XmlPullParser;

/**
 * 
 * Inspired by SAVOT to use XmlPullParser, but completely reworked.
 * @author gerard
 *
 */
public interface XMLPullConsumer {

    // start elements
	public abstract void startElement(XmlPullParser parser) throws XMLParsingException;
	// end elements
	public abstract void endElement(XmlPullParser parser) throws XMLParsingException;
    // TEXT
    public abstract void text(String text) throws XMLParsingException;

        // document
    public abstract void startDocument() throws XMLParsingException;

    public abstract void endDocument() throws XMLParsingException;
}
