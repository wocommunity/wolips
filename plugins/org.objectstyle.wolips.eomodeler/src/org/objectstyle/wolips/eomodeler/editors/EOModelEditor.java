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
package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.objectstyle.wolips.eomodeler.editors.entities.EOEntitiesTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityEditor;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class EOModelEditor extends MultiPageEditorPart implements IResourceChangeListener {
  private EOEntitiesTableEditor myEntitiesTableEditor;
  //  private EOAttributesTableEditor myAttributesTableEditor;
  //  private EORelationshipsTableEditor myRelationshipsTableEditor;
  private EOEntityEditor myEntityEditor;
  private EOModel myModel;
  private EOEntity mySelectedEntity;

  public EOModelEditor() {
    super();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
  }

  protected void createPages() {
    try {
      myEntitiesTableEditor = new EOEntitiesTableEditor();
      addPage(0, myEntitiesTableEditor, getEditorInput());
      setPageText(0, "Entites");

      myEntityEditor = new EOEntityEditor();
      addPage(1, myEntityEditor, getEditorInput());
      setPageText(1, "Entity");

      //      myAttributesTableEditor = new EOAttributesTableEditor();
      //      addPage(1, myAttributesTableEditor, getEditorInput());
      //      setPageText(1, "Attributes");
      //
      //      myRelationshipsTableEditor = new EORelationshipsTableEditor();
      //      addPage(2, myRelationshipsTableEditor, getEditorInput());
      //      setPageText(2, "Relationships");

      myEntitiesTableEditor.addSelectionChangedListener(new EntitySelectionChangedListener());
    }
    catch (PartInitException e) {
      ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
    }
  }

  public void setSelectedEntity(EOEntity _selectedEntity) {
    mySelectedEntity = _selectedEntity;
    myEntityEditor.setEntity(_selectedEntity);
    //    myAttributesTableEditor.setEntity(_selectedEntity);
    //    myRelationshipsTableEditor.setEntity(_selectedEntity);
    if (_selectedEntity == null) {
      EOModelEditor.this.setPageText(1, "Entity");
    }
    else {
      EOModelEditor.this.setPageText(1, _selectedEntity.getName());
    }
    updatePartName();
  }

  public void dispose() {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    super.dispose();
  }

  public void doSave(IProgressMonitor _monitor) {
    System.out.println("MultiPageEditor.doSave: doSave");
    myEntitiesTableEditor.doSave(_monitor);
  }

  public void doSaveAs() {
    System.out.println("MultiPageEditor.doSaveAs: saveAs");
    //String editorText = editor.getDocumentProvider().getDocument(editor.getEditorInput()).get();
    IEditorPart editor = myEntitiesTableEditor;
    editor.doSaveAs();
    setPageText(0, editor.getTitle());
    setInput(editor.getEditorInput());
  }

  public void gotoMarker(IMarker _marker) {
    System.out.println("MultiPageEditor.gotoMarker: goto marker " + _marker);
    //    setActivePage(0);
    //    IDE.gotoMarker(myModelEntitiesTableEditor, _marker);
  }

  public void init(IEditorSite _site, IEditorInput _editorInput) throws PartInitException {
    EOModelEditorInput input;
    if (_editorInput instanceof EOModelEditorInput) {
      input = (EOModelEditorInput) _editorInput;
    }
    else if (_editorInput instanceof IFileEditorInput) {
      IFileEditorInput fileEditorInput = (IFileEditorInput) _editorInput;
      try {
        input = new EOModelEditorInput(fileEditorInput);
      }
      catch (Exception e) {
        throw new PartInitException("Failed to create EOModelEditorInput for " + _editorInput + ".", e);
      }
    }
    else {
      throw new PartInitException("Unknown editor input: " + _editorInput + ".");
    }
    updatePartName();
    super.init(_site, input);
  }

  protected void updatePartName() {
    IEditorInput input = getEditorInput();
    String partName;
    if (input != null) {
      EOModel model = ((EOModelEditorInput) input).getModel();
      partName = model.getName();
      //      if (mySelectedEntity != null) {
      //        partName += " (" + mySelectedEntity.getName() + ")";
      //      }
    }
    else {
      partName = "EOModeler";
    }
    //EOModelEditor.this.setPageText(2, entity.getName() + " Relationships");
    setPartName(partName);
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  protected void pageChange(int _newPageIndex) {
    super.pageChange(_newPageIndex);
  }

  public void resourceChanged(final IResourceChangeEvent _event) {
    if (_event.getType() == IResourceChangeEvent.PRE_CLOSE) {
      Display.getDefault().asyncExec(new Runnable() {
        public void run() {
          IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
          for (int pageNum = 0; pageNum < pages.length; pageNum++) {
            EOModelEditorInput input = (EOModelEditorInput) myEntitiesTableEditor.getEditorInput();
            if (input.getFile().getProject().equals(_event.getResource())) {
              IEditorPart editorPart = pages[pageNum].findEditor(input);
              pages[pageNum].closeEditor(editorPart, true);
            }
          }
        }
      });
    }
  }

  protected class EntitySelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
      EOEntity entity = null;
      if (selection != null && !selection.isEmpty()) {
        entity = (EOEntity) selection.getFirstElement();
      }
      setSelectedEntity(entity);
    }
  }

}
