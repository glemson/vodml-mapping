package org.ivoa.vodml.xml;

import org.ivoa.vodml.xsd.XMLElement;
import org.ivoa.vodml.xsd.XMLParsingException;

public class VODMLRootElement extends XMLElement {
	public static final String E_model = "model";
	private Model model;

	public VODMLRootElement() throws XMLParsingException {
		super(null);
	}

	@Override
	public XMLElement addElement(String name, String xsiType)
			throws XMLParsingException {
		if (E_model.equals(name))
			return setModel();
		else
			return super.addElement(name, xsiType);
	}

	public Model getModel() {
		return model;
	}
	protected Model setModel() throws XMLParsingException{
		this.model = new Model(this);
		return this.model;
	}

}
