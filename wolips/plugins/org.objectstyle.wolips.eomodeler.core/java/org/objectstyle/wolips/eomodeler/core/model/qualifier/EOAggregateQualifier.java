package org.objectstyle.wolips.eomodeler.core.model.qualifier;

import java.util.List;

public abstract class EOAggregateQualifier extends EOQualifier {
	public abstract void addQualifier(EOQualifier qualifier);

	public abstract List<EOQualifier> getQualifiers();
}
