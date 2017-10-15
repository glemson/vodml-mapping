package org.ivoa.vodml.xml;

import graphs.DirectedGraph;
import graphs.Node;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.ivoa.vodml.RemoteVODMLRegistry;
import org.ivoa.vodml.VODMLRegistry;
import org.ivoa.vodml.xml.gen.Attribute;
import org.ivoa.vodml.xml.gen.DataType;
import org.ivoa.vodml.xml.gen.ModelImport;
import org.ivoa.vodml.xml.gen.Reference;
import org.ivoa.vodml.xml.gen.Relation;



/**
 * Manages VO-DML models. Is able to retrieve them and manage each.
 * Build a single data structure for all required models.
 * @author GerardLemson
 *
 */
public class VODMLManager extends ArrayList<Model> 
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5963181083012981591L;

//	public static final String VODML_url = "http://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vo-dml/VO-DML.vo-dml.xml";

	private VODMLRegistry vodmlReg;
	private VODMLParser parser;
	
	/** models keyed by url, to quickly see whether a model has been loaded */
	private Hashtable<String,Model> models_url;
	/** models keyed by their utype */
	private Hashtable<String,Model> models_prefix;


	 /** 
	 * This should be an ivoa identifier, currently the Model's name is used. */
	private Hashtable<String,ModelGraph> modelgraphs;

	/** utypes keyed by (possibly alternative) prefixes*/
	private Hashtable<String,String> vodmlref_prefixes;
	/** truncated model urls keyed by utype */
	private Hashtable<String,String> ModelLocation_url;
	/** Graph of objectypes */
	private DirectedGraph<ObjectType, Role> objectTypeGraph; 


	public VODMLManager(String url) {
		this(new RemoteVODMLRegistry(), url);
	}
	public VODMLManager(VODMLRegistry reg, String url)
	{
		this.vodmlReg = reg; // TODO check it's not null
		ArrayList<ModelLocation> ms = new ArrayList<ModelLocation>();
		ModelLocation ml = new ModelLocation();
		ml.setModelURL(url);
		ms.add(ml);
		try {
			initialize(ms);
		} catch(Exception e)
		{
			System.out.println("Unable to load VO-DML models");
			e.printStackTrace();
		}

	}

	/**
	 * Constructor for a set of VO-DML data models.<br/>
	 * Will download all of the models and parse them to a JAXB object. 
	 * Will order the models based on their import relationship.
	 * Will build ModelGraphs in that order, so as to satisfy type relationships.
	 * @param _models
	 */
	public VODMLManager(VODMLRegistry reg, List<ModelLocation> _models) 
	{
		this.vodmlReg = reg;
		try {
			initialize(_models);
		} catch(Exception e)
		{
			System.out.println("Unable to load VO-DML models");
			e.printStackTrace();
		}
	}
	
	private void initialize(List<ModelLocation> _models) throws Exception
	{
		if(parser == null)
			parser = new VODMLParser(false);
		
		models_prefix = new Hashtable<String, Model>();
		models_url = new Hashtable<String, Model>();

		vodmlref_prefixes = new Hashtable<String, String>();
		ModelLocation_url = new Hashtable<String, String>();
		modelgraphs = new Hashtable<String, ModelGraph>();


//		loadModels(VODML_url, "vo-dml"); // must be after initialisation of data structures
		for(ModelLocation _m: _models)
			loadModels(_m.getModelURL(),_m.getModelName());
		
		buildGraphs();
		
	}

	/**
	 * Load model identified by URL and using given prefix
	 * TODO add check that same model will never be attempted to be loaded with different prefix, 
	 * or allow multiple prefixes to point to same model.
	 * @param url
	 * @param prefix
	 * @throws Exception
	 */
	private Model loadModel(String url) throws Exception
	{
		Model m = models_url.get(url);
		if(m != null) // already loaded
			return m;
		InputStream in = vodmlReg.openModel(url);
		if(in == null)
			return null;
		m = parser.parse(in);
		return m;
	}
	/**
	 * Load model identified by URL and using given prefix and all models it imports.
	 * TODO check for cycles.
	 * TODO add check that same model will never be attempted to be loaded with different prefix, 
	 * or allow multiple prefixes to point to same model.
	 * @param url
	 * @param prefix
	 * @throws Exception
	 */
	private void loadModels(String url, String prefix) throws Exception
	{
		Model m = models_url.get(url);
		if(m == null)
		{
			// check whether maybe model was loaded with different URL already.
			// base on Model's utype, though an (ivo-)identifier should have been preferable. 
			m = loadModel(url);
			if(m == null)
				throw new IllegalStateException("no model loaded for url = '"+url+"'");
			if(prefix == null) prefix = m.getName();
			Model exm = models_prefix.get(m.getName()); 
			if(exm != null)
				models_url.put(url, exm);
			else
			{
				models_url.put(url, m);
				models_prefix.put(m.getName(), m);
				for(ModelImport mp: m.get_import())
				{
					Model mi = models_url.get(mp.get_url());
					if(mi == null)
						loadModels(mp.get_url_value(), mp.get_name_value());
				}
				this.add(m); // AFTER the imported models have been added!
			}
			addUtypePrefix(prefix, prefix, url);
		}
		return ;
	}
	/**
	 * If the prefix yet exists for the given u
	 * @param utype
	 * @param prefix
	 * @return true if the utype is registered with the prefix, false otherwise. False might happen if the prefix already identifies another utype.
	 */
	private boolean addUtypePrefix(String utype, String prefix, String url)
	{
		if(prefix == null)
			prefix = utype;
		ModelLocation_url.put(utype,url);
		String otherutype = vodmlref_prefixes.get(prefix);
		if(otherutype == null)
		{
			vodmlref_prefixes.put(prefix, utype);
			return true;
		} else
			return otherutype.equals(utype);
		
	}

	/**
	 * Return the VO-DML ReferencableElement for the indicated utype.<br/>
	 * Currently a simple lookup in a single hastable, may be made more flexible by first looking for Model based on prefix,
	 * then in model ask for utype without prefix. Allows variable prefixes
	 * @param utype
	 * @return
	 */
	public ReferableElement findReferableElement(String vodmlref) {
		ModelGraph model = findModelForUtype(vodmlref);
		if(model == null)
			return null;
		return model.getElementForVODMLREF(vodmlref);
	}

	public ModelGraph findModelForUtypePrefix(String prefix) {
		return modelgraphs.get(prefix);
	}

	public ModelGraph findModelForUtype(String vodmlref) {
		String prefix = VODMLUtil.getPrefix(vodmlref);
		return findModelForUtypePrefix(prefix);
	}
	public ModelGraph findModelForURL(String url) {
		Model m = (url == null?null:models_url.get(url));
		return m == null?null:modelgraphs.get(m.getName());
	}
	
	public Type findType(String utype)
	{
		ReferableElement el = findReferableElement(utype);
		if(el instanceof Type)
			return (Type)el;
		else
			return null;
	}

	public Type findTypeForRole(Role role)
	{
		return findType(role.get_datatype().get_vodml_ref().getValue());
	}
	/**
	 * Check whether the specified type is a valid type for instances of the given role.
	 * I.e. it must be the datatype of the role or one of its subclasses
	 * @param role
	 * @param type
	 * @return
	 */
	public boolean areRoleTypeCompatible(Role role, Type type)
	{
		if(role == null || type == null)
			return true;
		if(role instanceof Relation && VODMLUtil.isIdentifier(type.getVodmlref()))
			return true;
		if(role instanceof Reference && VODMLUtil.isReference(type.getVodmlref()))
			return true;
		if(VODMLUtil.isTypeInstance(role.getVodmlref()))
			return true;
		if(VODMLUtil.isObjectTypeInstance(role.getVodmlref()) && type instanceof ObjectType)
			return true;
		if(VODMLUtil.isDataTypeInstance(role.getVodmlref()) && type instanceof DataType)
			return true;
		
		Type datatype = findTypeForRole(role);
		Type currentType = type;
		while(currentType != null)
		{
		    if(datatype == currentType)
		    	return true;
		    currentType = findType(currentType.get_extends().get_vodml_ref_value());
		}
		
			
		// special checks: container and reference are allowed to use vodml-map:Identifier as type!
		// TBD could put this on utypeattr, add the actual declared type of the role, but indicate 
		// it is indirectly referenced through a vodml-map:Identifier
    	return false;
	}
	
	public void buildGraphs()
	{
		this.objectTypeGraph = new DirectedGraph<ObjectType, Role>();
		// Should be able to treat models in order of links between objecttypes
		// edges built from dependencies as in relational mapping, i.e.following reference and container pointers.
		for(Model m: this)
		{
			ModelGraph mg = new ModelGraph(this,m, getModelUrl(m.getName()));
			modelgraphs.put(m.getName(), mg);
			mg.initModel();
			for(ObjectType ot: mg)
				objectTypeGraph.addNode(new Node<ObjectType>(ot.getVodmlref(), ot));
		}

		for(ModelGraph mg: this.modelgraphs.values())
			mg.resolveRoles();

		// add edges
		for(Node<ObjectType> node: objectTypeGraph.getNodes())
		{
			ObjectType ot = node.getObject();
			if(ot.getContainer() != null)
				addEdge(ot, ot.getContainer());
			Hashtable<String,Type> visited = new Hashtable<String, Type>();
			addEdges(node.getObject(), ot, visited);
		}
		objectTypeGraph.sort();
	}

	public String getModelUrl(String utype)
	{
		String prefix = VODMLUtil.getPrefix(utype);
		return ModelLocation_url.get(prefix);
	}
	private void addEdges(ObjectType fromNode, Type st, Hashtable<String,Type> visited)
	{
		ArrayList<Reference> refs = (st instanceof ObjectType?
				((ObjectType)st).get_reference():
					(st instanceof DataType?((DataType)st).get_reference(): null));
		if(refs != null){
			for(Reference ref : refs)
				addEdge(fromNode, ref);
		}
/*
 * TODO need to take care of cycles in datatypes here 
 * If a datatype with reference at some point   
 */
		ArrayList<Attribute> atts = (st instanceof ObjectType?
				((ObjectType)st).get_attribute():
					(st instanceof DataType?((DataType)st).get_attribute(): null));
		if(atts == null) return;
		for(Attribute att: atts)
		{
			Type at = findType(att.getDatatype());
			if(at instanceof DataType && visited.get(at.getVodmlref()) == null)
			{
				visited.put(at.getVodmlref(), at);
				addEdges(fromNode, (DataType)at, visited);
			}
		}
	}

	
	/** Add edge from fromNode to target of Role, and to all subclasses of the target */
	private void addEdge(ObjectType fromNode, Relation role)
	{
		addEdge(fromNode, role, (ObjectType)findType(role.getDatatype()));
	}
	/** Recursive! also add edge to/from all subtypes.<br/> */
	private void addEdge(ObjectType fromNode, Role role, ObjectType target)
	{
		objectTypeGraph.addEdge(fromNode.getVodmlref(), target.getVodmlref(), role);
		for(Type t : target.getSubclasses())
		{
			objectTypeGraph.addEdge(fromNode.getVodmlref(), t.getVodmlref(),role);
			for(Type f : fromNode.getSubclasses() )
				objectTypeGraph.addEdge(f.getVodmlref(), t.getVodmlref(),role);
		}
		for(Type f : fromNode.getSubclasses() )
			objectTypeGraph.addEdge(f.getVodmlref(), target.getVodmlref(),role);
	}
	
	/**
	 * Remove scheme from the URL.<br/>
	 * Poor man's way to ensure https and http access to same volute resource is recognized as being the same.
	 * Should have ivoId in data model.
	 * @param url
	 * @return
	 */
	public static String truncateURL(String url)
	{
		int index = url.indexOf(":");
		if(index < 0)
			return url;
		return url.substring(index);
	}

	public DirectedGraph<ObjectType, Role> getObjectTypeGraph() {
		return objectTypeGraph;
	}
	

}
