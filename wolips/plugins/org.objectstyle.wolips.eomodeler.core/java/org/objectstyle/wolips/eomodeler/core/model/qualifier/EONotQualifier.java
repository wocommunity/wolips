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
	public String toString(int depth) {
		StringBuffer sb = new StringBuffer();
		sb.append("not (");
		sb.append(_qualifier.toString(depth + 1));
		sb.append(")");
		return sb.toString();
	}
}