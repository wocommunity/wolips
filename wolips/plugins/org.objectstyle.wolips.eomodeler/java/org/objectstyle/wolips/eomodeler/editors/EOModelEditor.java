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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.editors.entities.EOEntitiesTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityEditor;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EOModelVerificationFailure;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.outline.EOModelContentOutlinePage;

public class EOModelEditor extends MultiPageEditorPart implements IResourceChangeListener, ITabbedPropertySheetPageContributor, ISelectionProvider {
  public static final String EOMODEL_EDITOR_ID = "org.objectstyle.wolips.eomodeler.editors.EOModelEditor"; //$NON-NLS-1$

  private EOEntitiesTableEditor myEntitiesTableEditor;
  private EOEntityEditor myEntityEditor;
  private EOModelContentOutlinePage myContentOutlinePage;
  private ListenerList mySelectionChangedListeners;
  private IStructuredSelection mySelection;
  private PropertyChangeListener myDirtyModelListener;
  private EOEntity mySelectedEntity;

  private int mySelectionDepth;

  public EOModelEditor() {
    mySelectionChangedListeners = new ListenerList();
    myDirtyModelListener = new DirtyModelListener();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
  }

  public EOModelContentOutlinePage getContentOutlinePage() {
    if (myContentOutlinePage == null) {
      EOModelEditorInput input = (EOModelEditorInput) getEditorInput();
      myContentOutlinePage = new EOModelContentOutlinePage(input);
      myContentOutlinePage.addSelectionChangedListener(new EOModelContentSelectionChangedListener());
    }
    return myContentOutlinePage;
  }

  public String getContributorId() {
    return getSite().getId();
  }

  public Object getAdapter(Class _adapterClass) {
    Object adapter;
    if (_adapterClass == IPropertySheetPage.class) {
      adapter = new TabbedPropertySheetPage(this);
    }
    else if (_adapterClass == IContentOutlinePage.class) {
      adapter = getContentOutlinePage();
    }
    else {
      adapter = super.getAdapter(_adapterClass);
    }
    return adapter;
  }

  protected void createPages() {
    try {
      myEntitiesTableEditor = new EOEntitiesTableEditor();
      addPage(0, myEntitiesTableEditor, getEditorInput());
      setPageText(0, "Entites");

      myEntityEditor = new EOEntityEditor();
      addPage(1, myEntityEditor, getEditorInput());
      setPageText(1, "No Entity Selected");

      EOModelSelectionChangedListener modelSelectionChangedListener = new EOModelSelectionChangedListener();
      myEntitiesTableEditor.addSelectionChangedListener(modelSelectionChangedListener);

      EOEntitySelectionChangedListener entitySelectionChangedListener = new EOEntitySelectionChangedListener();
      myEntityEditor.addSelectionChangedListener(entitySelectionChangedListener);

      EOEntity fileEntity = ((EOModelEditorInput) getEditorInput()).getFileEntity();
      if (fileEntity != null) {
        setSelectedEntity(fileEntity);
        setActivePage(1);
      }
    }
    catch (PartInitException e) {
      ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
    }
  }

  public void setSelectedEntity(EOEntity _selectedEntity) {
    if ((mySelectedEntity == null && _selectedEntity != null) || (mySelectedEntity != null && !mySelectedEntity.equals(_selectedEntity))) {
      System.out.println("EOModelEditor.setSelectedEntity: " + _selectedEntity);
      mySelectedEntity = _selectedEntity;
      myEntitiesTableEditor.setSelectedEntity(_selectedEntity);
      myEntityEditor.setEntity(_selectedEntity);
      if (_selectedEntity == null) {
        EOModelEditor.this.setPageText(1, "No Entity Selected");
      }
      else {
        EOModelEditor.this.setPageText(1, _selectedEntity.getName());
      }
      updatePartName();
    }
  }

  public void dispose() {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    super.dispose();
  }

