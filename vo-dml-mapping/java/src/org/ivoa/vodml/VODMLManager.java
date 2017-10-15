package org.ivoa.vodml;

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

import javax.xml.bind.JAXB;
import javax.xml.bind.Marshaller;

import org.ivoa.vodml.graph.AttributeNode;
import org.ivoa.vodml.graph.DataTypeNode;
import org.ivoa.vodml.graph.ElementNode;
import org.ivoa.vodml.graph.ModelGraph;
import org.ivoa.vodml.graph.ModelNode;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.graph.ReferenceNode;
import org.ivoa.vodml.graph.RelationNode;
import org.ivoa.vodml.graph.RoleNode;
import org.ivoa.vodml.graph.StructuredTypeNode;
import org.ivoa.vodml.graph.TypeNode;
import org.ivoa.vodml.instance.jaxb.ModelLocation;
import org.ivoa.vodml.xsd.jaxb.Model;
import org.ivoa.vodml.xsd.jaxb.ModelImport;


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

//	public static final String VODML_url = "https://volute.g-vo.org/svn/trunk/projects/dm/vo-dml/models/vo-dml/VO-DML.vo-dml.xml";

	private VODMLRegistry vodmlReg;
	/** models keyed by url, to quickly see whether a model has been loaded */
	private Hashtable<String,Model> models_url;
	/** models keyed by their utype */
	private Hashtable<String,Model> models_prefix;


	 /** 
	 * This should be an ivoa identifier, currently the Model's name is used. */
	private Hashtable<String,ModelGraph> modelgraphs;

	/** utypes keyed by (possibly alternative) prefixes*/
	private Hashtable<String,String> utype_prefixes;
	/** truncated model urls keyed by model name (prefix) */
	private Hashtable<String,String> modelname_url;
	/** Graph of objectype nodes */
	private DirectedGraph<ObjectTypeNode, RoleNode> objectTypeGraph; 


	public VODMLManager(String url) {
		this(new RemoteVODMLRegistry(), url);
	}
	public VODMLManager(VODMLRegistry reg, String url)
	{
		this.vodmlReg = reg; // TODO check it's not null
		ArrayList<ModelLocation> ms = new ArrayList<ModelLocation>();
		ModelLocation ml = new ModelLocation();
		ml.setVodmlURL(url);
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
		models_prefix = new Hashtable<String, Model>();
		models_url = new Hashtable<String, Model>();

		utype_prefixes = new Hashtable<String, String>();
		modelname_url = new Hashtable<String, String>();
		modelgraphs = new Hashtable<String, ModelGraph>();


//		loadModels(VODML_url, "vo-dml"); // must be after initialisation of data structures
		for(ModelLocation _m: _models)
			loadModels(_m.getVodmlURL(),_m.getVodmlrefPrefix());
		
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
		m = VODML_JAXBHelper.jaxb.parseVODML(in);
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
				for(ModelImport mp: m.getImport())
				{
					Model mi = models_url.get(mp.getUrl());
					if(mi == null)
						loadModels(mp.getUrl(), mp.getName());
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
		modelname_url.put(utype,url);
		String otherutype = utype_prefixes.get(prefix);
		if(otherutype == null)
		{
			utype_prefixes.put(prefix, utype);
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
	public ElementNode findReferableElement(String vodmlref) {
		ModelGraph model = findModelForVODMLRef(vodmlref);
		if(model == null)
			return null;
		return model.getElementForVODMLREF(vodmlref);
	}

	public ModelGraph findModelForUtypePrefix(String prefix) {
		return modelgraphs.get(prefix);
	}

	public ModelGraph findModelForVODMLRef(String vodmlref) {
		String prefix = VODMLREF.getPrefix(vodmlref);
		return findModelForUtypePrefix(prefix);
	}
	public ModelGraph findModelForURL(String url) {
		Model m = (url == null?null:models_url.get(url));
		return m == null?null:modelgraphs.get(m.getName());
	}
	
	public TypeNode findType(String utype)
	{
		ElementNode node = findReferableElement(utype);
		if(node instanceof TypeNode)
			return (TypeNode)node;
		else
			return null;
	}	public RoleNode findRole(String utype)
	{
		ElementNode node = findReferableElement(utype);
		if(node instanceof RoleNode)
			return (RoleNode)node;
		else
			return null;
	}
	public ObjectTypeNode findContainer(ObjectTypeNode ot)
	{
		ObjectTypeNode container = null;
		// TODO implement
		return container;
	}
	public TypeNode findTypeForRole(RoleNode rn)
	{
		String utype = rn.getRole().getDatatype().getVodmlRef();
		ElementNode node = findReferableElement(utype);
		if(node instanceof TypeNode)
			return (TypeNode)node;
		else
			return null;
	}
	/**
	 * Check whether the specified type is a valid type for instances of the given role.
	 * I.e. it must be the datatype of the role or one of its subclasses
	 * @param role
	 * @param type
	 * @return
	 */
	public boolean areRoleTypeCompatible(RoleNode role, TypeNode type)
	{
		if(role == null || type == null)
			return true;
		
		TypeNode datatype = findTypeForRole(role);
		TypeNode currentType = type;
		while(currentType != null)
		{
		    if(datatype == currentType)
		    	return true;
		    currentType = currentType.getSuperType();
		}
		
			
		// special checks: container and reference are allowed to use vodml-map:Identifier as type!
		// TBD could put this on utypeattr, add the actual declared type of the role, but indicate 
		// it is indirectly referenced through a vodml-map:Identifier
    	return false;
	}
	
	public void buildGraphs()
	{
		this.objectTypeGraph = new DirectedGraph<ObjectTypeNode, RoleNode>();
		// Should be able to treat model s in order
		for(Model m: this)
		{
			ModelGraph mg = new ModelGraph(this,m, getModelUrl(m.getName()));
			modelgraphs.put(m.getName(), mg);
			mg.initModel();
			for(ObjectTypeNode ot: mg)
				objectTypeGraph.addNode(new Node<ObjectTypeNode>(ot.getVODMLREF(), ot));
		}

		for(ModelGraph mg: this.modelgraphs.values())
			mg.resolveRoles();

		// add edges
		for(Node<ObjectTypeNode> node: objectTypeGraph.getNodes())
		{
			ObjectTypeNode ot = node.getObject();
			if(ot.getContainer() != null)
				addEdge(node, ot.getContainer());
			Hashtable<String,TypeNode> visited = new Hashtable<String, TypeNode>();
			addEdges(node, ot, visited);
		}
		objectTypeGraph.sort();
	}

	public String getModelUrl(String utype)
	{
		String prefix = VODMLREF.getPrefix(utype);
		return modelname_url.get(prefix);
	}
	private void addEdges(Node<ObjectTypeNode> node, StructuredTypeNode st, Hashtable<String,TypeNode> visited)
	{
		Iterator<ReferenceNode> refs = st.getReferences();
		if(refs != null){
			while(refs.hasNext())
				addEdge(node, refs.next());
		}
/*
 * TODO need to take care of cycles in datatypes here 
 * If a datatype with reference at some point   
 */
		Iterator<AttributeNode> atts = st.getAttributes();
		if(atts == null) return;
		while(atts.hasNext())
		{
			TypeNode at = atts.next().getDatatype();
			if(at instanceof StructuredTypeNode && visited.get(at.getVODMLREF()) == null)
			{
				visited.put(at.getVODMLREF(), at);
				addEdges(node, (StructuredTypeNode)at, visited);
			}
		}
	}

	
	/** Add edge from fromNode to target of Role, and to all subclasses of the target */
	private void addEdge(Node<ObjectTypeNode>  fromNode, RoleNode role)
	{
		ObjectTypeNode target = (ObjectTypeNode)role.getDatatype();
		if(target == null)
			throw new IllegalStateException(String.format("Can not find ObjectTypeNode for role '%s'",role.getVODMLREF()));

		addEdge(fromNode, role, target);
	}
	/** Recursive! also add edge to all subtypes.<br/> */
	private void addEdge(Node<ObjectTypeNode>  fromNode, RoleNode role, ObjectTypeNode target)
	{
		objectTypeGraph.addEdge(fromNode, target.getVODMLREF(), role);
		Iterator<TypeNode> subclasses = target.getSubClasses();
		while(subclasses.hasNext())
			addEdge(fromNode, role, (ObjectTypeNode)subclasses.next());
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

	public DirectedGraph<ObjectTypeNode, RoleNode> getObjectTypeGraph() {
		return objectTypeGraph;
	}
	

}
