package org.objectstyle.wolips.ruleeditor.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum Qualifier {
	AND("and", "com.webobjects.eocontrol.EOAndQualifier"), KEY_VALUE("", "com.webobjects.eocontrol.EOKeyValueQualifier"), NOT("not", "com.webobjects.eocontrol.EONotQualifier"), OR("or", "com.webobjects.eocontrol.EOOrQualifier");

	private static final Map<String, Qualifier> QUALIFIERS_BY_CLASS_NAME;

	private static final Map<String, Qualifier> QUALIFIERS_BY_DISPLAY_NAME;

	static {
		Map<String, Qualifier> tempMap = new HashMap<String, Qualifier>(4);

		tempMap.put(AND.getClassName(), AND);
		tempMap.put(KEY_VALUE.getClassName(), KEY_VALUE);
		tempMap.put(NOT.getClassName(), NOT);
		tempMap.put(OR.getClassName(), OR);

		QUALIFIERS_BY_CLASS_NAME = Collections.unmodifiableMap(tempMap);

		tempMap = new HashMap<String, Qualifier>(4);

		tempMap.put(AND.getDisplayName(), AND);
		tempMap.put(KEY_VALUE.getDisplayName(), KEY_VALUE);
		tempMap.put(NOT.getDisplayName(), NOT);
		tempMap.put(OR.getDisplayName(), OR);

		QUALIFIERS_BY_DISPLAY_NAME = Collections.unmodifiableMap(tempMap);

		tempMap = null;
	}

	public static Qualifier forClassName(String className) {
		if (className == null) {
			return KEY_VALUE;
		}

		Qualifier qualifier = QUALIFIERS_BY_CLASS_NAME.get(className);

		if (qualifier == null) {
			throw new IllegalArgumentException("The className " + className + " is an invalid className for a Qualifier");
		}

		return qualifier;
	}

	public static Qualifier forDisplayName(String displayName) {
		if (displayName == null) {
			return KEY_VALUE;
		}

		Qualifier qualifier = QUALIFIERS_BY_DISPLAY_NAME.get(displayName);

		if (qualifier == null) {
			throw new IllegalArgumentException("The display name " + displayName + " is an invalid display name for a Qualifier");
		}

		return qualifier;

	}

	private final String className;

	private final String displayName;

	Qualifier(String displayName, String className) {
		this.className = className;
		this.displayName = displayName;
	}

	public String getClassName() {
		return className;
	}

	public String getDisplayName() {
		return displayName;
	}
}
