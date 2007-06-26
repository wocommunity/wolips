package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Map;
import java.util.Set;

public class VerificationContext {
	private Map<EOAttribute, Set<EORelationship>> _referencingRelationshipsCache;

	public VerificationContext(Map<EOAttribute, Set<EORelationship>> referencingRelationshipsCache) {
		_referencingRelationshipsCache = referencingRelationshipsCache;
	}

	public Map<EOAttribute, Set<EORelationship>> getReferencingRelationshipsCache() {
		return _referencingRelationshipsCache;
	}
}