  public void doSave(IProgressMonitor _monitor) {
    MessageDialog.openInformation(Display.getCurrent().getActiveShell(), "Not Yet", "Yeah, it can, but I'm not going to let you. It's safer for all of us at the moment.");
    /*
     try {
     EOModelEditorInput input = (EOModelEditorInput) getEditorInput();
     if (input != null) {
     EOModel model = input.getModel();

     List failures = new LinkedList();
     model.verify(failures);
     handleModelErrors(failures);

     IFile originalFile = input.getFile();
     IContainer originalFolder = originalFile.getParent();
     model.saveToFolder(originalFolder.getLocation().toFile());
     originalFolder.refreshLocal(IResource.DEPTH_INFINITE, _monitor);

     setInput(new FileEditorInput(originalFile));
     }
     }
     catch (IOException e) {
     e.printStackTrace();
     }
     catch (CoreException e) {
     e.printStackTrace();
     }
     */
  }

  public void doSaveAs() {
    doSave(null);
  }

  public void init(IEditorSite _site, IEditorInput _editorInput) throws PartInitException {
    try {
      IWorkbench workbench = Activator.getDefault().getWorkbench();
      IWorkbenchPage workbenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
      if (workbenchPage != null && !EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID.equals(workbenchPage.getPerspective().getId())) {
        boolean switchPerspectives = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), "Switch Perspectives?", "Would you like to switch to the EOModeler Perspective?");
        if (switchPerspectives) {
          workbench.showPerspective(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
        }
      }
    }
    catch (WorkbenchException e) {
      e.printStackTrace();
    }

