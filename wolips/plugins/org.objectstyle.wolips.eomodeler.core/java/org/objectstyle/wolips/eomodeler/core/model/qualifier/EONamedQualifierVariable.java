package org.objectstyle.wolips.eomodeler.core.model.qualifier;

public class EONamedQualifierVariable extends EOQualifierVariable {
	public EONamedQualifierVariable(String name) {
		super(name);
	}

	public String toString() {
		return "$" + getName();
	}
}
