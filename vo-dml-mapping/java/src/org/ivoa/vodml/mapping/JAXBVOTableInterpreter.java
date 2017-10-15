package org.ivoa.vodml.mapping;

import graphs.DirectedGraph;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBValidator;
import org.ivoa.vodml.mapping.VODMLException;
import org.ivoa.vodml.mapping.JAXBVOTableInterpreter;
import org.ivoa.vodml.RemoteVODMLRegistry;
import org.ivoa.vodml.VODMLREF;
import org.ivoa.vodml.VODMLManager;
import org.ivoa.vodml.VODMLRegistry;
import org.ivoa.vodml.VODML_JAXBHelper;
import org.ivoa.vodml.graph.AttributeNode;
import org.ivoa.vodml.graph.CompositionNode;
import org.ivoa.vodml.graph.DataTypeNode;
import org.ivoa.vodml.graph.ObjectTypeNode;
import org.ivoa.vodml.graph.ReferenceNode;
import org.ivoa.vodml.graph.RoleNode;
import org.ivoa.vodml.graph.StructuredTypeNode;
import org.ivoa.vodml.graph.TypeNode;
import org.ivoa.vodml.instance.jaxb.ModelLocation;
import org.ivoa.vodml.mapping.jaxb.MappedModels;
import org.ivoa.vodml.mapping.vodmli.VODMLIManager;
import org.ivoa.vodml.test.Tester;
import org.ivoa.vodml.votable.VOTABLE_JAXBHelper;
import org.ivoa.vodml.xml.Model;
import org.ivoa.vodml.xml.VODMLUtil;
import org.ivoa.vodml.xsd.jaxb.Attribute;
import org.ivoa.vodml.xsd.jaxb.Composition;
import org.ivoa.vodml.xsd.jaxb.Reference;
import org.ivoa.votable.jaxb.Field;
import org.ivoa.votable.jaxb.Param;
import org.ivoa.votable.jaxb.Table;
import org.ivoa.votable.jaxb.VODML;
import org.ivoa.votable.jaxb.VODMLAttribute;
import org.ivoa.votable.jaxb.VODMLComposition;
import org.ivoa.votable.jaxb.VODMLFieldOrParamRef;
import org.ivoa.votable.jaxb.VODMLGlobals;
//import org.ivoa.vodml.votable.graph.VOTABLEGraph;
import org.ivoa.votable.jaxb.VODMLInstance;
import org.ivoa.votable.jaxb.VODMLInstanceTemplates;
import org.ivoa.votable.jaxb.VODMLORMReference;
import org.ivoa.votable.jaxb.VODMLObject;
import org.ivoa.votable.jaxb.VODMLPrimitive;
import org.ivoa.votable.jaxb.VODMLReference;
import org.ivoa.votable.jaxb.VODMLRole;
import org.ivoa.votable.jaxb.VOTABLE;
import org.kohsuke.rngom.util.Uri;

/**
 * Utility class to help interpret a VOTable and its elements in terms of data
 * model.<br/>
 * 
 * @author GerardLemson
 *
 */
public class JAXBVOTableInterpreter {

	public static VODMLIManager interpret(URL votableURL, URL mappingFile) throws Exception {
		return interpret(votableURL, mappingFile, new RemoteVODMLRegistry());
	}

	/**
	 * input a VOTABLE, output a set of data model instances, managed by a
	 * VODMLIManager.<br/>
	 * 
	 * @param votable
	 * @return
	 * @throws Exception
	 */
	public static VODMLIManager interpret(URL votableURL, URL mappingFile, VODMLRegistry vodmlReg) throws Exception {
		JAXBVOTableInterpreter voti = new JAXBVOTableInterpreter(vodmlReg);
		voti.run(votableURL, mappingFile, false);
		return voti.vodmli;
	}

	/**
	 * input a VOTABLE, output a set of data model instances, managed by a
	 * VODMLIManager.<br/>
	 * 
	 * @param votable
	 * @return
	 * @throws Exception
	 */
	public static boolean validate(URL votableURL, VODMLRegistry vodmlReg) throws Exception {
		JAXBVOTableInterpreter voti = new JAXBVOTableInterpreter(vodmlReg);
		return voti.run(votableURL, null, true);
	}

