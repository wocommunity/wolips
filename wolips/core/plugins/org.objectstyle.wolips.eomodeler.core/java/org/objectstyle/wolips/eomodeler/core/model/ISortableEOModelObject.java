package org.objectstyle.wolips.eomodeler.core.model;

public interface ISortableEOModelObject {
	public String getName();
	
	public void setName(String name) throws DuplicateNameException;
}
