package org.objectstyle.wolips.eomodeler.core.model.qualifier;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

public class EOAndQualifier extends EOAggregateQualifier {
	private List<EOQualifier> _qualifiers;

	public EOAndQualifier() {
		_qualifiers = new LinkedList<EOQualifier>();
	}

	public EOAndQualifier(Collection<EOQualifier> qualifiers) {
		_qualifiers = new LinkedList<EOQualifier>();
		for (EOQualifier qualifier : qualifiers) {
			if (qualifier instanceof EOAndQualifier) {
				_qualifiers.addAll(((EOAndQualifier) qualifier).getQualifiers());
			} else {
				_qualifiers.add(qualifier);
			}
		}
	}

	public EOAndQualifier(EOQualifier... qualifiers) {
		_qualifiers = new LinkedList<EOQualifier>();
		for (EOQualifier qualifier : qualifiers) {
			if (qualifier instanceof EOAndQualifier) {
				_qualifiers.addAll(((EOAndQualifier) qualifier).getQualifiers());
			} else {
				_qualifiers.add(qualifier);
			}
		}
	}
	
	public void addQualifier(EOQualifier qualifier) {
		_qualifiers.add(qualifier);
	}

	public List<EOQualifier> getQualifiers() {
		return _qualifiers;
	}

	public String toString(int depth) {
		StringBuffer sb = new StringBuffer();
		if (depth > 0) {
			sb.append("(");
		}
		for (int i = 0; i < _qualifiers.size(); i++) {
			EOQualifier qualifier = _qualifiers.get(i);
			if (i > 0) {
				sb.append(" and ");
			}
			if (qualifier != null) {
				sb.append(qualifier.toString(depth + 1));
			}
			else {
				sb.append("true");
			}
		}
		if (depth > 0) {
			sb.append(")");
		}
		return sb.toString();
	}
}
