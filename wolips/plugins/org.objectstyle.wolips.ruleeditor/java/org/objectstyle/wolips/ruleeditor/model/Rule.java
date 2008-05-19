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

import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * @author uli
 */
public class Rule extends AbstractRuleElement {

	protected static final String AUTHOR_KEY = "author";

	protected static final String DEFAULT_ASSIGNMENT_CLASS_NAME = "com.webobjects.directtoweb.Rule";

	protected static final String DEFAULT_AUTHOR = "100";

	protected static final String LHS_KEY = "lhs";

	protected static final String RHS_KEY = "rhs";

	private String author;

	private final LeftHandSide leftHandSide;

	private final RightHandSide rightHandSide;

	protected Rule() {
		super(new HashMap<String, Object>());

		setAssignmentClassName(DEFAULT_ASSIGNMENT_CLASS_NAME);
		setAuthor(DEFAULT_AUTHOR);

		leftHandSide = new LeftHandSide();

		rightHandSide = new RightHandSide();
	}

	protected Rule(final Map properties) {
		super(properties);

		Map<String, Object> lhsProperties = (Map<String, Object>) properties.get(LHS_KEY);

		if (lhsProperties == null) {
			leftHandSide = new LeftHandSide();
		} else {
			leftHandSide = new LeftHandSide(lhsProperties);
		}

		properties.remove(LHS_KEY);

		Map<String, Object> rhsProperties = (Map<String, Object>) properties.get(RHS_KEY);

		rightHandSide = new RightHandSide(rhsProperties);

		properties.remove(RHS_KEY);

		setAuthor(properties.get(AUTHOR_KEY).toString());
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		super.addPropertyChangeListener(listener);

		leftHandSide.addPropertyChangeListener(listener);
		rightHandSide.addPropertyChangeListener(listener);
	}

	public String getAuthor() {
		return author;
	}

	public LeftHandSide getLeftHandSide() {
		return leftHandSide;
	}

	public RightHandSide getRightHandSide() {
		return rightHandSide;
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		super.removePropertyChangeListener(listener);

		leftHandSide.removePropertyChangeListener(listener);
		rightHandSide.removePropertyChangeListener(listener);
	}

	public void setAuthor(final String author) {
		String oldValue = this.author;

		this.author = author;

		firePropertyChange(AUTHOR_KEY, oldValue, this.author);
	}

	@Override
	public Map<String, Object> toMap() {
		Map<String, Object> ruleMap = new HashMap<String, Object>();

		ruleMap.put(CLASS_KEY, getAssignmentClassName());
		ruleMap.put(AUTHOR_KEY, getAuthor());

		Map<String, Object> lhsMap = leftHandSide.toMap();

		ruleMap.put(Rule.LHS_KEY, lhsMap);

		Map<String, Object> rhsMap = rightHandSide.toMap();

		ruleMap.put(Rule.RHS_KEY, rhsMap);

		return ruleMap;
	}
}
