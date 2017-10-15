package org.ivoa.vodml.xml;

import java.util.ArrayList;
import java.util.Hashtable;

import org.ivoa.vodml.xml.gen.Composition;
import org.ivoa.vodml.xml.gen.DataType;
import org.ivoa.vodml.xml.gen.Enumeration;
import org.ivoa.vodml.xml.gen.PrimitiveType;

public class ModelGraph extends ArrayList<ObjectType>{
	
	private boolean NODES_INITALIZED = false;
	private boolean ROLES_RESOLVED = false;
	
	private String modelURL;
	private VODMLManager vodml;

	private Model model;
	private Hashtable<String, ReferableElement> elements;
	private Hashtable<String, Type> types;

	
	private Hashtable<String, ArrayList<Type>> subtypesForType;
	
	
	
	public ModelGraph(VODMLManager _models, Model _model, String _modelURL){
		this.vodml  = _models;
		this.model = _model;
		this.modelURL = _modelURL;
		this.elements = new Hashtable<String, ReferableElement>();
		this.types = new Hashtable<String, Type>();
	}

	public void initModel() {
		if(NODES_INITALIZED)
			return;
		for (ObjectType t : model.get_objectType())
			register(t);
		for (DataType t : model.get_dataType())
			register(t);
		for (PrimitiveType t : model.get_primitiveType())
			register(t);
		for (Enumeration t : model.get_enumeration())
			register(t);
		for (org.ivoa.vodml.xml.gen.Package p : model.get_package())
			initPackage(p);
		this.NODES_INITALIZED = true;
	}
	

	private void initPackage(org.ivoa.vodml.xml.gen.Package p) {
		register(p);
		for (ObjectType t : model.get_objectType())
			register(t);
		for (DataType t : model.get_dataType())
			register(t);
		for (PrimitiveType t : model.get_primitiveType())
			register(t);
		for (Enumeration t : model.get_enumeration())
			register(t);
		for (org.ivoa.vodml.xml.gen.Package cp : model.get_package())
			initPackage(cp);
	}

	public ReferableElement getElementForVODMLREF(String vodmlref)
	{
		return elements.get(vodmlref);
	}

	public void resolveRoles()
	{
		if(ROLES_RESOLVED)
			return;
		if(!NODES_INITALIZED)
			throw new IllegalStateException("Nodes must have beeen initializaed before resolving roles");
// what is to be done?
		for(Type t: types.values() )
		{
			if(t.get_extends() != null) {
				Type supertype = vodml.findType(t.get_extends().get_vodml_ref_value());
				
				supertype.addSubClass(t);
				t.setSuperType(supertype);
			}
			if(t instanceof ObjectType){
				ObjectType ot = (ObjectType)t;
				for(Composition c : ot.get_collection()){
					ObjectType ch = (ObjectType)vodml.findType(c.getDatatype()); // TODO assuming model correct ...
					ch.setContainer(ot);
				}
			}
		}
		ROLES_RESOLVED = true;
	}
	
	public Model getModel() {
		return model;
	}
	/**
	 * 
	 * @param role
	 * @return
	 */
	public Type getType(Role role)
	{
		// first check local
		String vodmlref = role.get_datatype().get_vodml_ref().getValue();
		Type t = types.get(vodmlref);
		if(t != null)
			return t;
		else // check VO-DML model manager
			return vodml.findType(vodmlref);
	}

	public VODMLManager getVodml() {
		return vodml;
	}
	public void register(ReferableElement el)
	{
		if(el != null)
		{
			String ref = el.getVodmlref();
			this.elements.put(ref, el);
			if(el instanceof Type)
			{
				types.put(ref, (Type)el);
				if(el instanceof ObjectType)
					add((ObjectType)el);
			}
		}
	}

	public String getModelURL() {
		return modelURL;
	}

}
