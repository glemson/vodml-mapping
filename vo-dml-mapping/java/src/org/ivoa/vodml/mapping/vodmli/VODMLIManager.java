package org.ivoa.vodml.mapping.vodmli;

import graphs.DirectedGraph;
import graphs.Node;

import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

import org.ivoa.vodml.mapping.VOTABLEGraph;
import org.ivoa.vodml.mapping.VOTableElement;
import org.ivoa.vodml.mapping.vodmli.ObjectTypeCollection;
import org.ivoa.vodml.mapping.vodmli.TypeMapper;
import org.ivoa.vodml.VODMLREF;
import org.ivoa.vodml.VODMLManager;
import org.ivoa.vodml.graph.ModelNode;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.graph.ReferenceNode;
import org.ivoa.vodml.graph.RoleNode;
import org.ivoa.vodml.graph.TypeNode;
import org.ivoa.vodml.mapping.jaxb.MappedModels;
import org.ivoa.vodml.model.ObjectID;
import org.ivoa.vodml.model.ObjectTypeInstance;
import org.ivoa.vodml.model.StringsIdentifier;
import org.ivoa.vodml.model.StructuredObject;
import org.ivoa.vodml.votable.graph.GroupNode;
import org.ivoa.vodml.xsd.jaxb.Model;


/**
 * Manages instances of data models obtained by mapping to VOTable.<br/>
 * For each GROUP in the VOTable an ObjectTypeCollection is maintained
 * @author GerardLemson
 *
 */