	// ~~~~
	private URL votableURL, mappingFile;
	private VOTABLE votable;
	private VODML vodml;
	// private VOTABLEGraph votableGraph;
	private VODMLManager vodmlManager;
	private VODMLIManager vodmli;
	/**
	 * annotitonErrors caused by internal inconsistency of VODML element,
	 * mappingErrors by inconsistencies between VODML elements and the VOTable
	 * elements they anotate.
	 */
	private ArrayList<Exception> annotationErrors, mappingErrors;
	/**
	 * dictionary of all VOTableElements representing a Type keyed by the vodmlref
	 * of the Type they represent
	 */
	private ArrayList<StructuredTypeNode> standAloneTypes, templateTypes;
	private ArrayList<ObjectTypeNode> objectTypes;
	private Tester tester;
	private VODMLRegistry vodmlRegistry;

	private JAXBVOTableInterpreter(VODMLRegistry vodmlReg) {
		tester = new Tester();
		this.vodmlRegistry = vodmlReg;
	}

	/**
	 * Do the main work ... result is a VODMLIManager filled with objects.
	 */
	private boolean run(URL _votableURL, URL _mappingFile, boolean validateOnly) throws Exception {
		this.votableURL = _votableURL;
		this.mappingFile = _mappingFile;

		if (!tester.testVOTable(_votableURL)) {
			throw new Exception("VOTable was not valid, correct it before proceeding");
		}
		// first parse of votable, ignoring TRs parts
		// TOO try to transform this into using a custom parser
		this.votable = VOTABLE_JAXBHelper.jaxb.parseVOTABLE(votableURL, true);
		this.vodml = this.votable.getVODML();
		if (vodml == null)
			throw new Exception("Nothing to be done: No VODML annotation in VOTable.");

		this.annotationErrors = new ArrayList<Exception>();

		// find models, load up the VODMLManager, get objecttypes into graph
		inferModels();

		// validate the VO-DML annotation, i.e. do the types/roles exist in models
		// and are they properly related
		// this does NOT validate the consistency of their assignment to the votable
		// elements, that is done in validate mapping.
		boolean hasAnnotationErrors = !validateVODMLAnnotation();
		if (hasAnnotationErrors) {
			System.out.printf("Found %d exceptions while validating VODML annotations.\n", annotationErrors.size());
			for (Exception e : annotationErrors)
				System.out.println("\tEXCEPTION: " + e.getMessage());
		}

		// validate the annotated elements.

		this.mappingErrors = new ArrayList<Exception>();
		boolean hasMappingErrors = !validateMapping();

		if (hasMappingErrors) {
			System.out.printf("Found %d exceptions while validating mapping.\n", mappingErrors.size());
			for (Exception e : mappingErrors)
				System.out.println("\tEXCEPTION: " + e.getMessage());
		}
		if (validateOnly || hasMappingErrors || hasAnnotationErrors)
			return !(hasMappingErrors || hasAnnotationErrors);

		// find how ObjectType instances are represented by their groups-s and
		// register them on the vodmlimanager
		extractObjects();
		return true;
	}

	private void extractObjects() throws Exception {
		MappedModels mm = VODML_JAXBHelper.jaxb.parseVODMLMapping(mappingFile);
		if (mm == null)
			throw new Exception("Unable to find mapping file " + mappingFile);
		// this.vodmli = new VODMLIManager(vodmlManager, votableGraph,mm);
		// vodmli.extractObjects(votableURL);
	}

