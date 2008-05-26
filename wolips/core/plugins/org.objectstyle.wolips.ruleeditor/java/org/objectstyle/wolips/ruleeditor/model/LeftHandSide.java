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

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

/**
 * @author uli
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class LeftHandSide extends AbstractQualifierElement {

	public static final String EMPTY_LHS_VALUE = "*true*";

	/**
	 * The constructor is protected because users shouldn't create
	 * <code>LeftHandSide</code> objects by themselves.
	 */
	protected LeftHandSide() {
		super(Collections.EMPTY_MAP);
	}

	/**
	 * The constructor is protected because users shouldn't create
	 * <code>LeftHandSide</code> objects by themselves.
	 * 
	 * @param lhsProperties
	 *            A map describing the <code>LeftHandSide</code> object to be
	 *            created
	 */
	protected LeftHandSide(Map<String, Object> lhsProperties) {
		super(lhsProperties);
	}

	protected boolean isEmpty() {
		return getAssignmentClassName() == null && getKey() == null && getValue() == null && getSelectorName() == null && getQualifiers() == null;
	}

	public void setConditions(final String conditions) {
		String oldValue = toString();
		LeftHandSideParser parser = new LeftHandSideParser();

		Map<String, Object> properties = parser.parse(conditions);

		setAssignmentClassName((String) properties.get(CLASS_KEY));
		setKey((String) properties.get(KEY_KEY));
		setValue((String) properties.get(VALUE_KEY));
		setSelectorName((String) properties.get(SELECTOR_NAME_KEY));
		setQualifiers((Collection<QualifierElement>) properties.get(QUALIFIERS_KEY));

		firePropertyChange("LEFT_HAND_SIDE", oldValue, toString());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.objectstyle.wolips.ruleeditor.model.AbstractQualifierElement#toMap()
	 */
	@Override
	public Map<String, Object> toMap() {
		if (isEmpty()) {
			return null;
		}

		return super.toMap();
	}

	/**
	 * Returns the string representation of this <code>LeftHandSide</code>
	 * object.
	 * <p>
	 * If the conditions property is empty, returns the "*true*" string.
	 * <p>
	 * If the conditions property contains nested qualifiers, returns the
	 * qualifiers separated by parenthesis. i.e. (task = 'edit' and (entity.name =
	 * 'MyEntity' or entity.name = 'MyOtherEntity')).
	 * 
	 * @see java.lang.Object#toString()
	 * @return Returns the <code>String</code> representation of this object
	 */
	@Override
	public String toString() {
		if (isEmpty()) {
			return EMPTY_LHS_VALUE;
		}

		StringBuffer buffer = new StringBuffer();

		if (getQualifiers() == null) {
			buffer.append(getKey());
			buffer.append(" ");

			Selector selector = Selector.forName(getSelectorName());

			buffer.append(selector.getOperator());
			buffer.append(" '");
			buffer.append(getValue());
			buffer.append("'");
		} else {
			appendToDisplayStringBuffer(buffer);
		}

		return buffer.toString();
	}
}
