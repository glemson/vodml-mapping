package org.ivoa.vodml.graph;

import org.ivoa.vodml.xsd.jaxb.Composition;


public class ContainerNode extends RelationNode {

	public ContainerNode(Composition _e, ObjectTypeNode _parentType, ModelGraph _vodml) {
		super(_e, _parentType, _vodml);
	}
	
	public Composition getComposition() 
	{
		return (Composition)getElement();
	}
	public ObjectTypeNode getParentObjectType()
	{
		return (ObjectTypeNode)getOwner();
	}


}
