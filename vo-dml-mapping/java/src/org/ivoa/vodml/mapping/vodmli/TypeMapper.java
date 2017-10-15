package org.ivoa.vodml.mapping.vodmli;

import org.ivoa.vodml.VODMLREF;
import org.ivoa.vodml.VODMLManager;
import org.ivoa.vodml.graph.DataTypeNode;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.graph.TypeNode;
import org.ivoa.vodml.mapping.jaxb.MappedModels;
import org.ivoa.vodml.mapping.jaxb.ModelMapping;
import org.ivoa.vodml.model.DataTypeInstance;
import org.ivoa.vodml.model.ModelFactory;
import org.ivoa.vodml.model.ObjectTypeInstance;
import org.ivoa.vodml.xsd.jaxb.DataType;
import org.ivoa.vodml.xsd.jaxb.Enumeration;
import org.ivoa.vodml.xsd.jaxb.ObjectType;
import org.ivoa.vodml.xsd.jaxb.Type;


public class TypeMapper {

	private MappedModels mappedModels;
	private VODMLManager vodml;
	public TypeMapper(MappedModels mm, VODMLManager _vodml)
	{
		this.mappedModels = mm;
		this.vodml = _vodml;
	}
	/** 
	 * for a string representation, return a value of type corresponding to the type with the given utype.<br/>  
	 * In case the type corresponds to a structured type, the value is ignored!
	 * 
	 * @param sv
	 * @param ivoaUtype
	 * @return
	 */
	public Object map(String sv, String utype)
	{
		TypeNode tn = vodml.findType(utype);
		if(tn == null)
			return null;
		ModelFactory mf = getModelFactory(utype);
		if(mf == null)
			return null;
		Type t = tn.getType();
		if(t instanceof DataType)
			return (DataTypeInstance)mf.newStructuredObject(utype);
		else if(t instanceof ObjectType)
			return (ObjectTypeInstance)mf.newStructuredObject(utype); 
		else if(t instanceof Enumeration)
			return mf.newEnumeratedValue(utype, sv);
		else // if(t instanceof PrimitiveType)
			return mf.newPrimitiveValue(utype, sv);
	}
	
	public ModelMapping getModelMapping(String utype)
	{
		String modelUtype = VODMLREF.getPrefix(utype);
		for(ModelMapping mm : mappedModels.getModel())
		{
			if(modelUtype.equals(mm.getName()))
				return mm;
		}
		return null;
	}
	/**
	 * Return ModelFactory responsible for the specified utype.<br/>
	 * @param utype
	 * @return
	 */
	public ModelFactory getModelFactory(String utype)
	{
		ModelMapping mm = getModelMapping(utype);
		if(mm != null)
		{
			String pack = mm.getJavaPackage();
			try {
				Class<?> mfc = Class.forName(pack+".ModelFactory");
				ModelFactory mf = (ModelFactory)mfc.newInstance();
				return mf;
			} catch(ClassNotFoundException e)
			{
				System.out.printf("Can not find ModelFactory for model name '%s'\n", mm.getName());
			} catch(IllegalAccessException e2)
			{
				System.out.printf("Cannot instantiate a ModelFactory for model name '%s'\n", mm.getName());
			} catch(InstantiationException e2)
			{
				System.out.printf("Cannot instantiate a ModelFactory for model name '%s'\n", mm.getName());
			}
		}
		return null;
	}
	public DataTypeInstance newDataTypeInstance(DataTypeNode on)
	{
		ModelFactory mf = getModelFactory(on.getVODMLREF());
		if(mf == null)
			return null;
		return (DataTypeInstance)mf.newStructuredObject(on.getVODMLREF());
	}
	public DataTypeInstance newDataTypeInstance(String utype)
	{
		ModelFactory mf = getModelFactory(utype);
		if(mf == null)
			return null;
		return (DataTypeInstance)mf.newStructuredObject(utype);
	}
	public ObjectTypeInstance newObjectTypeInstance(ObjectTypeNode on)
	{
		ModelFactory mf = getModelFactory(on.getVODMLREF());
		if(mf == null)
			return null;
		return (ObjectTypeInstance)mf.newStructuredObject(on.getVODMLREF());
	}
	public ObjectTypeInstance newObjectTypeInstance(String utype)
	{
		ModelFactory mf = getModelFactory(utype);
		if(mf == null)
			return null;
		return (ObjectTypeInstance)mf.newStructuredObject(utype);
	}
	
	/**
	 * Translate the string into a value appropriate for the specified type.<br/> 
	 * @param type
	 * @param value
	 */
	public Object getValueFor(TypeNode type, String value)
	{
		return value;
	}
	
}
