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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.objectstyle.wolips.baseforplugins.plist.WOLPropertyListSerialization;

/**
 * This class is the base class to work with d2wmodel files.
 * 
 * @author uli
 * @author <a href="mailto:frederico@moleque.com.br">Frederico Lellis</a>
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 * @author <a href="mailto:hprange@moleque.com.br">Henrique Prange</a>
 */
public class D2WModel implements PropertyChangeListener {

	private static final int NUMBER_OF_RULES_KEY = 1;

	private static final String RULES_LIST_KEY = "rules";

	boolean hasUnsavedChanges = false;

	private final File modelFile;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	private final Collection<Rule> rules;

	/**
	 * The D2WModel is created based on a file. An empty file or an existing
	 * d2wmodel file could be passed as a parameter.
	 * 
	 * @param modelFile
	 *            An empty or existing d2wmodel file
	 */
	public D2WModel(File modelFile) {
		if (modelFile == null) {
			throw new IllegalArgumentException("The URL for the d2wmodel file cannot be null");
		}

		this.modelFile = modelFile;

		Map<String, Collection<Map>> modelMap = loadModel();

		rules = modelMapToRules(modelMap);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	public void addRule(final Rule newRule) {
		newRule.addPropertyChangeListener(this);

		getRules().add(newRule);

		setHasUnsavedChanges(true);
	}

	/**
	 * Copy the rule received into this D2WModel.
	 * 
	 * @param rule
	 */
	public void copyRule(Rule rule) {
		if (rule == null) {
			return;
		}

		Rule duplicateRule = new Rule(rule);

		addRule(duplicateRule);
	}

	/**
	 * Use this method to create new empty rules for this d2wmodel.
	 * 
	 * @return The newly created rule
	 */
	public Rule createEmptyRule() {
		Rule rule = new Rule();

		addRule(rule);

		return rule;
	}

	public String getModelPath() {
		return modelFile.toString();
	}

	public Collection<Rule> getRules() {
		return rules;
	}

	/**
	 * Tell if the model has changes not saved.
	 * 
	 * @return Returns <code>true</code> if the d2wmodel was changed or
	 *         <code>false</code> otherwise
	 */
	public boolean hasUnsavedChanges() {
		return hasUnsavedChanges;
	}

	protected Map<String, Collection<Map>> loadModel() {
		Map<String, Collection<Map>> modelMap = null;

		try {
			modelMap = (Map<String, Collection<Map>>) WOLPropertyListSerialization.propertyListFromFile(modelFile);
		} catch (Exception exception) {
			throw new IllegalArgumentException("The file " + modelFile + " cannot be found");
		}

		if (modelMap == null) {
			modelMap = new HashMap<String, Collection<Map>>();

			modelMap.put(RULES_LIST_KEY, new ArrayList<Map>());
		}

		return modelMap;

	}

	private Collection<Rule> modelMapToRules(Map<String, Collection<Map>> modelMap) {
		Collection<Map> rulesAsMaps = modelMap.get(RULES_LIST_KEY);

		Collection<Rule> rules = new ArrayList<Rule>(rulesAsMaps.size());

		for (Map ruleAsMap : rulesAsMaps) {
			Rule rule = new Rule(ruleAsMap);

			rule.addPropertyChangeListener(this);

			rules.add(rule);
		}

		return rules;
	}

	public void propertyChange(PropertyChangeEvent event) {
		setHasUnsavedChanges(true);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	public void removeRule(final Rule rule) {
		rule.removePropertyChangeListener(this);

		getRules().remove(rule);

		setHasUnsavedChanges(true);
	}

	private Map<String, Collection<Map>> rulesToModelMap() {
		Map<String, Collection<Map>> modelMap = new HashMap<String, Collection<Map>>(NUMBER_OF_RULES_KEY);

		Collection<Map> rulesArray = new ArrayList<Map>();

		for (Rule rule : rules) {

			Map<String, Object> ruleMap = rule.toMap();

			rulesArray.add(ruleMap);
		}

		modelMap.put(RULES_LIST_KEY, rulesArray);

		return modelMap;
	}

	/**
	 * Stores changes made to this object in the underlying d2wmodel file.
	 */
	public void saveChanges() {
		Map<String, Collection<Map>> modelMap = rulesToModelMap();

		try {
			WOLPropertyListSerialization.propertyListToFile("", modelFile, modelMap);

			setHasUnsavedChanges(false);

		} catch (Exception exception) {
			exception.printStackTrace();
		}
	}

	private void setHasUnsavedChanges(boolean newValue) {
		boolean oldValue = hasUnsavedChanges;

		hasUnsavedChanges = newValue;

		propertyChangeSupport.firePropertyChange("HAS_UNSAVED_CHANGES", oldValue, hasUnsavedChanges);
	}
}
