package org.objectstyle.wolips.eomodeler.core.model;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;
import java.util.HashSet;
import org.apache.commons.collections.set.ListOrderedSet;

import org.objectstyle.wolips.eomodeler.core.wocompat.ParserDataStructureFactory;

public class EOModelParserDataStructureFactory implements ParserDataStructureFactory {

	public Collection<Object> createCollection(String _keyPath) {
		boolean createSortedSet = false;
		if ("root.attributes".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.attributesUsedForLocking".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.arguments".equals(_keyPath)) {
			createSortedSet = false;
		} else if ("root.classProperties".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.entities".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.storedProcedures".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.sharedObjectFetchSpecificationNames".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.internalInfo._clientClassPropertyNames".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.internalInfo._deletedEntityNamesInObjectStore".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.primaryKeyAttributes".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.entityIndexes".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.entityIndexes.attributes".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.relationships".equals(_keyPath)) {
			createSortedSet = true;
		} else if ("root.relationships.joins".equals(_keyPath)) {
			createSortedSet = true;
		} else if (_keyPath.startsWith("root.connectionDictionary.jdbc2Info.typeInfo.") && _keyPath.endsWith(".defaultJDBCType")) {
			createSortedSet = true;
		} else if (_keyPath.endsWith(".prefetchingRelationshipKeyPaths")) {
			createSortedSet = true;
		} else if (_keyPath.endsWith(".rawRowKeyPaths")) {
			createSortedSet = true;
		}
		Collection<Object> collection;
		if (createSortedSet) {
			//collection = new PropertyListSet<Object>();
			collection = ListOrderedSet.decorate(new HashSet<Object>());
		} else {
			collection = new LinkedList<Object>();
		}
		return collection;
	}

	public Map<Object, Object> createMap(String _keyPath) {
		return new PropertyListMap<Object, Object>();
	}

}
