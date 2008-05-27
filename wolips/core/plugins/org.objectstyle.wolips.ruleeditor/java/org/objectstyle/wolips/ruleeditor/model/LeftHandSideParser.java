/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2005 The ObjectStyle Group and individual authors of the
 * software. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * 3. The end-user documentation included with the redistribution, if any, must
 * include the following acknowlegement: "This product includes software
 * developed by the ObjectStyle Group (http://objectstyle.org/)." Alternately,
 * this acknowlegement may appear in the software itself, if and wherever such
 * third-party acknowlegements normally appear.
 * 
 * 4. The names "ObjectStyle Group" and "Cayenne" must not be used to endorse or
 * promote products derived from this software without prior written permission.
 * For written permission, please contact andrus@objectstyle.org.
 * 
 * 5. Products derived from this software may not be called "ObjectStyle" nor
 * may "ObjectStyle" appear in their names without prior written permission of
 * the ObjectStyle Group.
 * 
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
 * OBJECTSTYLE GROUP OR ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
 * INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE,
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 * 
 * This software consists of voluntary contributions made by many individuals on
 * behalf of the ObjectStyle Group. For more information on the ObjectStyle
 * Group, please see <http://objectstyle.org/>.
 *  
 */
package org.objectstyle.wolips.ruleeditor.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class LeftHandSideParser {
	private static class RegularExpressionTokenizer implements Iterator<String> {

		private String delim;

		private final CharSequence input;

		private int lastEnd = 0;

		private String match;

		private Matcher matcher;

		private final boolean returnDelims;

		public RegularExpressionTokenizer(CharSequence input, String patternStr, boolean returnDelims) {
			this.input = input;
			this.returnDelims = returnDelims;

			Pattern pattern = Pattern.compile(patternStr);
			matcher = pattern.matcher(input);
		}

		public boolean hasNext() {
			if (matcher == null) {
				return false;
			}
			if (delim != null || match != null) {
				return true;
			}
			if (matcher.find()) {
				if (returnDelims) {
					delim = input.subSequence(lastEnd, matcher.start()).toString();
				}
				match = matcher.group();
				lastEnd = matcher.end();
			} else if (returnDelims && lastEnd < input.length()) {
				delim = input.subSequence(lastEnd, input.length()).toString();
				lastEnd = input.length();

				matcher = null;
			}
			return delim != null || match != null;
		}

		public boolean isNextToken() {
			return delim == null && match != null;
		}

		public String next() {
			String result = null;

			if (delim != null) {
				result = delim;
				delim = null;
			} else if (match != null) {
				result = match;
				match = null;
			}
			return result;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static final String AND_OR_NOT_PATTERN;

	private static final String INTERNAL_QUALIFIER_PREFIX = "$INTERNAL_QUALIFIER_";

	private static final String OPERATORS_PATTERN = "(=|!=|>|<|>=|<=|like){1}";

	static {
		StringBuffer buffer = new StringBuffer();

		buffer.append("^");
		buffer.append(Qualifier.NOT.getDisplayName());
		buffer.append("|(\\s+");
		buffer.append(Qualifier.AND.getDisplayName());
		buffer.append("\\s+|\\s+");
		buffer.append(Qualifier.OR.getDisplayName());
		buffer.append("\\s+)+");

		// Pattern "^not|(\s+and\s+|\s+or\s+)+"
		AND_OR_NOT_PATTERN = buffer.toString();
	}

	private static String getClassFromMap(Map<String, Object> properties) {
		return (String) properties.get(AbstractRuleElement.CLASS_KEY);
	}

	private static Collection<QualifierElement> getQualifiersFromMap(Map<String, Object> map) {
		return (Collection<QualifierElement>) map.get(AbstractQualifierElement.QUALIFIERS_KEY);
	}

	private static Map<String, Object> propertiesForOneQualifier(String expression) {
		// Handle one qualifier
		Iterator<String> tokenizer = new RegularExpressionTokenizer(expression, OPERATORS_PATTERN, true);

		tokenizer.hasNext();
		String key = tokenizer.next().trim().replaceAll("\\(", "");

		tokenizer.hasNext();
		String operator = tokenizer.next();

		tokenizer.hasNext();
		String value = tokenizer.next().replaceAll("(\'|\"|\\))", "").trim();

		Map<String, Object> properties = new HashMap<String, Object>();

		properties.put(AbstractRuleElement.CLASS_KEY, Qualifier.KEY_VALUE.getClassName());
		properties.put(AbstractQualifierElement.KEY_KEY, key);
		properties.put(AbstractQualifierElement.SELECTOR_NAME_KEY, Selector.forOperator(operator).getSelectorName());
		properties.put(AbstractQualifierElement.VALUE_KEY, value);

		return properties;
	}

	private static void putQualifiersIntoMap(Map<String, Object> map, Collection<QualifierElement> qualifiers) {
		map.put(AbstractQualifierElement.QUALIFIERS_KEY, qualifiers);
	}

	private int count = 0;

	private final Map<String, String> qualifierSubstitutionMap = new HashMap<String, String>();

	private Map<String, String> createParseMap(String expression) {
		Map<String, String> expressionAsMap = new TreeMap<String, String>();

		return null;
	}

	private Map<String, Object> handleNotQualifier(String conditionsToParse) {
		if (hasNotQualifier(conditionsToParse)) {
			String notPattern = "not\\s*\\(.*\\)";

			Iterator<String> tokenizer = new RegularExpressionTokenizer(conditionsToParse, notPattern, true);

			tokenizer.hasNext();
			String notText = tokenizer.next();

			if (tokenizer.hasNext()) {
				notText = tokenizer.next().trim();

				notText = notText.substring(3);

				Map<String, Object> qualifier = parse(notText);

				Map<String, Object> properties = new HashMap<String, Object>();

				properties.put(AbstractRuleElement.CLASS_KEY, Qualifier.NOT.getClassName());

				properties.put(AbstractQualifierElement.QUALIFIER_KEY, qualifier);

				return properties;
			}
		}

		return null;
	}

	private String handleParenthesis(final String expression) {
		String result = expression;

		for (; shouldHandleParenthesis(result); count++) {
			if (result.startsWith("(") && result.endsWith(")")) {
				result = result.substring(1, result.length() - 1);
			}

			// Handle parenthesis
			String parenthesisPattern = "\\([^\\(\\)]+\\)";

			Iterator<String> tokenizer = new RegularExpressionTokenizer(result, parenthesisPattern, false);

			tokenizer.hasNext();

			String internalQualifier = tokenizer.next();

			result = StringUtils.replace(result, internalQualifier, INTERNAL_QUALIFIER_PREFIX + count + "$");

			qualifierSubstitutionMap.put(INTERNAL_QUALIFIER_PREFIX + count + "$", internalQualifier);
		}

		return result;
	}

	private boolean hasMoreThanOneQualifier(String expression) {
		Pattern pattern = Pattern.compile(AND_OR_NOT_PATTERN);
		Matcher matcher = pattern.matcher(expression);

		return matcher.find();
	}

	private boolean hasNotQualifier(String expression) {
		int indexOfNot = expression.indexOf(Qualifier.NOT.getDisplayName());
		int indexOfOr = expression.indexOf(Qualifier.OR.getDisplayName());
		int indexOfAnd = expression.indexOf(Qualifier.AND.getDisplayName());

		if (indexOfNot < 0) {
			return false;
		}

		if (indexOfAnd >= 0) {
			if (indexOfNot < indexOfAnd) {
				if (indexOfOr >= 0) {
					return indexOfNot < indexOfOr;
				}
			}

			return false;
		}

		if (indexOfOr >= 0) {
			return indexOfNot < indexOfOr;
		}

		return true;
	}

	private boolean isQualifier(String expression) {
		return Qualifier.AND.getDisplayName().equals(expression) || Qualifier.OR.getDisplayName().equals(expression);
	}

	public Map<String, Object> parse(final String conditions) {
		if (conditions == null) {
			return Collections.emptyMap();
		}

		String trimmedConditions = conditions.trim();

		if ("".equals(trimmedConditions)) {
			return Collections.emptyMap();
		}

		String conditionsToParse = qualifierSubstitutionMap.get(trimmedConditions);

		if (conditionsToParse == null) {
			conditionsToParse = trimmedConditions;
		}

		if (!hasMoreThanOneQualifier(conditionsToParse)) {
			return propertiesForOneQualifier(conditionsToParse);
		}

		Map<String, Object> properties = handleNotQualifier(conditionsToParse);

		if (properties != null) {
			return properties;
		}

		conditionsToParse = handleParenthesis(conditionsToParse);

		return propertiesForManyQualifiers(conditionsToParse);

	}

	private Map<String, Object> propertiesForManyQualifiers(String conditionsToParse) {
		Map<String, Object> properties = new HashMap<String, Object>();

		Iterator<String> tokenizer = new RegularExpressionTokenizer(conditionsToParse, AND_OR_NOT_PATTERN, true);

		Collection<QualifierElement> qualifiers = new ArrayList<QualifierElement>();

		putQualifiersIntoMap(properties, qualifiers);

		while (tokenizer.hasNext()) {
			String expression = tokenizer.next().trim();

			Map<String, Object> returnedMap;

			if (isQualifier(expression)) {
				String qualifierDisplayName = expression;

				tokenizer.hasNext();
				expression = tokenizer.next();

				String assignmentClassName = getClassFromMap(properties);

				if (assignmentClassName != null && !assignmentClassName.equals(Qualifier.forDisplayName(qualifierDisplayName).getClassName())) {
					returnedMap = parse(expression);

					QualifierElement qualifier = new QualifierElement(returnedMap);

					QualifierElement wrapperQualifier = new QualifierElement(properties);

					properties = new HashMap<String, Object>();

					putQualifiersIntoMap(properties, new ArrayList<QualifierElement>());

					properties.put(AbstractRuleElement.CLASS_KEY, Qualifier.forDisplayName(qualifierDisplayName).getClassName());

					getQualifiersFromMap(properties).add(wrapperQualifier);
					getQualifiersFromMap(properties).add(qualifier);
				} else {
					properties.put(AbstractRuleElement.CLASS_KEY, Qualifier.forDisplayName(qualifierDisplayName).getClassName());

					returnedMap = parse(expression);

					QualifierElement qualifier = new QualifierElement(returnedMap);

					getQualifiersFromMap(properties).add(qualifier);

				}
			} else {
				returnedMap = parse(expression);

				QualifierElement qualifier = new QualifierElement(returnedMap);

				getQualifiersFromMap(properties).add(qualifier);
			}
		}

		return properties;
	}

	private boolean shouldHandleParenthesis(String expression) {
		Pattern pattern = Pattern.compile(".*\\(.*\\(");
		Matcher matcher = pattern.matcher(expression);

		return matcher.find();
	}
}
