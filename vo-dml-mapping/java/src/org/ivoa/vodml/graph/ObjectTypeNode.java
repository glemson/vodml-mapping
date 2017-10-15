package org.ivoa.vodml.graph;

import java.util.ArrayList;
import java.util.Iterator;

import org.ivoa.vodml.VODMLREF;
import org.ivoa.vodml.xsd.jaxb.Attribute;
import org.ivoa.vodml.xsd.jaxb.Composition;
import org.ivoa.vodml.xsd.jaxb.ElementRef;
import org.ivoa.vodml.xsd.jaxb.ObjectType;
import org.ivoa.vodml.xsd.jaxb.Reference;
import org.ivoa.votable.jaxb.VODMLObject;

/**
 * Represents an VODML:ObjectType in the model graph.
 * @author gerard
 *
 */
public class ObjectTypeNode extends StructuredTypeNode {

	private ArrayList<CompositionNode> compositions;
	private ContainerNode container;
	public ObjectTypeNode(ObjectType t, ModelGraph vodml) {
		super(t, vodml);
		compositions = new ArrayList<CompositionNode>();
		addRoles();
	}

	public ObjectType getObjectType()
	{
		return (ObjectType)getElement();
	}
	@Override
	protected void addRoles()
	{
		super.addRoles();
		ObjectType t = getObjectType();
		for(Attribute a: t.getAttribute())
			addAttribute(a);
		for(Reference r: t.getReference())
			addReference(r);
		for(Composition c: t.getComposition())
			addComposition(c);
	}
	protected void addComposition(Composition a)
	{
		CompositionNode node = new CompositionNode(a, this, getModelGraph());
		compositions.add(node);
		addRole(node);
	}
	protected void setContainer(CompositionNode c)
	{
		if(container != null) throw new IllegalStateException("Cannot overwrite an existing container node");
		ObjectTypeNode parent = c.getParent();
		ContainerNode ref = new ContainerNode(c.getComposition(), parent, this.getModelGraph());
		this.container = ref;
		addRole(this.container);
	}
	public ContainerNode getContainer() {
		return container;
	}

	protected Iterator<CompositionNode> getCompositions() {
		return compositions.iterator();
	}
	/**
	 * return utype for direct child element with specified name of this model.<br/>
	 * @param name
	 * @return
	 */
	@Override
	public String childForName(String name)
	{
		ObjectType ot = getObjectType();
		if(name == null)
			throw new IllegalArgumentException("Can not ask for element with name that is 'null'");
		for(Attribute e: ot.getAttribute())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		for(Reference e: ot.getReference())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		for(Composition e: ot.getComposition())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());

		return null;
	}
	
	public boolean hasContainer(){
		if(getContainer() !=null)
			return true;
		else if (getSuperType() != null)
			return ((ObjectTypeNode)getSuperType()).hasContainer();
		else 
			return false;
	}

	@Override
	public void resolveRoles() {
		super.resolveRoles();
		
		for(CompositionNode c: this.compositions){
			ObjectTypeNode targetNode = (ObjectTypeNode)c.getDatatype();
			targetNode.setContainer(c);
		}
	}


}
