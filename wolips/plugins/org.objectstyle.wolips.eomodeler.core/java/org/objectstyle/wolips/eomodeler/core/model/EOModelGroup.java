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
package org.objectstyle.wolips.eomodeler.core.model;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.jar.JarEntry;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.objectstyle.wolips.baseforplugins.util.URLUtils;
import org.objectstyle.wolips.eomodeler.core.Activator;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListParserException;
import org.objectstyle.wolips.eomodeler.core.wocompat.PropertyListSerialization;

public class EOModelGroup extends EOModelObject<Object> {
	public static final String MODELS = "models";

	private Set<EOModel> _models;

	private boolean _createDefaultDatabaseConfig;

	private String _editingModelName;

	private boolean _dirty;

	public EOModelGroup() {
		_models = new HashSet<EOModel>();
		// _createDefaultDatabaseConfig = true;
		_createDefaultDatabaseConfig = false;
	}

	public void setCreateDefaultDatabaseConfig(boolean createDefaultDatabaseConfig) {
		_createDefaultDatabaseConfig = createDefaultDatabaseConfig;
	}

	public boolean isCreateDefaultDatabaseConfig() {
		return _createDefaultDatabaseConfig;
	}

	public boolean hasProjectWonder() {
		return containsModelNamed("erprototypes");
	}

	public Set<EOModelReferenceFailure> getReferenceFailures() {
		return new HashSet<EOModelReferenceFailure>();
	}

	public void setDirty(boolean dirty) {
		_dirty = dirty;
	}

	public boolean isDirty() {
		boolean dirty = false;
		for (EOModel model : getModels()) {
			try {
				if (model.isDirty() && model.canSave()) {
					dirty = true;
					break;
				}
			} catch (MalformedURLException e) {
				Activator.getDefault().log("Unable to determine if the model " + model.getName() + " was dirty, so we skipped it.", e);
			}
		}
		return dirty;
	}

	@SuppressWarnings("unused")
	protected void _modelChanged(@SuppressWarnings("unused")
	EOModel model, String propertyName, Object oldValue, Object newValue) {
		if (EOModel.DIRTY.equals(propertyName)) {
			boolean oldDirty = _dirty;
			boolean dirty = isDirty();
			firePropertyChange(propertyName, Boolean.valueOf(oldDirty), Boolean.valueOf(dirty));
			_dirty = dirty;
		}
	}

	@Override
	protected void _propertyChanged(String name, Object value, Object value2) {
		// DO NOTHING
	}

	public Set<EOModel> getModels() {
		return _models;
	}

	public Set<EOModel> getSortedModels() {
		return new PropertyListSet<EOModel>(_models);
	}

	/**
	 * Returns the list of names of entities that are not prototypes.
	 * 
	 * @return list of entity names
	 */
	public Set<String> getNonPrototypeEntityNames() {
		Set<String> entityNames = new TreeSet<String>();
		for (EOModel model : _models) {
			for (EOEntity entity : model.getEntities()) {
				if (!entity.isPrototype()) {
					entityNames.add(entity.getName());
				}
			}
		}
		return entityNames;
	}

	public Set<String> getEntityNames() {
		Set<String> entityNames = new TreeSet<String>();
		for (EOModel model : _models) {
			for (EOEntity entity : model.getEntities()) {
				entityNames.add(entity.getName());
			}
		}
		return entityNames;
	}

	/**
	 * Returns the list of entities that are not prototypes.
	 * 
	 * @return list of non prototype entities
	 */
	public Set<EOEntity> getNonPrototypeEntities() {
		Set<EOEntity> entities = new HashSet<EOEntity>();
		for (EOModel model : _models) {
			entities.addAll(model.getNonPrototypeEntities());
		}
		return entities;
	}

	public Set<EOEntity> getEntities() {
		Set<EOEntity> allEntities = new HashSet<EOEntity>();
		for (EOModel model : _models) {
			allEntities.addAll(model.getEntities());
		}
		return allEntities;
	}