public class VODMLIManager extends ArrayList<ObjectTypeCollection>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2917356713743417831L;

	
	private VOTABLEGraph votableGraph; // the votable
	private VODMLManager vodml; // models
	private TypeMapper mapper;
	private Hashtable<Integer, ObjectTypeCollection> objectCollectionsByGroupRank;
	private Hashtable<StringsIdentifier, ObjectTypeInstance> objectsByPublisherDID;
	private DirectedGraph<ObjectTypeCollection, GroupNode> graph;
	
	public VODMLIManager(VODMLManager _vodml, VOTABLEGraph vg, MappedModels _mappedModels)
	{
		this.vodml = _vodml;
		this.votableGraph = vg;
		this.mapper = new TypeMapper(_mappedModels, vodml);
		objectCollectionsByGroupRank = new Hashtable<Integer, ObjectTypeCollection>();
		objectsByPublisherDID = new Hashtable<StringsIdentifier, ObjectTypeInstance>();
		init();
	}

	private void init()
	{
		// add nodes
		for(VOTableElement group: votableGraph)
		{
			if(group instanceof GroupNode)
				register((GroupNode)group);
		}
		this.graph = buildGroupRefGraph();
		if(graph.hasCycles())
			System.out.println("graph of ObjectType collections has cycles");
		graph.sort();
	}
	
	private DirectedGraph<ObjectTypeCollection, GroupNode> buildGroupRefGraph()
	{
		this.graph = new DirectedGraph<ObjectTypeCollection, GroupNode>();
		for(ObjectTypeCollection oc:this)
			graph.addNode(new Node<ObjectTypeCollection>(oc.getID(), oc));
		for(ObjectTypeCollection oc:this)
		{
			for(GroupNode ref: oc.getGroupRefs())
				graph.addEdge(oc.getID(),objectCollectionsByGroupRank.get(ref.getRank()).getID(), ref);
		}
		return graph;
	}

	public ObjectTypeCollection getOC(GroupNode group)
	{
		return objectCollectionsByGroupRank.get(group.getRank());
	}
	private ObjectTypeCollection addOC(GroupNode gn)
	{
		ObjectTypeCollection oc = new ObjectTypeCollection(gn, this);
		objectCollectionsByGroupRank.put(gn.getRank(), oc);
		return oc;
	}
	/**
	 * Make a collection for each GROUP node that does not play a collection role in definition of a parent GROUPs.<br/>
	 * Those will be dealt with when recursing down group hierarchy. Note, still allows stand-alone GROUPs with a CONTAINER pointer
	 * and a collection role. These will not be recursed down, hence ...
	 * @param gn
	 */
	private void register(GroupNode gn)
	{
		if(gn.getVODML() == null)
			return;
		TypeNode tn = gn.getVODMLType();
		TypeNode roleType = gn.getVODMLRoleType();
		RoleNode role = gn.getVODMLRole();
		GroupNode ref = gn.getGroupRef();
		// if root object
		// TODO check that there is no VODML_GROUP in parent hierarchy 
		if(role == null && (tn instanceof ObjectTypeNode && !VODMLREF.isModel(gn.getVODMLAnnotation())))
		{
			ObjectTypeCollection oc = addOC(gn);
			this.add(oc);
		} else
		{
//			System.out.printf("Skipped GROUP %d with utype '%s', did not build collection for it.\n",gn.getRank(), gn.getUtype());
		}
	}
	
	/**
	 * Extract objects from a votable with data.<br/>
	 * Assume it is compatible with the structure of the VODMLI manager.
	 * 
	 * TODO find proper/fast implementations
	 * 
	 * @param votable
	 */
	public void extractObjects(URL votableURL)
	{
		// should possibly reparse VOTable in an efficient manner to extract objects also from TABLEDATA
		// for now only infer direct GroupNode objects
		for(Node<ObjectTypeCollection> non: this.graph.getNodes())
		{
			ObjectTypeCollection on = non.getObject();
			if(!on.isFilled())
				on.fill(votableGraph);
		}
		// this produces a collection of objects for each GROUP
		// need some post processing to add objects to their container, follow references etc.

		// look for collections whose GROUP has a container.
		for(ObjectTypeCollection oc: this)
		{
			GroupNode ocGroup = oc.getGroup();
			if(ocGroup.hasContainer())
			{
				GroupNode containerPointer = ocGroup.getContainerPointer();
				RoleNode container = containerPointer.getVODMLRole();
				String collectionUtype = container.getOwner().getVODMLREF(); // TODO CHECK THIS !!!
				for(ObjectTypeInstance o: oc)
				{
					if(o.getContainer() != null)
						continue; // somehow already loaded, should likely not happen
					ObjectTypeInstance c = objectsByPublisherDID.get(o.getContainerPublisherDID());
					if(c != null)
						c.add2Collection(collectionUtype, o);
				}
			}
		}

	}

	protected VODMLManager getVodml() {
		return vodml;
	}
	
	/**
	 * Serialization ala vo-dml-instance.xsd. 
	 * @return
	 */
	public String serialize1()
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>").append(StructuredObject.NEWLINE);
		sb.append("<vodmli:instance xmlns:vodmli=\"http://volute.g-vo.org/dm/vo-dml-instance/v0.x\">").append(StructuredObject.NEWLINE);
		String offset="  ";
		for(Model m: vodml)
		{
			ModelNode mn = vodml.findModelForVODMLRef(m.getName()).getModelNode();
			mn.deepToString(sb, offset).append(StructuredObject.NEWLINE);
		}
		for(ObjectTypeCollection oc: this)
		{
			GroupNode gn = oc.getGroup();
			sb.append(String.format("<!-- +++++++++ START %s: -->\n",gn.toString()));
			for(ObjectTypeInstance ot: oc)
			{
				if(ot.getContainer() == null)
					ot.deepToString(sb, offset);
				else
					sb.append(String.format("<!-- object with transientId=%d written in collection on container with transientId=%d -->\n"
							,ot.getTransientID(), ot.getContainer().getTransientID()));
					
			}	
			sb.append(String.format("<!-- +++++++++   END %s -->\n",gn.toString()));
		}
		sb.append("</vodmli:instance>").append(StructuredObject.NEWLINE);
		return sb.toString();
	}

	public TypeMapper getMapper() {
		return mapper;
	}
	
	/**
	 * Add specified object, assign it with an ID if it does not have one.<br/>
	 * @param o
	 * @return
	 */
	public ObjectID addObject(ObjectTypeInstance o)
	{
		if(o.get_publisherDID() != null)
		objectsByPublisherDID.put(o.get_publisherDID(), o);
		return o.get_ID();
	}

}
