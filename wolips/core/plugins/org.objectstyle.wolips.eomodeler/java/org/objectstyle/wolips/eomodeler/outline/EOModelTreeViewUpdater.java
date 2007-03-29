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

import org.eclipse.jface.viewers.TreeViewer;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOModelContainer;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;

public class EOModelTreeViewUpdater {
	private TreeViewer myTreeViewer;

	private ModelPropertyChangeListener myModelListener;

	private EntityPropertyChangeListener myEntityListener;

	private StoredProcedurePropertyChangeListener myStoredProcedureListener;

	private EOModel myModel;

	private List<EOEntity> myEntities;

	private List<EOStoredProcedure> myStoredProcedures;

	public EOModelTreeViewUpdater(TreeViewer _treeViewer, EOModelOutlineContentProvider _contentProvider) {
		myTreeViewer = _treeViewer;
		myTreeViewer.setContentProvider(_contentProvider);
		myTreeViewer.setLabelProvider(new EOModelOutlineLabelProvider(myTreeViewer));
		myTreeViewer.setSorter(new EOModelOutlineViewerSorter());
		myModelListener = new ModelPropertyChangeListener();
		myEntityListener = new EntityPropertyChangeListener();
		myStoredProcedureListener = new StoredProcedurePropertyChangeListener();
	}

	public void setModel(EOModel _model) {
		removePropertyChangeListeners();
		myModel = _model;
		addPropertyChangeListeners();
		if (myTreeViewer != null) {
			setInput(myTreeViewer);
		}
	}

	protected void setInput(TreeViewer _treeViewer) {
		_treeViewer.setInput(new EOModelContainer(myModel));
		_treeViewer.expandToLevel(2);
	}

	protected void refreshPropertyChangeListeners() {
		removePropertyChangeListeners();
		addPropertyChangeListeners();
	}

	public EOModel getModel() {
		return myModel;
	}

	protected TreeViewer getTreeViewer() {
		return myTreeViewer;
	}

	protected void removePropertyChangeListeners() {
		if (myModel != null) {
			myModel.removePropertyChangeListener(myModelListener);
			if (myEntities != null) {
				Iterator oldEntitiesIter = myEntities.iterator();
				while (oldEntitiesIter.hasNext()) {
					EOEntity entity = (EOEntity) oldEntitiesIter.next();
					entity.removePropertyChangeListener(myEntityListener);
				}
			}
			if (myStoredProcedures != null) {
				Iterator oldStoredProceduresIter = myStoredProcedures.iterator();
				while (oldStoredProceduresIter.hasNext()) {
					EOStoredProcedure storedProcedure = (EOStoredProcedure) oldStoredProceduresIter.next();
					storedProcedure.removePropertyChangeListener(myStoredProcedureListener);
				}
			}
		}
	}

	protected void addPropertyChangeListeners() {
		if (myModel != null) {
			myEntities = new LinkedList<EOEntity>(myModel.getEntities());
			myStoredProcedures = new LinkedList<EOStoredProcedure>(myModel.getStoredProcedures());

			if (myStoredProcedures != null) {
				Iterator oldStoredProceduresIter = myStoredProcedures.iterator();
				while (oldStoredProceduresIter.hasNext()) {
					EOStoredProcedure storedProcedure = (EOStoredProcedure) oldStoredProceduresIter.next();
					storedProcedure.addPropertyChangeListener(myStoredProcedureListener);
				}
			}

			Iterator newEntitiesIter = myEntities.iterator();
			while (newEntitiesIter.hasNext()) {
				EOEntity entity = (EOEntity) newEntitiesIter.next();
				entity.addPropertyChangeListener(myEntityListener);
			}
			myModel.addPropertyChangeListener(myModelListener);
		}
	}

	protected void refreshRelationshipsForEntity(EOEntity _entity) {
		TreeViewer treeViewer = getTreeViewer();
		if (treeViewer != null) {
			treeViewer.refresh(_entity, true);
			Object[] expandedElements = treeViewer.getExpandedElements();
			for (int expandedElementNum = 0; expandedElementNum < expandedElements.length; expandedElementNum++) {
				if (expandedElements[expandedElementNum] instanceof EORelationshipPath) {
					EORelationshipPath relationshipPath = (EORelationshipPath) expandedElements[expandedElementNum];
					if (relationshipPath.getChildRelationship().getEntity().equals(_entity)) {
						treeViewer.refresh(relationshipPath, true);
					}
				}
			}
		}
	}

	protected class ModelPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			TreeViewer treeViewer = getTreeViewer();
			if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
				String changedPropertyName = _event.getPropertyName();
				if (EOModel.ENTITIES.equals(changedPropertyName) || EOModel.STORED_PROCEDURES.equals(changedPropertyName) || EOModel.DATABASE_CONFIGS.equals(changedPropertyName) || EOModel.DATABASE_CONFIG.equals(changedPropertyName)) {
					// getTreeViewer().refresh(true);
					treeViewer.refresh(true);
					refreshPropertyChangeListeners();
				}
        else if (EOModel.ACTIVE_DATABASE_CONFIG.equals(changedPropertyName)) {
          treeViewer.refresh(true);
        }
			}
		}
	}

	protected class StoredProcedurePropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			TreeViewer treeViewer = EOModelTreeViewUpdater.this.getTreeViewer();
			if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
				String changedPropertyName = _event.getPropertyName();
				if (EOStoredProcedure.NAME.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOStoredProcedure.ARGUMENTS.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOStoredProcedure.ARGUMENT.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				}
			}
		}
	}

	protected class EntityPropertyChangeListener implements PropertyChangeListener {
		public void propertyChange(PropertyChangeEvent _event) {
			TreeViewer treeViewer = EOModelTreeViewUpdater.this.getTreeViewer();
			if (treeViewer != null && !treeViewer.getTree().isDisposed()) {
				String changedPropertyName = _event.getPropertyName();
				if (EOEntity.NAME.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOEntity.FETCH_SPECIFICATIONS.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOEntity.FETCH_SPECIFICATION.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOEntity.ATTRIBUTES.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOEntity.ATTRIBUTE.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOEntity.RELATIONSHIPS.equals(changedPropertyName)) {
					EOEntity entity = (EOEntity) _event.getSource();
					EOModelTreeViewUpdater.this.refreshRelationshipsForEntity(entity);
				} else if (EOEntity.RELATIONSHIP.equals(changedPropertyName)) {
					EOEntity entity = (EOEntity) _event.getSource();
					EOModelTreeViewUpdater.this.refreshRelationshipsForEntity(entity);
				} else if (EOEntity.ENTITY_INDEX.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				} else if (EOEntity.ENTITY_INDEXES.equals(changedPropertyName)) {
					treeViewer.refresh(true);
				}
			}
		}
	}
}
