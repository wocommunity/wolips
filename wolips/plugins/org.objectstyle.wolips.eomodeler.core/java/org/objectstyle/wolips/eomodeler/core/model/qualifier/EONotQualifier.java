package org.objectstyle.wolips.eomodeler.core.model.qualifier;

public class EONotQualifier extends EOQualifier {
	private EOQualifier _qualifier;

	public EONotQualifier() {
		// DO NOTHING
	}

	public EONotQualifier(EOQualifier qualifier) {
		_qualifier = qualifier;
	}

	public EOQualifier getQualifier() {
		return _qualifier;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("not (");
		sb.append(_qualifier);
		sb.append(")");
		return sb.toString();
	}
}