package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Map;
import java.util.Set;

public class VerificationContext {
	private Map<EOAttribute, Set<EORelationship>> _referencingRelationshipsCache;

	private Map<EOEntity, Set<EOEntity>> _inheritanceCache;

	public VerificationContext(EOModelGroup modelGroup) {
		_referencingRelationshipsCache = modelGroup._createReferencingRelationshipsCache();
		_inheritanceCache = modelGroup._createInheritanceCache();
	}

	public VerificationContext(EOModel model) {
		_referencingRelationshipsCache = model._createReferencingRelationshipsCache();
		_inheritanceCache = model._createInheritanceCache();
	}

	public VerificationContext(Map<EOAttribute, Set<EORelationship>> referencingRelationshipsCache, Map<EOEntity, Set<EOEntity>> inheritanceCache) {
		_referencingRelationshipsCache = referencingRelationshipsCache;
		_inheritanceCache = inheritanceCache;
	}

	public Map<EOAttribute, Set<EORelationship>> getReferencingRelationshipsCache() {
		return _referencingRelationshipsCache;
	}

	public void setInheritanceCache(Map<EOEntity, Set<EOEntity>> inheritanceCache) {
		_inheritanceCache = inheritanceCache;
	}

	public Map<EOEntity, Set<EOEntity>> getInheritanceCache() {
		return _inheritanceCache;
	}
}
