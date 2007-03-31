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

public class EOModelGroup extends EOModelObject<Object> {
	public static final String MODELS = "models";

	private Set<EOModel> myModels;

	public EOModelGroup() {
		myModels = new HashSet<EOModel>();
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

	public Set<EOModel> getModels() {
		return myModels;
	}

	public Set<String> getEntityNames() {
		Set<String> entityNames = new TreeSet<String>();
		for (EOModel model : myModels) {
			for (EOEntity entity : model.getEntities()) {
				entityNames.add(entity.getName());
			}
		}
		return entityNames;
	}

	public Set<EOEntity> getEntities() {
		Set<EOEntity> allEntities = new HashSet<EOEntity>();
		for (EOModel model : myModels) {
			allEntities.addAll(model.getEntities());
		}
		return allEntities;
	}

	public String findUnusedEntityName(String _newName) {
		return _findUnusedName(_newName, "getEntityNamed");
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

	public void addModel(EOModel _model, Set<EOModelVerificationFailure> _failures) throws DuplicateEntityNameException, DuplicateModelNameException {
		_model._setModelGroup(this);
		_checkForDuplicateModelName(_model, _model.getName());
		for (EOEntity entity : _model.getEntities()) {
			_model._checkForDuplicateEntityName(entity, entity.getName(), _failures);
		}
		myModels.add(_model);
		clearCachedPrototypes(_failures);
		firePropertyChange(EOModelGroup.MODELS, null, null);
	}

	public void removeModel(EOModel _model, Set<EOModelVerificationFailure> _failures) {
		myModels.remove(_model);
		clearCachedPrototypes(_failures);
		firePropertyChange(EOModelGroup.MODELS, null, null);
		_model._setModelGroup(null);
	}

	public Set<EOEntity> getPrototypeEntities() {
		Set<EOEntity> prototypeEntities = new HashSet<EOEntity>();
		for (EOModel model : myModels) {
			for (EOEntity entity : model.getEntities()) {
				if (entity.isPrototype()) {
					prototypeEntities.add(entity);
				}
			}
		}
		return prototypeEntities;
	}

	protected void clearCachedPrototypes(Set<EOModelVerificationFailure> _failures) {
		for (EOModel model : myModels) {
			model.clearCachedPrototypes(_failures, false);
		}
	}

	public EOModel getModelNamed(String _name) {
		EOModel matchingModel = null;
		Iterator<EOModel> modelsIter = myModels.iterator();
		while (matchingModel == null && modelsIter.hasNext()) {
			EOModel model = modelsIter.next();
			if (model.getName().equals(_name)) {
				matchingModel = model;
			}
		}
		return matchingModel;
	}

	public void addModelsFromFolder(URL _folder, Set<EOModelVerificationFailure> _failures, boolean _skipOnDuplicates, IProject project) throws IOException, EOModelException {
		addModelsFromFolder(_folder, true, _failures, _skipOnDuplicates, project);
	}

	public void addModelsFromFolder(URL _folder, boolean _recursive, Set<EOModelVerificationFailure> _failures, boolean _skipOnDuplicates, IProject project) throws IOException, EOModelException {
		for (URL childURL : URLUtils.getChildren(_folder)) {
			if (URLUtils.isFolder(childURL)) {
				String path = childURL.getPath();
				if (path.endsWith(".eomodeld") || path.endsWith(".eomodeld/")) {
					addModelFromFolder(childURL, _failures, _skipOnDuplicates, project);
				} else if (_recursive) {
					addModelsFromFolder(childURL, _recursive, _failures, _skipOnDuplicates, project);
				}
			}
		}
	}

	public EOModel addModelFromFolder(URL _folder, Set<EOModelVerificationFailure> _failures, boolean _skipOnDuplicates, IProject project) throws IOException, EOModelException {
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
				} else {
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

	public void verify(Set<EOModelVerificationFailure> _failures) {
		for (EOModel model : myModels) {
			model.verify(_failures);
		}
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		for (EOModel model : myModels) {
			model.resolve(_failures);
		}
	}

	public String getFullyQualifiedName() {
		return "EOModelGroup";
	}

	public String toString() {
		return "[EOModelGroup: models = " + myModels + "]";
	}
	
	@Override
	public String getName() {
		return "EOModelGroup";
	}
	
	@Override
	public EOModelGroup _cloneModelObject() {
		throw new IllegalStateException("Not implemented");
	}

	@Override
	public void _addToModelParent(Object modelParent, boolean findUniqueName, Set<EOModelVerificationFailure> failures) {
		// DO NOTHING
	}

	@Override
	public Class<Object> _getModelParentType() {
		return null;
	}
	
	@Override
	public Object _getModelParent() {
		return null;
	}

	@Override
	public void _removeFromModelParent(Set<EOModelVerificationFailure> failures) {
		// DO NOTHING
	}
}
