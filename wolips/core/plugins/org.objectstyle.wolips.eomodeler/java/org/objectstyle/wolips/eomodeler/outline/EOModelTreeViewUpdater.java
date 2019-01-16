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
package org.objectstyle.wolips.eomodeler.outline;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelContainer;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;

public class EOModelTreeViewUpdater {
	private TreeViewer _treeViewer;

	private EOModelOutlineContentProvider _contentProvider;

	private ModelPropertyChangeListener _modelListener;

	private EntityPropertyChangeListener _entityListener;

	private StoredProcedurePropertyChangeListener _storedProcedureListener;

	private EOModel _model;

	private List<EOEntity> _entities;

	private List<EOStoredProcedure> _storedProcedures;
	
	private boolean _shouldRefresh;

	public EOModelTreeViewUpdater(TreeViewer treeViewer, EOModelOutlineContentProvider contentProvider) {
		_treeViewer = treeViewer;
		_contentProvider = contentProvider;
		_treeViewer.setContentProvider(contentProvider);
		_treeViewer.setLabelProvider(new EOModelOutlineLabelProvider(_treeViewer));
		_treeViewer.setSorter(new EOModelOutlineViewerSorter());
		_modelListener = new ModelPropertyChangeListener();
		_entityListener = new EntityPropertyChangeListener();
		_storedProcedureListener = new StoredProcedurePropertyChangeListener();
		_shouldRefresh = true;
	}

	public void setModel(EOModel model) {
		removePropertyChangeListeners();
		_model = model;
		addPropertyChangeListeners();
		if (_treeViewer != null) {
			setInput();
		}
	}

	public void showModelGroup() {
		if (_model != null) {
			_treeViewer.setInput(_model.getModelGroup());
			_treeViewer.expandToLevel(_model, 1);
			_treeViewer.setSelection(new StructuredSelection(_model));
		}
	}

	public void showModel() {
		if (_model != null) {
			EOModelContainer container = new EOModelContainer(_model);
			_treeViewer.setInput(container);
			_treeViewer.expandToLevel(_model, 1);
			_treeViewer.setSelection(new StructuredSelection(_model));
		}
	}

	public void showNonClassProperties() {
		if (_treeViewer != null && _contentProvider != null && !_contentProvider.isShowNonClassProperties()) {
			_contentProvider.setShowNonClassProperties(true);
			_treeViewer.refresh(true);
		}
	}

	public void hideNonClassProperties() {
		if (_treeViewer != null && _contentProvider != null && _contentProvider.isShowNonClassProperties()) {
			_contentProvider.setShowNonClassProperties(false);
			_treeViewer.refresh(true);
		}
	}

	protected void setInput() {
		showModel();
	}

	protected void refreshPropertyChangeListeners() {
		removePropertyChangeListeners();
		addPropertyChangeListeners();
	}

	public EOModel getModel() {
		return _model;
	}

	public void dispose() {
		removePropertyChangeListeners();
	}

	protected TreeViewer getTreeViewer() {
		return _treeViewer;
	}

	protected void removePropertyChangeListeners() {
		if (_model != null) {
			_model.removePropertyChangeListener(_modelListener);
			for (EOModel model : _model.getModelGroup().getModels()) {
				model.removePropertyChangeListener(_modelListener);
				if (_entities != null) {
					Iterator oldEntitiesIter = _entities.iterator();
					while (oldEntitiesIter.hasNext()) {
						EOEntity entity = (EOEntity) oldEntitiesIter.next();
						entity.removePropertyChangeListener(_entityListener);
					}
				}
				if (_storedProcedures != null) {
					Iterator oldStoredProceduresIter = _storedProcedures.iterator();
					while (oldStoredProceduresIter.hasNext()) {
						EOStoredProcedure storedProcedure = (EOStoredProcedure) oldStoredProceduresIter.next();
						storedProcedure.removePropertyChangeListener(_storedProcedureListener);
					}
				}
			}
		}
	}

	protected void addPropertyChangeListeners() {
		if (_model != null) {
			for (EOModel model : _model.getModelGroup().getModels()) {
				_entities = new LinkedList<EOEntity>(model.getEntities());
				_storedProcedures = new LinkedList<EOStoredProcedure>(model.getStoredProcedures());

				if (_storedProcedures != null) {
					Iterator oldStoredProceduresIter = _storedProcedures.iterator();
					while (oldStoredProceduresIter.hasNext()) {
						EOStoredProcedure storedProcedure = (EOStoredProcedure) oldStoredProceduresIter.next();
						storedProcedure.addPropertyChangeListener(_storedProcedureListener);
					}
				}

				Iterator newEntitiesIter = _entities.iterator();
				while (newEntitiesIter.hasNext()) {
					EOEntity entity = (EOEntity) newEntitiesIter.next();
					entity.addPropertyChangeListener(_entityListener);
				}
				model.addPropertyChangeListener(_modelListener);
			}
		}
	}

