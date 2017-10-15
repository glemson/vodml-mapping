package org.ivoa.vodml.xsd;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import org.kxml2.io.KXmlParser;
import org.xmlpull.v1.XmlPullParser;

/**
 * Inspired by SAVOT;s use of XmlPullParser, but rewritten and generalized.
 * @author gerard
 *
 */
public final class XMLPullParser {
    private XmlPullParser parser = null;
    private XMLPullConsumer consumer = null;

    public XMLPullParser(XMLPullConsumer consumer)  {
      this.parser = new KXmlParser();
      this.consumer = consumer;
    }
    public XMLPullParser(XMLPullConsumer consumer, String file) throws Exception {
    	this(consumer);
        parse(file);
    }
    public XMLPullParser(XMLPullConsumer consumer, URL url) throws Exception {
    	this(consumer);
    	InputStream in = url.openStream();
        parse(in);
    	in.close();
    }
    /**
     * 
     * @param consumer
     * @param file
     * @param debug
     */
    public XMLPullParser(XMLPullConsumer consumer, InputStream in) throws Exception {
    	this(consumer);
        parse(in);
    }


    /**
     * Copied from SAVOT's use of pull parser but much simplified.
     * No VOTable specific code at all, everything is delegated to consumer.
     * @param file
     * @throws IOException
     */
	public void parse(String file) throws IOException {
		InputStream in = new FileInputStream(file);
		parse(in);
	}
	public void parse(InputStream in) throws IOException {

		try {
			parser.setInput(new InputStreamReader(in));
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case KXmlParser.START_TAG:
					try {
						consumer.startElement(parser); // VOTABLE
					} catch (Exception e) {
						System.err.println("Exception START_TAG : " + e
								+ " at line " + parser.getLineNumber());
					}
					break;
				case KXmlParser.END_TAG:
					try {
						consumer.endElement(parser);
					} catch (Exception e) {
						System.err.println("Exception END_TAG : " + e
								+ " at line " + parser.getLineNumber());
					}
					break;
				case KXmlParser.END_DOCUMENT:
					try {
						consumer.endDocument();
					} catch (Exception e) {
						System.err.println("Exception END_DOCUMENT : " + e
								+ " at line " + parser.getLineNumber());
					}
					break;

				case KXmlParser.TEXT:
					try {
						consumer.text((parser.getText()).trim());
					} catch (Exception e) {
						System.err.printf("Exception TEXT : %s at line %d\n",e,parser.getLineNumber());
					}
					break;

				case KXmlParser.START_DOCUMENT:
					try {
						consumer.startDocument();
					} catch (Exception e) {
						System.err.printf("Exception START_DOCUMENT : %s at line %d",e,parser.getLineNumber());
					}
					break;

				default:
					System.err.printf("Unkown event encuntered at line number %d\n", " at line " + parser.getLineNumber());
				}
				eventType = parser.next();
			}
		} catch (Exception f) {
			System.err.println("Exception parse : " + f + " at line "
					+ parser.getLineNumber());
		}
		try {
			consumer.endDocument();
		} catch (Exception e) {
			System.err.println("Exception END_DOCUMENT : " + e + " at line "
					+ parser.getLineNumber());
		}
	}
}
