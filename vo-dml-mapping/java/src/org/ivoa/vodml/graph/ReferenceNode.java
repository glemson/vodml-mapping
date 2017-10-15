package org.ivoa.vodml.graph;

import org.ivoa.vodml.xsd.jaxb.Reference;
import org.ivoa.vodml.xsd.jaxb.Role;


public class ReferenceNode extends RelationNode {

	public ReferenceNode(Reference _e, TypeNode _owner, ModelGraph _vodml) {
		super(_e, _owner, _vodml);
	}

}
