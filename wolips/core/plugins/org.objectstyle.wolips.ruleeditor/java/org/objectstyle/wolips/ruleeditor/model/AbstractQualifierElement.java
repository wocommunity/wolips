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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * This class is an abstraction for qualifier elements.
 * 
 * @author uli
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public abstract class AbstractQualifierElement extends AbstractRuleElement {

	protected static final String KEY_KEY = "key";

	protected static final String QUALIFIERS_KEY = "qualifiers";

	protected static final String SELECTOR_NAME_KEY = "selectorName";

	protected static final String VALUE_KEY = "value";

	protected static final String QUALIFIER_KEY = "qualifier";

	private String key;

	private Collection<QualifierElement> qualifiers;

	private QualifierElement qualifier;

	private String selectorName;

	private LhsValue value;

	public AbstractQualifierElement(final Map<String, Object> properties) {
		super(properties);

		key = (String) properties.get(KEY_KEY);
		selectorName = (String) properties.get(SELECTOR_NAME_KEY);

		Object value = properties.get(VALUE_KEY);

		if (value != null) {
			this.value = new LhsValue(value);
		}

		Map<String, Object> anotherQualifierMap = (Map<String, Object>) properties.get(QUALIFIER_KEY);

		if (anotherQualifierMap != null) {
			this.qualifier = new QualifierElement(anotherQualifierMap);
		}

		Collection<Map<String, Object>> qualifiersMap = (Collection<Map<String, Object>>) properties.get(QUALIFIERS_KEY);

		if (qualifiersMap == null) {
			return;
		}

		qualifiers = new ArrayList<QualifierElement>(qualifiersMap.size());

		for (Object qualifierMap : qualifiersMap) {
			QualifierElement qualifier;

			if (qualifierMap instanceof Map) {
				qualifier = new QualifierElement((Map<String, Object>) qualifierMap);

			} else {
				qualifier = (QualifierElement) qualifierMap;
			}

			qualifiers.add(qualifier);
		}
	}

	public void appendToDisplayStringBuffer(final StringBuffer buffer) {
		if (Qualifier.NOT.getClassName().equals(getAssignmentClassName())) {
			buffer.append("not (");

			getQualifier().appendToDisplayStringBuffer(buffer);

			buffer.append(")");
		}

		if (getQualifiers() == null) {
			return;
		}

		buffer.append("(");

		Iterator iterator = qualifiers.iterator();

		while (iterator.hasNext()) {
			AbstractQualifierElement abstractQualifierElement = (AbstractQualifierElement) iterator.next();

			abstractQualifierElement.appendToDisplayStringBuffer(buffer);

			if (iterator.hasNext()) {
				buffer.append(" ");
				buffer.append(Qualifier.forClassName(getAssignmentClassName()).getDisplayName());
				buffer.append(" ");
			}
		}

		buffer.append(")");
	}

	public String getKey() {
		return key;
	}

	public QualifierElement getQualifier() {
		return qualifier;
	}

	public Collection<QualifierElement> getQualifiers() {
		return qualifiers;
	}

	public String getSelectorName() {
		return selectorName;
	}

	public LhsValue getValue() {
		return value;
	}

	protected void setKey(final String key) {
		this.key = key;
	}

	protected void setQualifier(final QualifierElement qualifier) {
		this.qualifier = qualifier;
	}

	protected void setQualifiers(final Collection<QualifierElement> qualifiers) {
		this.qualifiers = qualifiers;
	}

	protected void setSelectorName(final String selectorName) {
		this.selectorName = selectorName;
	}

	protected void setValue(final Object value) {
		if (value == null) {
			this.value = null;

			return;
		}

		this.value = new LhsValue(value);
	}

	@Override
	protected Map<String, Object> toMap() {
		Map<String, Object> qualifierMap = new HashMap<String, Object>();

		qualifierMap.put(CLASS_KEY, getAssignmentClassName());

		if (key != null) {
			qualifierMap.put(KEY_KEY, key);
		}

		if (selectorName != null) {
			qualifierMap.put(SELECTOR_NAME_KEY, selectorName);
		}

		if (value != null) {
			qualifierMap.put(VALUE_KEY, value.toMap());
		}

		if (qualifier != null) {
			qualifierMap.put(QUALIFIER_KEY, qualifier.toMap());
		}

		if (qualifiers != null) {
			Collection<Map<String, Object>> qualifiersArray = new ArrayList<Map<String, Object>>(qualifiers.size());

			for (QualifierElement qualifier : qualifiers) {
				qualifiersArray.add(qualifier.toMap());
			}

			qualifierMap.put(QUALIFIERS_KEY, qualifiersArray);
		}

		return qualifierMap;
	}
}
