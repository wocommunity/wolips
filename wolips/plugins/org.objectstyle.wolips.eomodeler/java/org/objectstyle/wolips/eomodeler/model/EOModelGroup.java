/*
 * ====================================================================
 * 
 * The ObjectStyle Group Software License, Version 1.0
 * 
 * Copyright (c) 2006 The ObjectStyle Group and individual authors of the
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
package org.objectstyle.wolips.eomodeler.model;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IProject;
import org.objectstyle.wolips.eomodeler.utils.URLUtils;

public class EOModelGroup extends EOModelObject {
	public static final String MODELS = "models";

	private Set myModels;

	public EOModelGroup() {
		myModels = new HashSet();
	}
	
	public boolean hasProjectWonder() {
		return containsModelNamed("erprototypes");
	}

	public Set getReferenceFailures() {
		return new HashSet();
	}

	protected void _propertyChanged(String _propertyName, Object _oldValue, Object _newValue) {
		// DO NOTHING
	}

	public Set getModels() {
		return myModels;
	}

	public Set getEntityNames() {
		Set entityNames = new TreeSet();
		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			Iterator entitiesIter = model.getEntities().iterator();
			while (entitiesIter.hasNext()) {
				EOEntity entity = (EOEntity) entitiesIter.next();
				entityNames.add(entity.getName());
			}
		}
		return entityNames;
	}

	public Set getEntities() {
		Set allEntities = new HashSet();
		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			allEntities.addAll(model.getEntities());
		}
		return allEntities;
	}

	public String findUnusedEntityName(String _newName) {
		String unusedName = _newName;
		boolean unusedNameFound = (getEntityNamed(_newName) == null);
		for (int dupeNameNum = 1; !unusedNameFound; dupeNameNum++) {
			unusedName = _newName + dupeNameNum;
			EOEntity renameEntity = getEntityNamed(unusedName);
			unusedNameFound = (renameEntity == null);
		}
		return unusedName;
	}

	public EOEntity getEntityNamed(String _entityName) {
		EOEntity matchingEntity = null;
		Iterator modelsIter = myModels.iterator();
		while (matchingEntity == null && modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			matchingEntity = model.getEntityNamed(_entityName);
		}
		return matchingEntity;
	}

	public boolean containsModelNamed(String _entityName) {
		return getModelNamed(_entityName) != null;
	}

	public void _checkForDuplicateModelName(EOModel _model, String _newName) throws DuplicateModelNameException {
		EOModel model = getModelNamed(_newName);
		if (model != null && model != _model) {
			throw new DuplicateModelNameException(_newName, this);
		}
	}

	public void addModel(EOModel _model) throws DuplicateEntityNameException, DuplicateModelNameException {
		addModel(_model, null);
	}

	public void addModel(EOModel _model, Set _failures) throws DuplicateEntityNameException, DuplicateModelNameException {
		_model._setModelGroup(this);
		_checkForDuplicateModelName(_model, _model.getName());
		Iterator entitiesIter = _model.getEntities().iterator();
		while (entitiesIter.hasNext()) {
			EOEntity entity = (EOEntity) entitiesIter.next();
			_model._checkForDuplicateEntityName(entity, entity.getName(), _failures);
		}
		myModels.add(_model);
		clearCachedPrototypes(_failures);
		firePropertyChange(EOModelGroup.MODELS, null, null);
	}

	public void removeModel(EOModel _model, Set _failures) {
		myModels.remove(_model);
		clearCachedPrototypes(_failures);
		firePropertyChange(EOModelGroup.MODELS, null, null);
		_model._setModelGroup(null);
	}

	public Set getPrototypeEntities() {
		Set prototypeEntities = new HashSet();
		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			Iterator entitiesIter = model.getEntities().iterator();
			while (entitiesIter.hasNext()) {
				EOEntity entity = (EOEntity) entitiesIter.next();
				if (entity.isPrototype()) {
					prototypeEntities.add(entity);
				}
			}
		}
		return prototypeEntities;
	}

	protected void clearCachedPrototypes(Set _failures) {
		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			model.clearCachedPrototypes(_failures, false);
		}
	}

	public EOModel getModelNamed(String _name) {
		EOModel matchingModel = null;
		Iterator modelsIter = myModels.iterator();
		while (matchingModel == null && modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			if (model.getName().equals(_name)) {
				matchingModel = model;
			}
		}
		return matchingModel;
	}

	public void addModelsFromFolder(URL _folder, Set _failures, boolean _skipOnDuplicates, IProject project) throws IOException, EOModelException {
		addModelsFromFolder(_folder, true, _failures, _skipOnDuplicates, project);
	}

	public void addModelsFromFolder(URL _folder, boolean _recursive, Set _failures, boolean _skipOnDuplicates, IProject project) throws IOException, EOModelException {
		URL[] children = URLUtils.getChildren(_folder);
		for (int childNum = 0; childNum < children.length; childNum++) {
			if (URLUtils.isFolder(children[childNum])) {
				String path = children[childNum].getPath();
				if (path.endsWith(".eomodeld") || path.endsWith(".eomodeld/")) {
					addModelFromFolder(children[childNum], _failures, _skipOnDuplicates, project);
				} else if (_recursive) {
					addModelsFromFolder(children[childNum], _recursive, _failures, _skipOnDuplicates, project);
				}
			}
		}
	}

	public EOModel addModelFromFolder(URL _folder, Set _failures, boolean _skipOnDuplicates, IProject project) throws IOException, EOModelException {
		String path = _folder.getPath();
		int lastSlashIndex = path.lastIndexOf('/', path.length() - 2);
		String name = path.substring(lastSlashIndex + 1);
		String modelName = name.substring(0, name.indexOf('.'));
		EOModel model = getModelNamed(modelName);
		if (model == null) {
			boolean reloadModel = true;
			while (reloadModel) {
				model = new EOModel(modelName, project);
				model.setModelURL(_folder);
				URL indexURL = model.getIndexURL();
				if (!URLUtils.exists(indexURL)) {
					reloadModel = false;
					_failures.add(new EOModelVerificationFailure(model, "Skipping model because " + indexURL + " does not exist.", true));
				}
				else {
					model._setModelGroup(this);
					try {
						model.loadFromFolder(_folder, _failures);
						addModel(model, _failures);
						reloadModel = false;
					} catch (DuplicateEntityNameException e) {
						e.printStackTrace();
						if (!_skipOnDuplicates) {
							throw e;
						}
						EOEntity existingEntity = e.getExistingEntity();
						EOModel existingEntityModel = existingEntity.getModel();
						_failures.add(new EOModelVerificationFailure(model, existingEntityModel.getName() + " and " + model.getName() + " both declare an entity named " + existingEntity.getName() + ", so " + existingEntityModel.getName() + " is being removed. You can create an EOModelGroup file to resolve this.", true, e));
						removeModel(existingEntityModel, _failures);
					}
				}
			}
		}
		return model;
	}

	public void verify(Set _failures) {
		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			model.verify(_failures);
		}
	}

	public void resolve(Set _failures) {
		Iterator modelsIter = myModels.iterator();
		while (modelsIter.hasNext()) {
			EOModel model = (EOModel) modelsIter.next();
			model.resolve(_failures);
		}
	}

	public String getFullyQualifiedName() {
		return "EOModelGroup";
	}

	public String toString() {
		return "[EOModelGroup: models = " + myModels + "]";
	}
}