	/**
	 * Interpret all elements as model elements.<br/>
	 * Register types
	 * 
	 * @throws Exception
	 */
	private DirectedGraph<ObjectTypeNode, RoleNode> inferModels() throws Exception {
		ArrayList<ModelLocation> models = new ArrayList<ModelLocation>();
		for (org.ivoa.votable.jaxb.Model el : vodml.getMODELS()) {
			ModelLocation ml = new ModelLocation();
			ml.setVodmlURL(el.getURL());
			ml.setVodmlrefPrefix(el.getNAME());
			ml.setIoIdentifier(el.getIDENTIFIER());
			models.add(ml);
		}
		if (models.size() > 0) {
			vodmlManager = new VODMLManager(vodmlRegistry, models);
			return vodmlManager.getObjectTypeGraph();
		} else
			return null;

	}

	/**
	 * 
	 * @throws VODMLException
	 */
	private boolean validateVODMLAnnotation() throws VODMLException {
		boolean ok = true;
		int inSize = annotationErrors.size();

		this.standAloneTypes = new ArrayList<StructuredTypeNode>();
		this.templateTypes = new ArrayList<StructuredTypeNode>();
		this.objectTypes = new ArrayList<ObjectTypeNode>();

		// associate all vodmlref attributes to their model elements
		List<Object> globalsAndTemplates = vodml.getGLOBALSAndTEMPLATES();
		for (Object got : globalsAndTemplates) {
			if (got instanceof VODMLInstanceTemplates)
				continue;
			VODMLGlobals global = (VODMLGlobals) got;
			for (VODMLObject o : global.getINSTANCES()) {
				String dmtype = o.getDmtype();
				if (dmtype == null) {
					annotationErrors.add(new VODMLException("Missing 'dmtype' attribute on global instance"));
					continue;
				}
				try {
					TypeNode tn = vodmlManager.findType(dmtype);// .inferVODMLAnnotation(vodmlManager);
					if (tn instanceof ObjectTypeNode) {
						ObjectTypeNode on = (ObjectTypeNode) tn;
						validateObject(o);
						standAloneTypes.add(on);
						objectTypes.add(on);
					} else {
						throw new VODMLException(String.format("dmtype '%s' does not identify an ObjectType", dmtype));
					}
				} catch (VODMLException er) {
					annotationErrors.add(er);
				}

				// if we want to keep a dictionary of Type-VODataElement pairs, the
				// following method can do so.
				// register(o);
			}
		}
		for (Object got : globalsAndTemplates) {
			if (got instanceof VODMLGlobals)
				continue;
			VODMLInstanceTemplates template = (VODMLInstanceTemplates) got;
			for (VODMLObject o : template.getINSTANCES()) {
				String dmtype = o.getDmtype();
				if (dmtype == null) {
					annotationErrors.add(new VODMLException("Missing 'dmtype' attribute on root template instance"));
					continue;
				}
				try {
					TypeNode tn = vodmlManager.findType(dmtype);// .inferVODMLAnnotation(vodmlManager);
					if (tn instanceof ObjectTypeNode) { //
						ObjectTypeNode on = (ObjectTypeNode) tn;
						validateObject(o);
						templateTypes.add(on);
						objectTypes.add(on);
					} else if (tn instanceof DataTypeNode) {
						DataTypeNode on = (DataTypeNode) tn;
						validateDataObject(o);
						templateTypes.add(on);
					} else {
						throw new VODMLException(String.format(
						    "dmtype '%s' of root template type does not identify an ObjectType or DataType", dmtype));
					}
				} catch (VODMLException er) {
					annotationErrors.add(er);
				}
			}
		}
		validateVODMLGraph();

		ok = (annotationErrors.size() - inSize == 0);
		return ok;
	}

	/**
	 * Check that all references are proper and point to existing objects where it
	 * is possible to check this.<br/>
	 * I.e. this is at this stage only possible for IDREF and remote references.
	 * Existence of ORM references can only be checked once the data is read,
	 * though it can be checked whether referenced container template objects
	 * exist.
	 * 
	 * @param errors
	 */
	private void validateVODMLGraph() {

	}

