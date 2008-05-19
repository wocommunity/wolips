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

import java.beans.*;
import java.util.*;

/**
 * This class is an abstraction with the basic properties of a rule element.
 * Every element of a rule should extends this class.
 * 
 * @author uli
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public abstract class AbstractRuleElement {
	protected static final String CLASS_KEY = "class";

	private String assignmentClassName;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * The constructor is protected because the creation of rule elements should
	 * be done by a <code>Rule</code> object.
	 * 
	 * @param properties
	 *            A map describing this element
	 */
	protected AbstractRuleElement(final Map<String, Object> properties) {
		if (properties == null) {
			throw new IllegalArgumentException("The properties Map of this model element cannot be null");
		}

		assignmentClassName = (String) properties.get(CLASS_KEY);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	protected void firePropertyChange(PropertyChangeEvent event) {
		propertyChangeSupport.firePropertyChange(event);
	}

	protected void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
		propertyChangeSupport.firePropertyChange(propertyName, oldValue, newValue);
	}

	public String getAssignmentClassName() {
		return assignmentClassName;
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void setAssignmentClassName(final String className) {
		String oldValue = assignmentClassName;

		assignmentClassName = className;

		firePropertyChange(CLASS_KEY, oldValue, assignmentClassName);
	}

	/**
	 * Subclasses of <code>AbstractRuleElement</code> must implement this
	 * method and return a Map describing it using the key-value pattern.
	 * 
	 * @return A <code>Map</code> describing the rule element
	 */
	protected abstract Map<String, Object> toMap();
}
