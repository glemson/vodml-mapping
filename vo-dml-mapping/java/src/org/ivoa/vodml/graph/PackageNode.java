package org.ivoa.vodml.graph;

import org.ivoa.vodml.VODMLREF;
import org.ivoa.vodml.xsd.jaxb.DataType;
import org.ivoa.vodml.xsd.jaxb.Enumeration;
import org.ivoa.vodml.xsd.jaxb.Model;
import org.ivoa.vodml.xsd.jaxb.ObjectType;
import org.ivoa.vodml.xsd.jaxb.Package;
import org.ivoa.vodml.xsd.jaxb.PrimitiveType;

public class PackageNode extends ElementNode {

	public PackageNode(org.ivoa.vodml.xsd.jaxb.Package _e, ModelGraph _vodml) {
		super(_e, _vodml);
	}

	public org.ivoa.vodml.xsd.jaxb.Package getPackage()
	{
		return (org.ivoa.vodml.xsd.jaxb.Package)getElement();
	}
	/**
	 * return utype for direct child element with specified name of this model.<br/>
	 * @param name
	 * @return
	 */
	@Override
	public String childForName(String name)
	{
		Package p = getPackage();
		if(name == null)
			throw new IllegalArgumentException("Can not ask for element with name that is 'null'");
		for(Package e : p.getPackage())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		for(PrimitiveType e: p.getPrimitiveType())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		for(Enumeration e: p.getEnumeration())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		for(DataType e: p.getDataType())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		for(ObjectType e: p.getObjectType())
			if(name.equals(e.getName())) 
				return VODMLREF.vodmlrefFor(e, getModel());
		return null;
	}
}
