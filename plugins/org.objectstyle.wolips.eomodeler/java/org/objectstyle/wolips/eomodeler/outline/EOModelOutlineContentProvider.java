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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditorInput;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;

public class EOModelOutlineContentProvider implements ITreeContentProvider {
  private EOModelEditorInput myEditorInput;

  public Object[] getChildren(Object _parentElement) {
    Object[] children;
    if (_parentElement instanceof EOModelEditorInput) {
      EOModelEditorInput editorInput = (EOModelEditorInput) _parentElement;
      children = new Object[] { editorInput.getModel() };
    }
    else if (_parentElement instanceof EOModel) {
      EOModel model = (EOModel) _parentElement;
      List entitiesList = model.getEntities();
      children = entitiesList.toArray(new EOEntity[entitiesList.size()]);
    }
    else if (_parentElement instanceof EOEntity) {
      EOEntity entity = (EOEntity) _parentElement;
      List entityChildren = new LinkedList();
      entityChildren.addAll(entity.getRelationships());
      entityChildren.addAll(entity.getFetchSpecs());
      children = entityChildren.toArray();
    }
    else if (_parentElement instanceof EORelationship) {
      EORelationship relationship = (EORelationship) _parentElement;
      children = getChildrenRelationshipPaths(new EORelationshipPath(null, relationship));
    }
    else if (_parentElement instanceof EORelationshipPath) {
      EORelationshipPath relationshipPath = (EORelationshipPath) _parentElement;
      children = getChildrenRelationshipPaths(relationshipPath);
    }
    else {
      children = null;
    }
    return children;
  }

  protected EORelationshipPath[] getChildrenRelationshipPaths(EORelationshipPath _parentRelationshipPath) {
    EORelationshipPath[] children;
    EORelationship parentRelationship = _parentRelationshipPath.getChildRelationship();
    if (parentRelationship != null) {
      List relationshipsList = parentRelationship.getDestination().getRelationships();
      children = new EORelationshipPath[relationshipsList.size()];
      Iterator relationshipsIter = relationshipsList.iterator();
      for (int childNum = 0; relationshipsIter.hasNext(); childNum++) {
        EORelationship childRelationship = (EORelationship) relationshipsIter.next();
        children[childNum] = new EORelationshipPath(_parentRelationshipPath, childRelationship);
      }
    }
    else {
      children = null;
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
    if (_element instanceof EOModelEditorInput) {
      parent = null;
    }
    else if (_element instanceof EOModel) {
      parent = myEditorInput;
    }
    else if (_element instanceof EOEntity) {
      parent = ((EOEntity) _element).getModel();
    }
    else if (_element instanceof EOAttribute) {
      parent = ((EOAttribute) _element).getEntity();
    }
    else if (_element instanceof EOFetchSpecification) {
      parent = ((EOFetchSpecification) _element).getEntity();
    }
    else if (_element instanceof EORelationship) {
      parent = ((EORelationship) _element).getEntity();
    }
    else if (_element instanceof EORelationshipPath) {
      EORelationshipPath parentRelationshipPath = ((EORelationshipPath) _element).getParentRelationshipPath();
      if (parentRelationshipPath == null) {
        parent = ((EORelationshipPath) _element).getChildRelationship().getEntity();
      }
      else {
        parent = parentRelationshipPath;
      }
    }
    else {
      parent = null;
    }
    return parent;
  }

  public boolean hasChildren(Object _element) {
    boolean hasChildren = true;
    if (_element instanceof EOFetchSpecification) {
      hasChildren = false;
    }
    return hasChildren;
  }

  public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
    myEditorInput = (EOModelEditorInput) _newInput;
  }
}
