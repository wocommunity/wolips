package org.objectstyle.wolips.ruleeditor.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public enum Selector {
	EQUAL("isEqualTo", "="), 
	GREATER_THAN("isGreaterThan", ">"), 
	GREATER_THAN_OR_EQUAL("isGreaterThanOrEqualTo", ">="), 
	LESS_THAN("isLessThan", "<"), 
	LESS_THAN_OR_EQUAL("isLessThanOrEqualTo", "<="), 
	LIKE("isLike", "like"), 
	CASE_INSENSITIVE_LIKE("isCaseInsensitiveLike", "caseInsensitiveLike"), 
	NOT_EQUAL("isNotEqualTo", "!=");

	private static final Map<String, Selector> SELECTORS_BY_NAME;

	private static final Map<String, Selector> SELECTORS_BY_OPERATOR;

	static {
		Map<String, Selector> tempMap = new HashMap<String, Selector>(7);

		tempMap.put(EQUAL.getSelectorName(), EQUAL);
		tempMap.put(NOT_EQUAL.getSelectorName(), NOT_EQUAL);
		tempMap.put(LESS_THAN.getSelectorName(), LESS_THAN);
		tempMap.put(LESS_THAN_OR_EQUAL.getSelectorName(), LESS_THAN_OR_EQUAL);
		tempMap.put(GREATER_THAN.getSelectorName(), GREATER_THAN);
		tempMap.put(GREATER_THAN_OR_EQUAL.getSelectorName(), GREATER_THAN_OR_EQUAL);
		tempMap.put(LIKE.getSelectorName(), LIKE);
		tempMap.put(CASE_INSENSITIVE_LIKE.getSelectorName(), CASE_INSENSITIVE_LIKE);

		// Support to isEqual
		tempMap.put("isEqual", EQUAL);

		SELECTORS_BY_NAME = Collections.unmodifiableMap(tempMap);

		tempMap = new HashMap<String, Selector>(7);

		tempMap.put(EQUAL.getOperator(), EQUAL);
		tempMap.put(NOT_EQUAL.getOperator(), NOT_EQUAL);
		tempMap.put(LESS_THAN.getOperator(), LESS_THAN);
		tempMap.put(LESS_THAN_OR_EQUAL.getOperator(), LESS_THAN_OR_EQUAL);
		tempMap.put(GREATER_THAN.getOperator(), GREATER_THAN);
		tempMap.put(GREATER_THAN_OR_EQUAL.getOperator(), GREATER_THAN_OR_EQUAL);
		tempMap.put(LIKE.getOperator(), LIKE);
		tempMap.put(CASE_INSENSITIVE_LIKE.getOperator(), CASE_INSENSITIVE_LIKE);

		SELECTORS_BY_OPERATOR = Collections.unmodifiableMap(tempMap);

		tempMap = null;
	}

	public static Selector forName(final String name) {
		if (name == null) {
			return EQUAL;
		}

		Selector selector = SELECTORS_BY_NAME.get(name);

		return selector;
	}

	public static Selector forOperator(final String operator) {
		if (operator == null) {
			return EQUAL;
		}

		Selector selector = SELECTORS_BY_OPERATOR.get(operator);

		if (selector == null && !operator.endsWith(":")) {
			throw new IllegalArgumentException("The operator " + operator + " is an invalid operator for a Selector");
		}

		return selector;
	}

	private final String name;

	private final String operator;

	Selector(String name, String operator) {
		this.name = name;
		this.operator = operator;
	}

	public String getOperator() {
		return operator;
	}

	public String getSelectorName() {
		return name;
	}

}
