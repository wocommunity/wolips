package org.objectstyle.wolips.baseforuiplugins.plist;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import org.objectstyle.woenvironment.plist.ParserDataStructureFactory;

public class StableDataStructureFactory implements ParserDataStructureFactory {
	public Collection<Object> createCollection(String keyPath) {
		return new LinkedList<Object>();
	}

	public Map<Object, Object> createMap(String keyPath) {
		return new LinkedHashMap<Object, Object>();
	}
}