	/**
	 * For all template instances, check whether the mapping to the VOTable
	 * elements is consistent. <br/>
	 * E.g. this includes that FIELDs referenced by ATTRIBUTE/COLUMNs exist in the
	 * same table that the ATTRIBUTE's owning type is mapped to. Etc.
	 * 
	 * Errors written to this.mappingErrors
	 */
	private boolean validateMapping() throws VODMLException {
		int inSize = mappingErrors.size();
		VODML vodmlAnnotation = votable.getVODML();
		List<Object> lt = vodmlAnnotation.getGLOBALSAndTEMPLATES();

		for (Object ot : lt) {
			if (ot instanceof VODMLGlobals)
				continue;
			VODMLInstanceTemplates template = (VODMLInstanceTemplates) ot;
			Object tr = template.getTableref();
			Table table = null;
			if (tr instanceof Table)
				table = (Table) tr;
			else
				mappingErrors.add(new VODMLException(String.format("template's tablref '%s' does not identify a TABLE element",
				    template.getTableref())));

			for (VODMLObject o : template.getINSTANCES()) {
				validateTemplateMapping(o, table);
			}
		}
		return mappingErrors.size() == inSize;
	}

	/**
	 * Return true if the given table contains the given field in its content,
	 * false otherwise.<br/>
	 * 
	 * @param table
	 * @param field
	 * @return
	 */
	public static boolean TABLE_contains_FIELD(Table table, Object field) {
		for (Object je : table.getINFOSAndFIELDSAndPARAMS()) {
			if (je == field)
				return true;
		}
		return false;
	}

	private void validateTemplateMapping(VODMLObject o, Table table) {
		// check that all child elements that have a COLUMN are actually referring
		// to FIELDs in this table
		for (VODMLAttribute a : o.getATTRIBUTES()) {
			for (JAXBElement<? extends VODMLPrimitive> e : a.getCOLUMNSAndCONSTANTSAndLITERALS()) {
				VODMLPrimitive p = e.getValue();
				if ("COLUMN".equals(e.getName().getLocalPart())) {// implies p
																													// instanceof
																													// VODMLFieldOrParamRef){
					Object cr = ((VODMLFieldOrParamRef) p).getRef();
					if (cr instanceof Field) {
						// TODO check FIELD inside of table
						if (!TABLE_contains_FIELD(table, cr))
							mappingErrors.add(new VODMLException(String.format(
							    "Bad mapping of attribute %s: FIELD with ID '%s' does not exist on TABLE with ID '%s'",
							    a.getDmrole(), ((Field) cr).getID(), table.getID())));
					} else {
						mappingErrors.add(new VODMLException(String.format(
						    "IDREF on COLUMN should identify a FIELD but now identifies a %s", o.getClass().getName())));
					}
				} else if ("CONSTANT".equals(e.getName().getLocalPart())) {// implies p
																																	 // instanceof
																																	 // VODMLFieldOrParamRef){
					Object cr = ((VODMLFieldOrParamRef) p).getRef();
					if (!(cr instanceof Param)) {
						mappingErrors.add(new VODMLException(String.format(
						    "Bad mapping of attribute %s: IDREF on CONSTANT should identify a PARAM but now identifies a %s",
						    a.getDmrole(), o.getClass().getName())));
					}
					// TODO add warning about type/multiplicity?
				}
			}
			if(a.getINSTANCES() != null){
				for (VODMLObject ao : a.getINSTANCES()) {
					validateTemplateMapping(ao, table);
				}
			}
		}
		if(o.getCOMPOSITIONS() != null){
			for (VODMLComposition c : o.getCOMPOSITIONS()) {
				if(c.getINSTANCES() != null)
					for (VODMLObject co : c.getINSTANCES())
						validateTemplateMapping(co, table);
				if(c.getEXTINSTANCES() != null){
					for (JAXBElement<Object> co : c.getEXTINSTANCES()) {
						// TODO do soemthing?
					}
				}
			}
		}
	}