    try {
      EOModelEditorInput input;
      if (_editorInput instanceof EOModelEditorInput) {
        input = (EOModelEditorInput) _editorInput;
      }
      else if (_editorInput instanceof IFileEditorInput) {
        IFileEditorInput fileEditorInput = (IFileEditorInput) _editorInput;
        List failures = new LinkedList();
        input = new EOModelEditorInput(fileEditorInput, failures);
        handleModelErrors(failures);
      }
      else {
        throw new PartInitException("Unknown editor input: " + _editorInput + ".");
      }
      EOModelEditorInput oldInput = (EOModelEditorInput) getEditorInput();
      if (oldInput != null) {
        oldInput.getModel().removePropertyChangeListener(myDirtyModelListener);
      }
      input.getModel().addPropertyChangeListener(myDirtyModelListener);
      updatePartName();
      super.init(_site, input);
      _site.setSelectionProvider(this);
    }
    catch (Exception e) {
      throw new PartInitException("Failed to create EOModelEditorInput for " + _editorInput + ".", e);
    }
  }

  protected void handleModelErrors(List _failures) {
    // TODO: Display errors to user!
    Iterator failuresIter = _failures.iterator();
    while (failuresIter.hasNext()) {
      EOModelVerificationFailure failure = (EOModelVerificationFailure) failuresIter.next();
      System.out.println("EOModelEditor.init: " + failure);
    }
  }

  protected void updatePartName() {
    IEditorInput input = getEditorInput();
    String partName;
    if (input != null) {
      EOModel model = ((EOModelEditorInput) input).getModel();
      partName = model.getName();
    }
    else {
      partName = "EOModeler";
    }
    setPartName(partName);
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  protected void pageChange(int _newPageIndex) {
    super.pageChange(_newPageIndex);
    ISelectionProvider selectionProvider = (ISelectionProvider) getEditor(_newPageIndex);
    getSite().setSelectionProvider(selectionProvider);
  }

  public void resourceChanged(final IResourceChangeEvent _event) {
    if (_event.getType() == IResourceChangeEvent.PRE_CLOSE) {
      final EOModelEditorInput input = (EOModelEditorInput) myEntitiesTableEditor.getEditorInput();
      Display.getDefault().asyncExec(new Runnable() {
        public void run() {
          IWorkbenchPage[] pages = getSite().getWorkbenchWindow().getPages();
          for (int pageNum = 0; pageNum < pages.length; pageNum++) {
            if (input.getFile().getProject().equals(_event.getResource())) {
              IEditorPart editorPart = pages[pageNum].findEditor(input);
              pages[pageNum].closeEditor(editorPart, true);
            }
          }
        }
      });
    }
  }

  public EOEntitiesTableEditor getEntitiesTableEditor() {
    return myEntitiesTableEditor;
  }

  public EOEntityEditor getEntityEditor() {
    return myEntityEditor;
  }

  public void setActivePage(int _pageIndex) {
    super.setActivePage(_pageIndex);
  }

  public ISelection getSelection() {
    return mySelection;
  }

  public void setSelection(ISelection _selection) {
    setSelection(_selection, true);
  }

  public synchronized void setSelection(ISelection _selection, boolean _updateOutline) {
    // MS: it's really easy to setup a selection loop with so many interrelated
    // components.  In reality, we only want the top selection to count, and if
    // the call to setSelection is called again from within this stack, then
    // that's pretty much bad.
    mySelectionDepth++;
    try {
      if (mySelectionDepth == 1) {
        IStructuredSelection previousSelection = mySelection;
        IStructuredSelection selection = (IStructuredSelection) _selection;
        mySelection = selection;
        if (previousSelection == null || !selection.toList().equals(previousSelection.toList())) {
          Object selectedObject = null;
          if (!selection.isEmpty()) {
            selectedObject = selection.getFirstElement();
          }
          if (selectedObject instanceof EOModel) {
            //EOModel selectedModel = (EOModel) selectedObject;
            setSelectedEntity(null);
            setActivePage(0);
          }
          else if (selectedObject instanceof EOEntity) {
            EOEntity selectedEntity = (EOEntity) selectedObject;
            setSelectedEntity(selectedEntity);
          }
          else if (selectedObject instanceof EOAttribute) {
            //((ISelectionProvider) getActiveEditor()).setSelection(_selection);
          }
          else if (selectedObject instanceof EORelationship) {
            EORelationship selectedRelationship = (EORelationship) selectedObject;
            setSelectedEntity(selectedRelationship.getEntity());
            getEntityEditor().setSelection(selection);
            setActivePage(1);
          }
          else if (selectedObject instanceof EORelationshipPath) {
            EORelationshipPath selectedRelationshipPath = (EORelationshipPath) selectedObject;
            setSelectedEntity(selectedRelationshipPath.getChildRelationship().getEntity());
            getEntityEditor().setSelection(new StructuredSelection(selectedRelationshipPath.getChildRelationship()));
            setActivePage(1);
          }
          if (_updateOutline) {
            getContentOutlinePage().setSelection(selection);
          }
          fireSelectionChanged(selection);
        }
      }
    }
    finally {
      mySelectionDepth--;
    }
  }

  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    mySelectionChangedListeners.add(_listener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    mySelectionChangedListeners.remove(_listener);
  }

  protected void fireSelectionChanged(ISelection _selection) {
    Object[] selectionChangedListeners = mySelectionChangedListeners.getListeners();
    SelectionChangedEvent selectionChangedEvent = new SelectionChangedEvent(this, _selection);
    for (int listenerNum = 0; listenerNum < selectionChangedListeners.length; listenerNum++) {
      ISelectionChangedListener listener = (ISelectionChangedListener) selectionChangedListeners[listenerNum];
      listener.selectionChanged(selectionChangedEvent);
    }
  }

  protected void editorDirtyStateChanged() {
    firePropertyChange(IEditorPart.PROP_DIRTY);
  }

  protected class EOModelContentSelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
      setSelection(selection, false);
    }
  }

  protected class EOModelSelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
      setSelection(selection);
    }
  }

  protected class EOEntitySelectionChangedListener implements ISelectionChangedListener {
    public void selectionChanged(SelectionChangedEvent _event) {
      IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
      setSelection(selection);
    }
  }

  protected class DirtyModelListener implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent _event) {
      EOModelEditor.this.editorDirtyStateChanged();
    }
  }
}