package org.ivoa.vodml.xml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.Stack;

import org.ivoa.vodml.xsd.XMLPullParser;
import org.ivoa.vodml.xsd.XMLParsingException;
import org.ivoa.vodml.xsd.XMLElement;
import org.ivoa.vodml.xsd.XMLPullConsumer;
import org.xmlpull.v1.XmlPullParser;

public class VODMLParser  implements XMLPullConsumer  {

	/** root element name */
	public static final String MODEL = "model";
	private int counter = 0;
	private Stack<XMLElement> stack = new Stack<XMLElement>();
	private XMLElement root = null;
	private boolean debug=false;
	
	public VODMLParser(boolean debug)
	{
		this.debug = debug;
	}
	public XMLElement current()
	{
		return stack.peek();
	}
	
	public void startElement(XmlPullParser parser) throws XMLParsingException {
		try {
			String name = parser.getName();
			name=name.substring(name.indexOf(":")+1);
			String ns =	parser.getNamespace();
			
			XMLElement next = current().addElement(name);
			stack.push(next);
			if(parser.getAttributeCount() > 0)
				next.setAttributes(parser);
			if(debug) log(String.format("[LINE %d]: new element %s", parser.getLineNumber(),parser.getName()));

			
		} catch(XMLParsingException ex)
		{
			log(String.format("[LINE %d]: %s", 
					parser.getLineNumber(),ex.getMessage()));
		}
	}
	

	private void log(String message)
	{
		System.out.println(message);
	}
	public void endElement(XmlPullParser parser) {
		stack.pop();
	}
	
	@Override
	public void text(String text) throws XMLParsingException {
		if(text != null && text.trim().length() > 0)
			current().text(text);
	}

	@Override
	public void startDocument() throws XMLParsingException {
		root = new VODMLRootElement();
		stack.push(root);
	}

	@Override
	public void endDocument() throws XMLParsingException {
		// TODO some checks here?
		XMLElement last = stack.pop();
		if(last != root)
			throw new XMLParsingException("internal error, last on stack is not the root");
	}
	
	public static void main(String[] args) throws Exception
	{
		VODMLParser consumer = new VODMLParser(true);
		String file = "C:/workspaces/eclipse-luna-4.4.1/vo-urp-etc/vo-dml/models/source/SourceDM.vo-dml.xml";
		Model model = consumer.parseFile(file);
		if(model != null)
			System.out.printf("Model name = %s\n",model.get_name());
	}
	
	
	public Model parse(InputStream in ) throws Exception{
		new XMLPullParser(this, in);
		return getModel();
	}
	public Model parseFile(String file) throws Exception{
		return parse(new FileInputStream(file));
	}
	public Model parseURL(URL url) throws Exception{
		return parse(url.openStream());
	}

	private Model getModel()
	{
		return (root != null?((VODMLRootElement)root).getModel():null);
	}

}
