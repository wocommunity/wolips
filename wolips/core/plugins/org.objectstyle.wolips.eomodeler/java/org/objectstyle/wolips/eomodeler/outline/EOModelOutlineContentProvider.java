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

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.core.model.AbstractEOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EOArgument;
import org.objectstyle.wolips.eomodeler.core.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.core.model.EOAttributePath;
import org.objectstyle.wolips.eomodeler.core.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.core.model.EOEntity;
import org.objectstyle.wolips.eomodeler.core.model.EOEntityIndex;
import org.objectstyle.wolips.eomodeler.core.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.core.model.EOModel;
import org.objectstyle.wolips.eomodeler.core.model.EOModelContainer;
import org.objectstyle.wolips.eomodeler.core.model.EOModelGroup;
import org.objectstyle.wolips.eomodeler.core.model.EOModelObject;
import org.objectstyle.wolips.eomodeler.core.model.EORelationship;
import org.objectstyle.wolips.eomodeler.core.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.core.model.EOStoredProcedure;

public class EOModelOutlineContentProvider implements ITreeContentProvider {
	private Object _modelContainer;

	private boolean _showEntities;

	private boolean _showAttributes;

	private boolean _showRelationships;

	private boolean _showFetchSpecs;

	private boolean _showStoredProcedures;

	private boolean _showEntityIndexes;

	private boolean _showDatabaseConfigs;

	private boolean _showNonClassProperties;

	public EOModelOutlineContentProvider(boolean showEntities, boolean showAttributes, boolean showRelationships, boolean showFetchSpecs, boolean showStoredProcedures, boolean showDatabaseConfigs, boolean showEntityIndexes, boolean showNonClassProperties) {
		_showEntities = showEntities;
		_showAttributes = showAttributes;
		_showRelationships = showRelationships;
		_showFetchSpecs = showFetchSpecs;
		_showStoredProcedures = showStoredProcedures;
		_showDatabaseConfigs = showDatabaseConfigs;
		_showEntityIndexes = showEntityIndexes;
		_showNonClassProperties = showNonClassProperties;
	}

	public void setShowNonClassProperties(boolean showNonClassProperties) {
		_showNonClassProperties = showNonClassProperties;
	}

	public boolean isShowNonClassProperties() {
		return _showNonClassProperties;
	}

	public Object[] getChildren(Object _parentElement) {
		Object[] children;
		if (_parentElement instanceof EOModelLoading) {
		  EOModelLoading loading = (EOModelLoading)_parentElement;
		  if (loading.getModel() == null) {
			  children = new Object[] { "Please Wait ..." };
		  }
		  else {
        children = new Object[] { "Loading " + loading.getModel().getName() + " ..." };
		  }
		} else if (_parentElement instanceof EOModelContainer) {
			EOModelContainer modelContainer = (EOModelContainer) _parentElement;
			EOModel model = modelContainer.getModel();
			if (model == null) {
				children = new Object[] { "No Model Loaded" };
			} else {
				children = new Object[] { model };
			}
		} else if (_parentElement instanceof EOModelGroup) {
			EOModelGroup modelGroup = (EOModelGroup) _parentElement;
			Set<EOModel> modelGroupChildren = new TreeSet<EOModel>(new EOSortableEOModelObjectComparator());
			modelGroupChildren.addAll(modelGroup.getModels());
			children = modelGroupChildren.toArray();
		} else if (_parentElement instanceof EOModel) {
			EOModel model = (EOModel) _parentElement;
			Set<EOModelObject> modelChildren = new TreeSet<EOModelObject>(new EOSortableEOModelObjectComparator());
			if (_showEntities) {
				modelChildren.addAll(model.getEntities());
			}
			if (_showStoredProcedures) {
				modelChildren.addAll(model.getStoredProcedures());
			}
			if (_showDatabaseConfigs) {
				modelChildren.addAll(model.getDatabaseConfigs());
			}
			children = modelChildren.toArray();
		} else if (_parentElement instanceof EOEntity) {
			EOEntity entity = (EOEntity) _parentElement;
			Set<EOModelObject> entityChildren = new TreeSet<EOModelObject>(new EOSortableEOModelObjectComparator());
			if (_showAttributes) {
				if (_showNonClassProperties) {
					entityChildren.addAll(entity.getAttributes());
				} else {
					entityChildren.addAll(entity.getClassAttributes());
				}
			}
			if (_showRelationships) {
				if (_showNonClassProperties) {
					entityChildren.addAll(entity.getRelationships());
				} else {
					entityChildren.addAll(entity.getClassRelationships());
				}
			}
			if (_showFetchSpecs) {
				entityChildren.addAll(entity.getFetchSpecs());
			}
			if (_showEntityIndexes) {
				entityChildren.addAll(entity.getEntityIndexes());
			}
			children = entityChildren.toArray();
		} else if (_parentElement instanceof EORelationship) {
			EORelationship relationship = (EORelationship) _parentElement;
			Set<AbstractEOAttributePath> relationshipPathChildren = new TreeSet<AbstractEOAttributePath>(new EOSortableEOModelObjectComparator());
			relationshipPathChildren.addAll(Arrays.asList(new EORelationshipPath(null, relationship).getChildren()));
			children = relationshipPathChildren.toArray();
		} else if (_parentElement instanceof EORelationshipPath) {
			EORelationshipPath relationshipPath = (EORelationshipPath) _parentElement;
			Set<AbstractEOAttributePath> relationshipPathChildren = new TreeSet<AbstractEOAttributePath>(new EOSortableEOModelObjectComparator());
			relationshipPathChildren.addAll(Arrays.asList(relationshipPath.getChildren()));
			children = relationshipPathChildren.toArray();
		} else if (_parentElement instanceof EOStoredProcedure) {
			EOStoredProcedure storedProcedure = (EOStoredProcedure) _parentElement;
			Set<EOArgument> arguments = new TreeSet<EOArgument>(new EOSortableEOModelObjectComparator());
			arguments.addAll(storedProcedure.getArguments());
			children = arguments.toArray();
		} else {
			children = new Object[0];
		}
		return children;
	}