	private RoleNode validateRole(VODMLRole vodmlrole, TypeNode parentType) {
		RoleNode role = vodmlManager.findRole(vodmlrole.getDmrole());
		if (role == null) {
			annotationErrors.add(new VODMLException(String.format("dmrole '%s'  does not exist", vodmlrole.getDmrole())));
			return null;
		}
		if (parentType != null && role != null && !parentType.isRoleAvailable(role)) {
			annotationErrors.add(new VODMLException(String.format(
			    "%s '%s' is not a role on type '%s'", vodmlrole.getClass().getSimpleName(), vodmlrole.getDmrole(),parentType)));
			return null;
		}
		// TODO check that the kind of roles are correct, i.e. VODMLAttribute <-> Attribute etc
		if((vodmlrole instanceof VODMLAttribute && !(role.getRole() instanceof Attribute))
			|| (vodmlrole instanceof VODMLReference && !(role.getRole() instanceof Reference))
			|| (vodmlrole instanceof VODMLComposition && !(role.getRole() instanceof Composition))){
			annotationErrors.add(new VODMLException(String.format("Role '%s' declared as '%s' is actually a '%s'", 
					role.getVODMLREF(),vodmlrole.getClass().getSimpleName(),role.getRole().getClass().getSimpleName())));
			return null;
		}

		return role;
	}

	/**
	 * R
	 * 
	 * @param a
	 * @param parentType
	 * @return true if the attribute identified by a is a valid attribute on the
	 *         specified type, false otherwise
	 */
	private boolean validateAttribute(VODMLAttribute a, TypeNode parentType) {
		int inSize = annotationErrors.size();
		RoleNode role = validateRole(a, parentType);
		if (role == null)
			return false;

		// ask for role's datatype as known to parent, to take into account possible
		// subsetting of the role.
		TypeNode datatype = vodmlManager.findType(parentType.roleDatatype(role));
		if (a.getINSTANCES() != null) {
			for (VODMLObject i : a.getINSTANCES()) {
				if(!checkRoleDMType(role, i, datatype))
					return false;
			}
		}
		if (a.getCOLUMNSAndCONSTANTSAndLITERALS() != null) {
			for (JAXBElement<? extends VODMLPrimitive> i : a.getCOLUMNSAndCONSTANTSAndLITERALS()) {
				if(!checkRoleDMType(role, i.getValue(), datatype))
					return false;
			}
		}

		return (annotationErrors.size() == inSize);
	}

	public boolean checkRoleDMType(RoleNode role, VODMLInstance i, TypeNode datatype){
		String dmtype = i.getDmtype();
		if (dmtype != null) {
			TypeNode declaredType = vodmlManager.findType(dmtype);
			if(declaredType == null){
				annotationErrors.add(new VODMLException(String.format(
				    "declared dmtype '%s' of role '%s' cannot be found in declared models",dmtype, role.getVODMLREF())));
				return false;
			}else if (!isTypeCompatible(declaredType, datatype)){
				annotationErrors.add(new VODMLException(String.format(
				    "@dmtype '%s' is not compatible with expected datatype '%s' of Role '%s'",dmtype, datatype, role.getVODMLREF())));
				return false;
			}
		} else // ensure @dmtype set on all instances and primitives before further processing
			i.setDmtype(datatype.getVODMLREF());
		return true;
	}
	
	
	public static boolean isTypeCompatible(TypeNode declaredType, TypeNode roleType) {
		if (roleType.equals(declaredType))
			return true;
		else if (declaredType.getSuperType() != null)
			return isTypeCompatible(declaredType.getSuperType(), roleType);
		else
			return false;
	}
	/**
	 * Validate the VODMLReference, supposedly belonging to the declared parenttype
	 * @param r
	 * @param parentType
	 * @return
	 * @throws VODMLException
	 */
	private boolean validateReference(VODMLReference r, TypeNode parentType) throws VODMLException {
		int inSize = annotationErrors.size();
		// check the dmrole fits on the parent type.
		RoleNode role = validateRole(r, parentType);
		if (role == null)
			return false;
		
		// TODO check that reference object is of acceptable type ...
		// TBD should this be put on the validateGraph method?
		if(r.getIDREVESAndREMOTEREFERENCESAndFOREIGNKEIES() != null){
			for (JAXBElement<?> e: r.getIDREVESAndREMOTEREFERENCESAndFOREIGNKEIES()){
				Object o = e.getValue();
				if(o instanceof VODMLORMReference){
					// TODO
					VODMLORMReference orm = (VODMLORMReference)o;
					Object target = orm.getTARGETID();
				} else if (o instanceof String){ // anyURI
					// TODO check it is really a URI, then check it really identifies an object of the right type.
					// Latter is still ill defined, so may be better to throw a warning here!
					String uri = (String)o;
					System.out.printf("WARNING external reference found to %s. unclear whether compatible with reference %s",uri, r.getDmrole());
				} else if(o instanceof VODMLObject) {// object somewhere in this document, must be a VODMLObject
					VODMLObject ob = (VODMLObject)o;
					String dmtype = ob.getDmtype(); // NOTE nice here if indeed each type declares dmtype explicitly!!
					// TODO check declared type of reference and the type of the VODMLObject are compatible, 
					// 	i.e. ob MUST be of same type, or of a subtype of the datatype of the reference. 
					// TODO take into account subsets constraints! 
					
				}
				
			}
			
			
		}
		return (annotationErrors.size() == inSize);
	}
	private boolean validateContainer(VODMLReference r, ObjectTypeNode parentType) throws VODMLException {
		int inSize = annotationErrors.size();
		if (parentType != null && !parentType.hasContainer()) {
			annotationErrors.add(new VODMLException(String.format("Type '%s'  an not have a container", parentType.getVODMLREF())));
		}
		// TODO check that type of container agrees with target of the container ointer
		return (annotationErrors.size() == inSize);
	}

