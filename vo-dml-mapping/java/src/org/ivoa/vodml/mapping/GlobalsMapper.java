package org.ivoa.vodml.mapping;

import java.util.ArrayList;
import java.util.Hashtable;

import org.ivoa.vodml.VODMLManager;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.mapping.vodmli.ObjectTypeCollection;
import org.ivoa.votable.jaxb.VODMLGlobals;
import org.ivoa.votable.jaxb.VODMLObject;
import org.ivoa.votable.jaxb.VOTABLE;

public class GlobalsMapper extends ArrayList<ObjectTypeNode>{

	private Hashtable<String, ObjectTypeCollection> collections;
	private VODMLManager vodmlManager;
	public GlobalsMapper(VODMLManager vodmlManager, VOTABLE votable){
		this.vodmlManager = vodmlManager;
		this.collections = new Hashtable<String, ObjectTypeCollection>();
		this.initCollections(votable);
	}
	private void initCollections(VOTABLE votable){
		for(Object o:votable.getVODML().getGLOBALSAndTEMPLATES()){
			if(o instanceof VODMLGlobals)
				add((VODMLGlobals)o);
		}
	}
	private ObjectTypeCollection getCollection(String dmtype){
		ObjectTypeCollection otc = collections.get(dmtype);
		if(otc == null){
			otc = new ObjectTypeCollection(otn, _vodmli)
		}
	}
	public void add(VODMLGlobals globals){
		for(VODMLObject o: globals.getINSTANCES()){
			String dmtype = o.getDmtype();
			if(dmtype == null) // TBD throw runtime exception? For if the mapping is valid this should not hapen. root INSTANCEs MUST have a dmtype.
				return;
			ObjectTypeCollection otc = getCollection(dmtype);
			otc.add(o);
		}
		
	}
	
}
