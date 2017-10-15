package org.ivoa.vodml.mapping.vodmli;

import java.util.ArrayList;

import org.ivoa.dm.ivoa.IntegerQuantity;
import org.ivoa.dm.ivoa.Quantity;
import org.ivoa.dm.ivoa.RealQuantity;
import org.ivoa.dm.ivoa.Unit;
import org.ivoa.vodml.mapping.VOTABLEGraph;
import org.ivoa.vodml.mapping.VOTableElement;
import org.ivoa.vodml.mapping.vodmli.GroupID;
import org.ivoa.vodml.mapping.vodmli.ObjectTypeCollection;
import org.ivoa.vodml.mapping.vodmli.TypeMapper;
import org.ivoa.vodml.mapping.vodmli.VODMLIManager;
import org.ivoa.vodml.VODMLREF;
import org.ivoa.vodml.graph.AttributeNode;
import org.ivoa.vodml.graph.CompositionNode;
import org.ivoa.vodml.graph.DataTypeNode;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.graph.ReferenceNode;
import org.ivoa.vodml.graph.RoleNode;
import org.ivoa.vodml.graph.TypeNode;
import org.ivoa.vodml.model.DataTypeInstance;
import org.ivoa.vodml.model.Identifier;
import org.ivoa.vodml.model.ObjectTypeInstance;
import org.ivoa.vodml.model.ReferenceObject;
import org.ivoa.vodml.model.StringsIdentifier;
import org.ivoa.vodml.model.StructuredObject;
import org.ivoa.vodml.votable.graph.FieldNode;
import org.ivoa.vodml.votable.graph.FieldRefNode;
import org.ivoa.vodml.votable.graph.GroupNode;
import org.ivoa.vodml.votable.graph.ParamNode;
import org.ivoa.vodml.votable.graph.ParamRefNode;
import org.ivoa.vodml.votable.graph.TableNode;
import org.ivoa.vodml.xsd.jaxb.Type;
import org.ivoa.votable.jaxb.Data;
import org.ivoa.votable.jaxb.Table;
import org.ivoa.votable.jaxb.TableData;
import org.ivoa.votable.jaxb.Tr;
import org.ivoa.votable.jaxb.VODMLObject;


/**
 * For a given GROUP holds on to objects of corresponding type
 * @author GerardLemson
 *
 */
public class ObjectTypeCollection {

	// the VO-DML:ObjectType represented by this collection
	private ObjectTypeNode vodmlObjectType;

	/**
	 * List of INSTANCE-s that are referenced from the VODMLObject representing this collection.<br/>
	 */
	private ArrayList<VODMLObject> vot_objects;

	private VODMLIManager vodmli;
	private TypeMapper typeMapper;
	/** status variable indicating if a collection was filled already or not */
	private boolean fillStarted = false, isFilled = false;
	
	public ObjectTypeCollection(ObjectTypeNode otn, VODMLIManager _vodmli)
	{
		this.vodmli=_vodmli;
		this.typeMapper = vodmli.getMapper();
		this.vodmlObjectType = otn;
		this.vot_objects = new ArrayList<VODMLObject>();
	}	
	public ObjectTypeNode getObjectType() {
		return vodmlObjectType;
	}
	
	public void fill(VOTABLEGraph votg)
	{
		fillStarted = true;
		if(group.isDirect())
			fillDirect(votg);
		else
			fillIndirect(votg);
		isFilled = true;
	}
	private void fillDirect(VOTABLEGraph votg)
	{
		ObjectTypeInstance o = newObjectTypeInstance(group, null, null);
		if(group.getGROUP().getID() != null)
			o.set_altID(new GroupID(group.getGROUP().getID()));
		if(o != null)
			add(o);
	}
	
	private void fillIndirect(VOTABLEGraph votg)
	{
		TableNode tn = group.getTable();
		Table t = tn.getTable();
		Data data = TableNode.getDATA(t);
		if(data != null && data.getTABLEDATA() != null)
		{
			TableData tdata = data.getTABLEDATA();
			for(Tr tr : tdata.getTRS())
			{
				ObjectTypeInstance o = newObjectTypeInstance(group, tr, null);
				if(o != null)
					add(o);
			}
		}
	}
	
	public boolean add(VODMLObject o){
		o.getDmtype()
	}
	
