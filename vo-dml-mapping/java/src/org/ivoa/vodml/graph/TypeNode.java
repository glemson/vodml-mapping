package org.ivoa.vodml.graph;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;

import org.ivoa.vodml.xsd.jaxb.Constraint;
import org.ivoa.vodml.xsd.jaxb.ElementRef;
import org.ivoa.vodml.xsd.jaxb.SubsettedRole;
import org.ivoa.vodml.xsd.jaxb.Type;

public abstract class TypeNode extends ElementNode{

	/** the VOTable element represented by this TypNode */
	private Hashtable<String, RoleNode> roles;
	private Hashtable<String, String> subsettedRoles;

	private ArrayList<TypeNode> subClasses;
	public TypeNode(Type _e, ModelGraph _vodml) {
		super(_e, _vodml);
		roles = new Hashtable<String, RoleNode>();
		subsettedRoles = new Hashtable<String, String>();
		subClasses = new ArrayList<TypeNode>();
	  for(Constraint c :_e.getConstraint()){
	  	if(c instanceof SubsettedRole){
	  		SubsettedRole sr=(SubsettedRole)c;
	  		ElementRef er = sr.getRole();
	  		subsettedRoles.put(er.getVodmlRef(),sr.getDatatype().getVodmlRef());
	  	}
	  }
	}
	/**
	 * Return vodml-ref of datatype of role
	 * @param role
	 * @return
	 */
	public String roleDatatype(RoleNode role){
		String sr = subsettedRoles.get(role.getVODMLREF());
		if(sr != null)
			return sr;
		else if(getSuperType() != null)
			return getSuperType().roleDatatype(role);
		else
			return role.getDatatype().getVODMLREF();
	}
	
	public Type getType()
	{
		return (Type)getElement();
	}
	
	protected void addRole(RoleNode role)
	{
		roles.put(role.getVODMLREF(), role);
	}
	private void addSubClass(TypeNode sc)
	{
		this.subClasses.add(sc);
	}
	protected void addRoles()
	{
	}

	public void resolveRoles()
	{
	}
	public TypeNode getSuperType() {
		
		return getType().getExtends() == null?null:getVODML().findType(getType().getExtends().getVodmlRef());
	}

	public RoleNode getRole(String dmrole)
	{
		return roles.get(dmrole);
	}

	/**
	 * Check whether role is available on the current Type.<br/>
	 * True when defined on type, or on a base type.
	 * @param utype
	 * @return
	 */
	public boolean isRoleAvailable(String utype)
	{
		RoleNode role = getRole(utype);
		if(role != null)
			return true;
		else if(getSuperType() != null)
			return getSuperType().isRoleAvailable(utype);
		else
			return false;
	}
	public boolean isRoleAvailable(RoleNode role)
	{
		return isRoleAvailable(role.getVODMLREF());
	}
	/**
	 * Return true if the current type is equal to or a subclass of the specified type.<br/>
	 * @param other
	 * @return
	 */
	public boolean isSubClassOrSelf(TypeNode other)
	{
		if(other == this)
			return true;
		else if(other == null)
			return false; // TODO TBD whether an illegalargumentexception should be thrown
		else if(getSuperType() == null)
			return false;
		else
			return getSuperType().isSubClassOrSelf(other);
	}
	/**
	 * Return true if the current type is equal to or a base class of the specified type.<br/>
	 * @param other
	 * @return
	 */
	public boolean isBaseClassOrSelf(TypeNode other)
	{
		if(other == null)
			return false; //TODO TBD whether an illegalargumentexception should be thrown
		return other.isSubClassOrSelf(this);
	}

	public Iterator<TypeNode> getSubClasses() {
		return subClasses.iterator();
	}

}
