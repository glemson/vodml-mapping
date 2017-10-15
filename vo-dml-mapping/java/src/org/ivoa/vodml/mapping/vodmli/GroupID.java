package org.ivoa.vodml.mapping.vodmli;

import org.ivoa.vodml.model.StringsIdentifier;

public class GroupID extends StringsIdentifier {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3863060126561711924L;

	public GroupID(String id)
	{
		super(id);
	}

	@Override
	public StringBuilder deepToString(StringBuilder sb, String offset) {
		 super.deepToString(sb, offset);
		 sb.append(offset).append("<source>GROUP/@ID</source>");
		 return sb;
	}
	
	
}