	private ObjectTypeInstance newObjectTypeInstance(GroupNode gn, Tr tr, ObjectTypeInstance container)
	{
		ObjectTypeInstance o = typeMapper.newObjectTypeInstance(gn.getVODMLType().getVODMLREF());
		if(o == null)
			return null; // TODO throw exception

		if(container != null)
		{
			o.setContainer(container);
			if(gn.getVODMLRole() != null)
				container.add2Collection(gn.getVODMLRole().getVODMLREF(), o);
		}

		for(int i = 0; i < gn.getChildCount(); i++)
		{
			VOTableElement el = gn.getChild(i);
			setRole(o, el, tr);
		}
		vodmli.addObject(o);
		return o;
	}
	private DataTypeInstance newDataTypeInstance(GroupNode gn, Tr tr)
	{
		DataTypeInstance o = typeMapper.newDataTypeInstance(gn.getVODMLType().getVODMLREF());
		if(o == null)
			return null; // TODO throw exception
		for(int i = 0; i < gn.getChildCount(); i++){
			VOTableElement el = gn.getChild(i);
			setRole(o, el, tr);
		}
		return o;
	}
	private DataTypeInstance newDataTypeInstance(ParamNode pn, Tr tr)
	{
		String utype = pn.getVODMLType().getVODMLREF();
		DataTypeInstance o = typeMapper.newDataTypeInstance(utype);
		if(o == null)
			return null; // TODO throw exception
		if(o instanceof Quantity)
		{
			if(pn.getUnit() != null)
				((Quantity)o).setUnit(new Unit(pn.getUnit()));
			if(o instanceof RealQuantity)
				((RealQuantity)o).setValue(Double.parseDouble(pn.getValue()));
			else if(o instanceof IntegerQuantity)
				((IntegerQuantity)o).setValue(Integer.parseInt(pn.getValue()));
		} 
		return o;
	}
	private DataTypeInstance newDataTypeInstance(FieldNode fn, Tr tr)
	{
		String utype = fn.getVODMLType().getVODMLREF();
		DataTypeInstance o = typeMapper.newDataTypeInstance(utype);
		if(o == null)
			return null; // TODO throw exception
		if(o instanceof Quantity)
		{
			if(fn.getUnit() != null)
				((Quantity)o).setUnit(new Unit(fn.getUnit()));
			if(o instanceof RealQuantity)
				((RealQuantity)o).setValue(Double.parseDouble(getValue(fn, tr)));
			else if(o instanceof IntegerQuantity)
				((IntegerQuantity)o).setValue(Integer.parseInt(getValue(fn, tr)));
		} 
		return o;
	}
	private StringsIdentifier newID(VOTableElement el, Tr tr)
	{
		StringsIdentifier id = new StringsIdentifier();
		if(el instanceof GroupNode)
		{
			GroupNode gn = (GroupNode)el;
			for(int i = 0; i < gn.getChildCount(); i++){
				VOTableElement ch = gn.getChild(i);
				String v = getValue(ch, tr);
				id.add(v != null?v:"");
			}
		} else { // TODO invalid?
			String v = getValue(el,tr);
			id.add(v != null?v:"");
		}
		return id;
	}	
	private ReferenceObject newReferenceObject(GroupNode ref, Tr tr)
	{
		String utype = ref.getVODMLType().getVODMLREF();
		ObjectTypeInstance o = typeMapper.newObjectTypeInstance(utype);
		StringsIdentifier id = newID(ref, tr);
		return new ReferenceObject(ref.getVODMLRole().getVODMLREF(), ref.getGroupRef().getVODMLType().getVODMLREF(), id);
	}
	
