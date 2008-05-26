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
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

/**
 * @author uli
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 */
public class RightHandSide extends AbstractRuleElement {

	protected static final String DEFAULT_ASSIGNMENT_CLASS_NAME = "com.webobjects.directtoweb.Assignment";

	protected static final String KEY_PATH_KEY = "keyPath";

	protected static final String VALUE_KEY = "value";

	private String keyPath;

	Object value;

	protected RightHandSide() {
		this(new HashMap<String, Object>());

		setAssignmentClassName(DEFAULT_ASSIGNMENT_CLASS_NAME);
		setValue("");
		setKeyPath("");
	}

	protected RightHandSide(final Map properties) {
		super(properties);

		value = properties.get(VALUE_KEY);
		keyPath = (String) properties.get(KEY_PATH_KEY);
	}

	public String getKeyPath() {
		return keyPath;
	}

	public String getValue() {
		return valueForObject(value);
	}

	private Object parseValueString(String value) {
		value = StringUtils.remove(value, "\"");

		// When its an Array
		if (value.startsWith("(")) {
			value = StringUtils.substring(value, 1, value.length() - 1);

			String[] arrayComponents = Pattern.compile(",").split(value);

			ArrayList<Object> array = new ArrayList<Object>();

			for (String expression : arrayComponents) {
				expression = expression.trim();

				if ("".equals(expression)) {
					continue;
				}

				array.add(parseValueString(expression));
			}

			return array;
		}
		// When its a Dictionary
		else if (value.startsWith("{")) {
			Map<String, Object> dictionary = new HashMap<String, Object>();

			value = StringUtils.substring(value, 1, value.lastIndexOf(";"));

			String[] dictionaryComponents = Pattern.compile(";").split(value);

			for (String expression : dictionaryComponents) {
				String key = expression.substring(0, expression.indexOf("=")).trim();

				Object dictionaryValue = parseValueString(expression.substring(expression.indexOf("=") + 1, expression.length()).trim());

				dictionary.put(key, dictionaryValue);
			}
			return dictionary;
		}

		// So its a String
		return value;
	}

	public void setKeyPath(final String keyPath) {
		String oldValue = this.keyPath;

		this.keyPath = keyPath;

		firePropertyChange(KEY_PATH_KEY, oldValue, this.keyPath);
	}

	public void setValue(final String newValue) {
		String oldValue = getValue();

		value = parseValueString(newValue.trim());

		firePropertyChange(VALUE_KEY, oldValue, newValue);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> rhsMap = new HashMap<String, Object>();

		rhsMap.put(CLASS_KEY, getAssignmentClassName());
		rhsMap.put(KEY_PATH_KEY, getKeyPath());
		rhsMap.put(VALUE_KEY, value);

		return rhsMap;
	}

	private String valueForObject(Object object) {
		if (object == null) {
			return null;
		}

		if (object instanceof Collection) {
			Collection<Object> arrayOfValues = (Collection<Object>) object;

			if (arrayOfValues.size() == 0) {
				return "()";
			}

			StringBuffer buffer = new StringBuffer();

			buffer.append("( ");

			for (Object value : arrayOfValues) {
				buffer.append(valueForObject(value));
				buffer.append(", ");
			}

			buffer.deleteCharAt(buffer.lastIndexOf(", "));
			buffer.append(")");

			return buffer.toString();
		}

		if (object instanceof Map) {
			Map<String, Object> mapOfValues = (Map<String, Object>) object;

			StringBuffer buffer = new StringBuffer();

			buffer.append("{ ");

			ArrayList<String> list = new ArrayList<String>();

			list.addAll(mapOfValues.keySet());

			Collections.sort(list);

			for (Object key : list) {
				buffer.append("\"");
				buffer.append(key);
				buffer.append("\" = ");
				buffer.append(valueForObject(mapOfValues.get(key)));
				buffer.append("; ");
			}

			buffer.append("}");

			return buffer.toString();
		}

		return "\"" + object.toString() + "\"";
	}
}