	public Set<EOEntity> getSortedEntities() {
		return new PropertyListSet<EOEntity>(getEntities());
	}

	public String findUnusedEntityName(String _newName) {
		return _findUnusedName(_newName, "getEntityNamed");
	}

	public EOEntity getEntityNamed(String _entityName) {
		EOEntity matchingEntity = null;
		Iterator modelsIter = _models.iterator();
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
		Set<EOModel> oldModels = new HashSet<EOModel>(_models);
		_model._setModelGroup(this);
		_checkForDuplicateModelName(_model, _model.getName());
		for (EOEntity entity : _model.getEntities()) {
			_model._checkForDuplicateEntityName(entity, entity.getName(), _failures);
		}
		_models.add(_model);
		clearCachedPrototypes(_failures);
		firePropertyChange(EOModelGroup.MODELS, oldModels, _models);
	}

	public void removeModel(EOModel _model, Set<EOModelVerificationFailure> _failures) {
		Set<EOModel> oldModels = new HashSet<EOModel>(_models);
		_models.remove(_model);
		clearCachedPrototypes(_failures);
		firePropertyChange(EOModelGroup.MODELS, oldModels, _models);
		_model._setModelGroup(null);
	}

	public Set<EOEntity> getPrototypeEntities() {
		Set<EOEntity> prototypeEntities = new HashSet<EOEntity>();
		for (EOModel model : _models) {
			for (EOEntity entity : model.getEntities()) {
				if (entity.isPrototype()) {
					prototypeEntities.add(entity);
				}
			}
		}
		return prototypeEntities;
	}

	protected void clearCachedPrototypes(Set<EOModelVerificationFailure> _failures) {
		for (EOModel model : _models) {
			model.clearCachedPrototypes(_failures, false);
		}
	}

	public Map<EOAttribute, Set<EORelationship>> _createReferencingRelationshipsCache() {
		Map<EOAttribute, Set<EORelationship>> cache = new HashMap<EOAttribute, Set<EORelationship>>();
		for (EOModel model : getModels()) {
			model._createReferencingRelationshipsCache(cache);
		}
		return cache;
	}

	public Map<EOEntity, Set<EOEntity>> _createInheritanceCache() {
		Map<EOEntity, Set<EOEntity>> cache = new HashMap<EOEntity, Set<EOEntity>>();
		for (EOModel model : getModels()) {
			model._createInheritanceCache(cache);
		}
		return cache;
	}

	public EOModel getModelNamed(String _name) {
		EOModel matchingModel = null;
		Iterator<EOModel> modelsIter = _models.iterator();
		while (matchingModel == null && modelsIter.hasNext()) {
			EOModel model = modelsIter.next();
			if (model.getName().equals(_name)) {
				matchingModel = model;
			}
		}
		return matchingModel;
	}

	/**
	 * Sets the URL of the model that is being editing (considered the primary
	 * model).
	 * 
	 * @param editingModelURL
	 *            the url of the model that is in edit mode
	 */
	public void setEditingModelURL(URL editingModelURL) {
		setEditingModelName(EOModelGroup.getModelNameForURL(editingModelURL));
	}

	/**
	 * Sets the name of the model that is being editing (considered the primary
	 * model).
	 * 
	 * @param editingModelName
	 *            the name of the model that is in edit mode
	 */
	public void setEditingModelName(String editingModelName) {
		_editingModelName = editingModelName;
	}

	/**
	 * Returns the name of the model that is being editing (considered the
	 * primary model).
	 * 
	 * @return the name of the model that is in edit mode
	 */
	public String getEditingModelName() {
		return _editingModelName;
	}

	/**
	 * Returns the model that is being editing (considered the primary model).
	 * 
	 * @return the model that is in edit mode
	 */
	public EOModel getEditingModel() {
		return getModelNamed(_editingModelName);
	}

