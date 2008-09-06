package org.objectstyle.wolips.baseforplugins.util;

import java.util.LinkedHashMap;

public class LRUMap<K, V> extends LinkedHashMap<K, V> {

	private static final long serialVersionUID = 1L;

	private final int fMaxSize;

	public LRUMap(int maxSize) {
		super(maxSize, 0.75f, true);
		fMaxSize = maxSize;
	}

	protected boolean removeEldestEntry(java.util.Map.Entry<K, V> eldest) {
		return size() > fMaxSize;
	}
}