	public void dispose() {
		// DO NOTHING
	}

	public Object[] getElements(Object _inputElement) {
		return getChildren(_inputElement);
	}

	public Object getParent(Object _element) {
		Object parent;
		if (_element instanceof EOModelContainer) {
			parent = null;
		}
		// else if (_element instanceof EOModelGroup) {
		// parent = myModelContainer;
		// }
		else if (_element instanceof EOModel) {
			parent = _modelContainer;
		} else if (_element instanceof EOEntity) {
			parent = ((EOEntity) _element).getModel();
		} else if (_element instanceof EOAttribute) {
			parent = ((EOAttribute) _element).getEntity();
		} else if (_element instanceof EOFetchSpecification) {
			parent = ((EOFetchSpecification) _element).getEntity();
		} else if (_element instanceof EORelationship) {
			parent = ((EORelationship) _element).getEntity();
		} else if (_element instanceof EOStoredProcedure) {
			parent = ((EOStoredProcedure) _element).getModel();
		} else if (_element instanceof EODatabaseConfig) {
			parent = ((EODatabaseConfig) _element).getModel();
		} else if (_element instanceof EOArgument) {
			parent = ((EOArgument) _element).getStoredProcedure();
		} else if (_element instanceof AbstractEOAttributePath) {
			EORelationshipPath parentRelationshipPath = ((AbstractEOAttributePath) _element).getParentRelationshipPath();
			if (parentRelationshipPath == null) {
				parent = ((AbstractEOAttributePath) _element).getChildIEOAttribute().getEntity();
			} else {
				parent = parentRelationshipPath;
			}
		} else if (_element instanceof EOEntityIndex) {
			parent = ((EOEntityIndex) _element).getEntity();
		} else {
			parent = null;
		}
		return parent;
	}

	public boolean hasChildren(Object _element) {
		boolean hasChildren = true;
		if (_element instanceof EOModelLoading) {
			hasChildren = false;
		} else if (_element instanceof EOFetchSpecification) {
			hasChildren = false;
		} else if (_element instanceof EOAttribute) {
			hasChildren = false;
		} else if (_element instanceof EOAttributePath) {
			hasChildren = false;
		} else if (_element instanceof EOArgument) {
			hasChildren = false;
		} else if (_element instanceof EODatabaseConfig) {
			hasChildren = false;
		} else if (_element instanceof EOEntityIndex) {
			hasChildren = false;
		}
		return hasChildren;
	}

	public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
		_modelContainer = _newInput;
	}
}