	private String getValue(VOTableElement el, Tr tr)
	{
		if(el instanceof ParamNode)
			return ((ParamNode)el).getValue();
		else if(el instanceof ParamRefNode)
			return ((ParamRefNode)el).getValue();
		else if(el instanceof FieldRefNode)
			return ((FieldRefNode)el).getValue(tr);
		else // other element, GroupNode for example
			return null;
	}
	
	
	/**
	 * 
	 * @param parent
	 * @param el
	 * @param tr may be null!
	 */
	private void setRole(StructuredObject parent, VOTableElement el, Tr tr)
	{
		RoleNode role = el.getVODMLRole();
		if(role == null)
			throw new IllegalArgumentException(String.format("Should have found a role for  '%s'", el.getVODML())); 
		String utype = role.getVODMLREF();
		if(VODMLREF.isObjectID(utype)) // parent should be an ObjectTypeInstance
			((ObjectTypeInstance)parent).set_publisherDID(newID(el, tr));
		else if(role instanceof AttributeNode)
		{
			if(el instanceof GroupNode) // type should be a datatype
				parent.setProperty(role.getVODMLREF(), newDataTypeInstance((GroupNode)el, tr));
			else 
			{
				TypeNode dt = role.getDatatype();
				if(dt instanceof DataTypeNode)
				{
					if(el instanceof ParamNode)
						parent.setProperty(role.getVODMLREF(), newDataTypeInstance((ParamNode)el, tr));
					else if(el instanceof FieldRefNode)
						parent.setProperty(role.getVODMLREF(), newDataTypeInstance((FieldRefNode)el, tr));
					else if(el instanceof FieldNode)
						parent.setProperty(role.getVODMLREF(), newDataTypeInstance((FieldNode)el, tr));
				}
				else {
					String v = getValue(el, tr);
					if(v != null)
						parent.setProperty(role.getVODMLREF(), v);
				}
			}
		}/*
		else if(VODMLREF.isContainer(role.getVODMLREF()))
		{
			GroupNode cg = (GroupNode)el; // the group doing the referencing
			GroupNode target = cg.getGroupRef(); 
			ObjectTypeCollection oc = vodmli.getOC(target);
			if(oc == null)
			{
				System.out.printf("Unable to find object type collection for target of Container");
				return;
			}
			if(target.isDirect() && oc.isFilled)
				parent.setProperty(role.getVODMLREF(), oc.get(0));
			else
				parent.setProperty(role.getVODMLREF(), newReferenceObject(cg, tr));
		}*/
		
		else if(role instanceof ReferenceNode)
		{
			GroupNode cg = (GroupNode)el; // the group doing the referencing
			GroupNode target = cg.getGroupRef(); 
			if(target != null)
			{
				ObjectTypeCollection oc = vodmli.getOC(target);
				if(oc == null)
				{
					System.out.printf("Unable to find object type collection for target of reference");
					return;
				}
				if(target.isDirect() && oc.isFilled)
					parent.setProperty(role.getVODMLREF(), oc.get(0));
				else // TODO could try searching already for objects in filled indirect collections 
					parent.setProperty(role.getVODMLREF(), newReferenceObject(cg, tr));
			} else {
				// TODO Rgister the reference as being search for object in all of document, to be done at end. REGISTR
			}
		} 
		else if(role instanceof CompositionNode)
		{
			ObjectTypeInstance container = (ObjectTypeInstance)parent;
			GroupNode cg = (GroupNode)el; // the group representing the collection
			TypeNode declaredType = el.getVODML().getTypeCast();
			GroupNode target = cg.getGroupRef(); 
			// decide, if:
			// 1. group represents the type (i.e. has a declared type): create the object, add to appropriate collection
			if(target == null && declaredType != null)
			{
//				container.add2Collection(utype, newObjectTypeInstance(cg, tr, container));
				newObjectTypeInstance(cg, tr, container); // this will add child object to container
			}
		}
	}
	public GroupNode getGroup() {
		return group;
	}

	public boolean isFilled() {
		return isFilled;
	}

	public ArrayList<GroupNode> getGroupRefs() {
		return groupRefs;
	}

	@Override
	public String toString()
	{
		return group.toString();
	}
	private DataTypeInstance newDataTypeInstance(FieldRefNode frn, Tr tr)
	{
		String utype = frn.getVODMLType().getVODMLREF();
		DataTypeInstance o = typeMapper.newDataTypeInstance(utype);
		if(o == null)
			return null; // TODO throw exception
		FieldNode fn = frn.getFieldNode();
		if(o instanceof Quantity)
		{
			if(fn.getUnit() != null)
				((Quantity)o).setUnit(new Unit(fn.getUnit()));
			if(o instanceof RealQuantity)
				((RealQuantity)o).setValue(Double.parseDouble(getValue(frn, tr)));
			else if(o instanceof IntegerQuantity)
				((IntegerQuantity)o).setValue(Integer.parseInt(getValue(frn, tr)));
		} 
		return o;
	}
}