	/**
	 * Returns the model name for the given URL.
	 * 
	 * @param url
	 *            the URL to lookup (could be index.eomodeld file, or .eomodeld
	 *            folder)
	 * @return the model name for the given URL
	 */
	public static String getModelNameForURL(URL url) {
		String modelName;
		String protocol = url.getProtocol();
		if ("jar".equals(protocol)) {
			try {
				JarURLConnection conn = (JarURLConnection) url.openConnection();
				JarEntry jarEntry = conn.getJarEntry();
				if (jarEntry != null) {
					modelName = EOModelGroup.getModelNameForJarEntry(jarEntry);
				} else {
					modelName = null;
				}
			} catch (IOException ioe) {
				throw new IllegalStateException("The jar url '" + url.toString() + "' was unable to be used to find out the EO model's name.", ioe);
			}
		} else {
			File file = URLUtils.cheatAndTurnIntoFile(url);
			modelName = EOModelGroup.getModelNameForFile(file);
		}
		return modelName;
	}

	/**
	 * Returns the model name for the given jar entry. This would be used in the
	 * situation where a Jar file is being introspected for the models within
	 * it.
	 * 
	 * @param jarEntry
	 *            the jar entry to lookup (could be index.eomodeld file, or
	 *            .eomodeld folder)
	 * @return the model name for the given jar entry
	 */
	public static String getModelNameForJarEntry(JarEntry jarEntry) {
		String modelName = null;

		if (jarEntry != null) {
			String jarEntryName = jarEntry.getName();

			if (jarEntryName.endsWith("index.eomodeld")) {
				int lastSlashIndex = jarEntryName.lastIndexOf('/');

				if (lastSlashIndex == -1) {
					jarEntryName = null;
				} else {
					jarEntryName = jarEntryName.substring(0, lastSlashIndex + 1);
				}
			}

			if (jarEntryName != null) {
				if (jarEntryName.endsWith(".eomodeld/")) {
					int lastDotIndex = jarEntryName.lastIndexOf('.', jarEntryName.length() - 2);
					int lastSlashIndex = jarEntryName.lastIndexOf('/', jarEntryName.length() - 2);

					modelName = jarEntryName.substring(lastSlashIndex + 1, lastDotIndex);

					if (modelName.length() == 0) {
						modelName = null;
					}
				}
			}
		}

		return modelName;
	}

	/**
	 * Returns the model name for the given file.
	 * 
	 * @param url
	 *            the file to lookup (could be index.eomodeld file, or .eomodeld
	 *            folder)
	 * @return the model name for the given file
	 */
	public static String getModelNameForFile(File file) {
		String modelName = null;
		if (file != null) {
			String fileName = file.getName();
			if (file.getName().equals("index.eomodeld")) {
				fileName = file.getParentFile().getName();
			}
			if (fileName.endsWith(".eomodeld")) {
				modelName = fileName.substring(0, fileName.lastIndexOf('.'));
			}
		}
		return modelName;
	}

	public EOModel loadModelFromURL(URL url) throws IOException, EOModelException {
		Set<EOModelVerificationFailure> failures = new HashSet<EOModelVerificationFailure>();
		EOModel model = loadModelFromURL(url, failures, true, new NullProgressMonitor());
		if (failures.size() > 0) {
			throw new EOModelException("Failed to load model from URL '" + url + "': " + failures);
		}
		return model;
	}

	public EOModel loadModelFromURL(URL url, Set<EOModelVerificationFailure> failures) throws IOException, EOModelException {
		EOModel model = loadModelFromURL(url, failures, true, new NullProgressMonitor());
		return model;
	}

	public EOModel loadModelFromURL(URL url, Set<EOModelVerificationFailure> failures, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		EOModel model = loadModelFromURL(url, failures, true, progressMonitor);
		return model;
	}

	public void loadModelsFromURL(URL url, Set<EOModelVerificationFailure> failures, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		loadModelsFromURL(url, -1, failures, true, progressMonitor);
	}

	protected void loadModelsFromURL(URL url, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		loadModelsFromURL(url, -1, failures, skipOnDuplicates, progressMonitor);
	}

