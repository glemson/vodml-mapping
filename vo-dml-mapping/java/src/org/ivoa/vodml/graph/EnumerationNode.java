package org.ivoa.vodml.graph;

import java.util.ArrayList;

import org.ivoa.vodml.xsd.jaxb.EnumLiteral;
import org.ivoa.vodml.xsd.jaxb.Enumeration;
import org.ivoa.votable.jaxb.VODMLInstance;

public class EnumerationNode extends TypeNode {

	public ArrayList<LiteralNode> literals;
	public EnumerationNode(Enumeration _e, ModelGraph _vodml) {
		super(_e, _vodml);
		this.literals = new ArrayList<LiteralNode>();
		addLiterals();
	}
	public Enumeration getEnum()
	{
		return (Enumeration)getElement();
	}
	private void addLiterals()
	{
		for(EnumLiteral el: getEnum().getLiteral())
		{
			literals.add(new LiteralNode(el, this, getModelGraph()));
		}
	}

}
