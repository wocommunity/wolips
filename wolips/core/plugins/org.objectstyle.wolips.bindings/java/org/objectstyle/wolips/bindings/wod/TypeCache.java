package org.objectstyle.wolips.bindings.wod;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jdt.core.IType;
import org.objectstyle.wolips.bindings.api.ApiCache;

public class TypeCache {
	private ApiCache _apiCache;

	private HashMap<IType, Map<String, IType>> _typeContextCache;

	public TypeCache() {
		this(null);
	}

	public TypeCache(ApiCache apiCache) {
		_typeContextCache = new HashMap<IType, Map<String, IType>>();
		_apiCache = apiCache;
		if (_apiCache == null) {
			_apiCache = new ApiCache();
		}
	}

	public ApiCache getApiCache() {
		return _apiCache;
	}

	public Map<IType, Map<String, IType>> getTypeContextCache() {
		return _typeContextCache;
	}

	public void clearCache() {
		_typeContextCache.clear();
	}
}