	public void loadModelsFromURL(URL url, int maxDepth, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		String path = url.getPath();
		if (path.endsWith(".eomodeld") || path.endsWith(".eomodeld/")) {
			loadModelFromURL(url, failures, skipOnDuplicates, progressMonitor);
		} else if (maxDepth != 0) {
			for (URL childURL : URLUtils.getChildrenFolders(url)) {
				if (URLUtils.isFolder(childURL)) {
					loadModelsFromURL(childURL, maxDepth - 1, failures, skipOnDuplicates, progressMonitor);
				}
			}
		}
	}

	public EOModel loadModelFromURL(URL modelURL, Set<EOModelVerificationFailure> failures, boolean skipOnDuplicates, IProgressMonitor progressMonitor) throws IOException, EOModelException {
		String modelName = EOModelGroup.getModelNameForURL(modelURL);
		progressMonitor.setTaskName("Loading " + modelName + " ...");
		EOModel model = getModelNamed(modelName);
		if (model != null) {
			if (skipOnDuplicates) {
				// _failures.add(new EOModelVerificationFailure(model, "The
				// model named '" + modelName + "' exists in " +
				// model.getIndexURL() + " and " + _folder + ". Skipping " +
				// _folder + ".", true));
			} else {
				failures.add(new EOModelVerificationFailure(model, model, "The model named '" + modelName + "' exists in " + model.getIndexURL() + " and " + modelURL + ".", true));
			}
		}
		if (!skipOnDuplicates || model == null) {
			boolean reloadModel = true;
			while (reloadModel) {
				model = new EOModel(modelName);
				model.setModelURL(modelURL);
				URL indexURL = model.getIndexURL();
				if (!URLUtils.exists(indexURL)) {
					reloadModel = false;
					failures.add(new EOModelVerificationFailure(model, model, "Skipping model because " + indexURL + " does not exist.", true));
				} else {
					model._setModelGroup(this);
					try {
						model.loadFromURL(modelURL, _createDefaultDatabaseConfig, failures);
						addModel(model, failures);
						reloadModel = false;
					} catch (DuplicateEntityNameException e) {
						e.printStackTrace();
						if (!skipOnDuplicates) {
							throw e;
						}
						EOEntity existingEntity = e.getExistingEntity();
						EOModel existingEntityModel = existingEntity.getModel();
						failures.add(new EOModelVerificationFailure(model, model, existingEntityModel.getName() + " and " + model.getName() + " both declare an entity named " + existingEntity.getName() + ", so " + existingEntityModel.getName() + " is being removed. You can create an EOModelGroup file to resolve this.", true, e));
						removeModel(existingEntityModel, failures);
					} catch (Exception e) {
						failures.add(new EOModelVerificationFailure(model, model, model.getName() + " failed to load.", true, e));
						reloadModel = false;
					}
				}
			}
		}
		return model;
	}

	public void verify(Set<EOModelVerificationFailure> _failures) {
		VerificationContext verificationContext = new VerificationContext(this);
		for (EOModel model : _models) {
			model.verify(_failures, verificationContext);
		}
	}

	public void resolve(Set<EOModelVerificationFailure> _failures) {
		for (EOModel model : _models) {
			model.resolve(_failures);
		}
		for (EOModel model : _models) {
			model.resolveFlattened(_failures);
		}
	}

	public String getFullyQualifiedName() {
		return "EOModelGroup";
	}

	public String toString() {
		return "[EOModelGroup: models = " + _models + "]";
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

	public static void main(String[] args) throws IOException, PropertyListParserException {
		long a = System.currentTimeMillis();
		for (int i = 0; i < 1000; i++) {
			PropertyListSerialization.propertyListFromFile(new File("/Users/mschrag/Documents/workspace/MDTask/Resources/MDTask.eomodeld/index.eomodeld"));
		}
		System.out.println("EOModelGroup.main: " + (System.currentTimeMillis() - a));
	}
}