	protected void refreshRelationshipsForEntity(EOEntity entity) {
		TreeViewer treeViewer = getTreeViewer();
		if (treeViewer != null) {
			treeViewer.refresh(entity, true);
			Object[] expandedElements = treeViewer.getExpandedElements();
			for (int expandedElementNum = 0; expandedElementNum < expandedElements.length; expandedElementNum++) {
				if (expandedElements[expandedElementNum] instanceof EORelationshipPath) {
					EORelationshipPath relationshipPath = (EORelationshipPath) expandedElements[expandedElementNum];
					if (relationshipPath.getChildRelationship().getEntity().equals(entity)) {
						treeViewer.refresh(relationshipPath, true);
					}
				}
			}
		}
	}
	
	protected void refresh() {
		if (_shouldRefresh) {
			TreeViewer treeViewer = getTreeViewer();
			treeViewer.refresh(true);
		}
	}

	protected class ModelPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			TreeViewer treeViewer = getTreeViewer();
			if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
				String changedPropertyName = event.getPropertyName();
				if (EOModel.MODEL_SAVING.equals(changedPropertyName)) {
					Boolean saving = (Boolean)event.getNewValue();
					_shouldRefresh = !saving.booleanValue();
					refresh();
				} 
				else if (EOModel.ENTITIES.equals(changedPropertyName) || EOModel.STORED_PROCEDURES.equals(changedPropertyName) || EOModel.DATABASE_CONFIGS.equals(changedPropertyName) || EOModel.DATABASE_CONFIG.equals(changedPropertyName)) {
					// getTreeViewer().refresh(true);
					refresh();
					refreshPropertyChangeListeners();
				} else if (EOModel.ACTIVE_DATABASE_CONFIG.equals(changedPropertyName)) {
					refresh();
				} else if (EOModel.DIRTY.equals(changedPropertyName)) {
					refresh();
				} 
				// SAVAS hier wird der Outline refreshed.
				else if (EOModel.ERDIAGRAMCOLLECTION.equals(changedPropertyName) || EOModel.CLASSDIAGRAMCOLLECTION.equals(changedPropertyName)) { 
					refresh();
					refreshPropertyChangeListeners();
				}
			}
		}
	}

	protected class StoredProcedurePropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			TreeViewer treeViewer = EOModelTreeViewUpdater.this.getTreeViewer();
			if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
				String changedPropertyName = event.getPropertyName();
				if (EOStoredProcedure.NAME.equals(changedPropertyName)) {
					refresh();
				} else if (EOStoredProcedure.ARGUMENTS.equals(changedPropertyName)) {
					refresh();
				} else if (EOStoredProcedure.ARGUMENT.equals(changedPropertyName)) {
					refresh();
				}
			}
		}
	}

	protected class EntityPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent event) {
			TreeViewer treeViewer = EOModelTreeViewUpdater.this.getTreeViewer();
			if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
				String changedPropertyName = event.getPropertyName();
				if (EOEntity.NAME.equals(changedPropertyName)) {
					refresh();
				} else if (EOEntity.FETCH_SPECIFICATIONS.equals(changedPropertyName)) {
					refresh();
				} else if (EOEntity.FETCH_SPECIFICATION.equals(changedPropertyName)) {
					refresh();
				} else if (EOEntity.ATTRIBUTES.equals(changedPropertyName)) {
					refresh();
				} else if (EOEntity.ATTRIBUTE.equals(changedPropertyName)) {
					refresh();
				} else if (EOEntity.RELATIONSHIPS.equals(changedPropertyName)) {
					EOEntity entity = (EOEntity) event.getSource();
					EOModelTreeViewUpdater.this.refreshRelationshipsForEntity(entity);
				} else if (EOEntity.RELATIONSHIP.equals(changedPropertyName)) {
					EOEntity entity = (EOEntity) event.getSource();
					EOModelTreeViewUpdater.this.refreshRelationshipsForEntity(entity);
				} else if (EOEntity.ENTITY_INDEX.equals(changedPropertyName)) {
					refresh();
				} else if (EOEntity.ENTITY_INDEXES.equals(changedPropertyName)) {
					refresh();
				}
			}
		}
	}
}