	private boolean validateComposition(VODMLComposition c, TypeNode parentType) throws VODMLException {
		int inSize = annotationErrors.size();
		RoleNode role = validateRole(c, parentType);
		if (role == null)
			return false;
		
		TypeNode datatype = vodmlManager.findType(parentType.roleDatatype(role));
		if (c.getINSTANCES() != null) {
			for (VODMLObject i : c.getINSTANCES()) {
				if(!checkRoleDMType(role, i, datatype))
					return false;
			}
		}
		
		// TBD similar test of EXTINSTANCEs should be made from the actual child-objects found in that collection.

		return (annotationErrors.size() == inSize);

	}

	/**
	 * Validate the contents of the VOTable::VODMLObject annotation.<br/>
	 * I.e. check whether declared contents, attributes, references, composition and container are consistent
	 * with the definition of the VO-DML::ObjectTye identified by the dmtype of the element.
	 * @param o
	 * @throws VODMLException
	 */
	private void validateObject(VODMLObject o) throws VODMLException {
		String dmtype = o.getDmtype();
		TypeNode type = vodmlManager.findType(dmtype);
		for (VODMLAttribute a : o.getATTRIBUTES())
			validateAttribute(a, type);
		for (VODMLReference r : o.getREFERENCES())
			validateReference(r, type);
		for (VODMLComposition c : o.getCOMPOSITIONS())
			validateComposition(c, type);
		if (o.getCONTAINER() != null)
			validateContainer(o.getCONTAINER(), (ObjectTypeNode)type);
	}

	private boolean validateDataObject(VODMLObject o) throws VODMLException {
		int inSize = annotationErrors.size();
		String dmtype = o.getDmtype();
		TypeNode type = vodmlManager.findType(dmtype);
		if (type == null) {
			annotationErrors.add(new VODMLException(String.format("No type found for dmtype '%s' ", dmtype)));
			return false;
		}

		if (!(type instanceof DataTypeNode)) {
			annotationErrors.add(new VODMLException(String.format(
			    "Validating DataObject is given a dmtype '%s' identifying a %s", dmtype, type.getClass().getName())));
			return false;
		}
		if (o.getPRIMARYKEY() != null) {
			annotationErrors.add(new VODMLException(String.format("DataObject '%s' cannot have a PRIMARYKEY", dmtype)));
		}
		for (VODMLAttribute a : o.getATTRIBUTES())
			validateAttribute(a, type);
		for (VODMLReference r : o.getREFERENCES())
			validateReference(r, type);
		return annotationErrors.size() == inSize;
	}

}
