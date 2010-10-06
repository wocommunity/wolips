package org.objectstyle.woenvironment.plist;

import java.util.Enumeration;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeSet;

// MS: This is java.util.Properties but with sorting added in ...
public class ToHellWithProperties extends Properties {
	private static final long serialVersionUID = 1L;

	public ToHellWithProperties() {
		super(null);
	}

	public ToHellWithProperties(Properties defaults) {
		super(defaults);
	}

	@Override
	public synchronized Enumeration<Object> keys() {
		TreeSet<Object> sortedKeys = new TreeSet<Object>(keySet());
		final Iterator<Object> sortedKeysIter = sortedKeys.iterator();
		return new Enumeration<Object>() {
			public boolean hasMoreElements() {
				return sortedKeysIter.hasNext();
			}

			public Object nextElement() {
				return sortedKeysIter.next();
			}
		};
	}
}
