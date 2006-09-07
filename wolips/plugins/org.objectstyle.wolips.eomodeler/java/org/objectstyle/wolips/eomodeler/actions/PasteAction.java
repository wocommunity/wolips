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
package org.objectstyle.wolips.eomodeler.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.objectstyle.wolips.eomodeler.model.EOArgument;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EODatabaseConfig;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EOStoredProcedure;
import org.objectstyle.wolips.eomodeler.utils.EOModelUtils;

public class PasteAction extends Action implements IWorkbenchWindowActionDelegate {
  private IWorkbenchWindow myWindow;
  private ISelection mySelection;
  private Clipboard myClipboard;

  public PasteAction(Clipboard _clipboard) {
    myClipboard = _clipboard;
  }

  public void dispose() {
    // DO NOTHING
  }

  public void init(IWorkbenchWindow _window) {
    myWindow = _window;
  }

  public void selectionChanged(IAction _action, ISelection _selection) {
    mySelection = _selection;
  }

  public void run() {
    try {
      Object selectedObject = null;
      if (mySelection instanceof IStructuredSelection) {
        selectedObject = ((IStructuredSelection) mySelection).getFirstElement();
      }
      ISelection pastedSelection = LocalSelectionTransfer.getTransfer().getSelection();
      Object[] clipboardObjects = ((IStructuredSelection) pastedSelection).toArray();
      for (int clipboardObjectNum = 0; clipboardObjectNum < clipboardObjects.length; clipboardObjectNum++) {
        Object clipboardObject = clipboardObjects[clipboardObjectNum];
        if (clipboardObject instanceof EOEntity) {
          EOModel model = EOModelUtils.getRelatedModel(selectedObject);
          if (model != null) {
            EOEntity clipboardEntity = (EOEntity) clipboardObject;
            EOEntity clonedClipboardEntity = clipboardEntity.cloneEntity();
            clonedClipboardEntity.setName(model.findUnusedEntityName(clonedClipboardEntity.getName()));
            model.addEntity(clonedClipboardEntity);
          }
        }
        else if (clipboardObject instanceof EOAttribute) {
          EOEntity entity = EOModelUtils.getRelatedEntity(selectedObject);
          if (entity != null) {
            EOAttribute clipboardAttribute = (EOAttribute) clipboardObject;
            EOAttribute clonedClipboardAttribute = clipboardAttribute.cloneAttribute();
            clonedClipboardAttribute.setName(entity.findUnusedAttributeName(clonedClipboardAttribute.getName()));
            entity.addAttribute(clonedClipboardAttribute);
          }
        }
        else if (clipboardObject instanceof EORelationship) {
          EOEntity entity = EOModelUtils.getRelatedEntity(selectedObject);
          if (entity != null) {
            EORelationship clipboardRelationship = (EORelationship) clipboardObject;
            EORelationship clonedClipboardRelationship = clipboardRelationship.cloneRelationship();
            clonedClipboardRelationship.setName(entity.findUnusedRelationshipName(clonedClipboardRelationship.getName()));
            entity.addRelationship(clonedClipboardRelationship);
          }
        }
        else if (clipboardObject instanceof EORelationship) {
          EOEntity entity = EOModelUtils.getRelatedEntity(selectedObject);
          if (entity != null) {
            EORelationship clipboardRelationship = (EORelationship) clipboardObject;
            EORelationship clonedClipboardRelationship = clipboardRelationship.cloneRelationship();
            clonedClipboardRelationship.setName(entity.findUnusedRelationshipName(clonedClipboardRelationship.getName()));
            entity.addRelationship(clonedClipboardRelationship);
          }
        }
        else if (clipboardObject instanceof EOFetchSpecification) {
          EOEntity entity = EOModelUtils.getRelatedEntity(selectedObject);
          if (entity != null) {
            EOFetchSpecification clipboardFetchSpecification = (EOFetchSpecification) clipboardObject;
            EOFetchSpecification clonedClipboardFetchSpecification = clipboardFetchSpecification.cloneFetchSpecification();
            clonedClipboardFetchSpecification.setName(entity.findUnusedFetchSpecificationName(clonedClipboardFetchSpecification.getName()));
            entity.addFetchSpecification(clonedClipboardFetchSpecification);
          }
        }
        else if (clipboardObject instanceof EOStoredProcedure) {
          EOModel model = EOModelUtils.getRelatedModel(selectedObject);
          if (model != null) {
            EOStoredProcedure clipboardStoredProcedure = (EOStoredProcedure) clipboardObject;
            EOStoredProcedure clonedClipboardStoredProcedure = clipboardStoredProcedure.cloneStoredProcedure();
            clonedClipboardStoredProcedure.setName(model.findUnusedStoredProcedureName(clonedClipboardStoredProcedure.getName()));
            model.addStoredProcedure(clonedClipboardStoredProcedure);
          }
        }
        else if (clipboardObject instanceof EOArgument) {
          EOStoredProcedure storedProcedure = EOModelUtils.getRelatedStoredProcedure(selectedObject);
          if (storedProcedure != null) {
            EOArgument clipboardArgument = (EOArgument) clipboardObject;
            EOArgument clonedClipboardArgument = clipboardArgument.cloneArgument();
            clonedClipboardArgument.setName(storedProcedure.findUnusedArgumentName(clonedClipboardArgument.getName()));
            storedProcedure.addArgument(clonedClipboardArgument);
          }
        }
        else if (clipboardObject instanceof EODatabaseConfig) {
          EOModel model = EOModelUtils.getRelatedModel(selectedObject);
          if (model != null) {
            EODatabaseConfig clipboardDatabaseConfig = (EODatabaseConfig) clipboardObject;
            EODatabaseConfig clonedClipboardDatabaseConfig = clipboardDatabaseConfig.cloneDatabaseConfig();
            clonedClipboardDatabaseConfig.setName(model.findUnusedDatabaseConfigName(clonedClipboardDatabaseConfig.getName()), false);
            model.addDatabaseConfig(clonedClipboardDatabaseConfig);
          }
        }
      }
    }
    catch (Throwable t) {
      t.printStackTrace();
    }
  }

  public void runWithEvent(Event _event) {
    run();
  }

  public void run(IAction _action) {
    run();
  }
}
