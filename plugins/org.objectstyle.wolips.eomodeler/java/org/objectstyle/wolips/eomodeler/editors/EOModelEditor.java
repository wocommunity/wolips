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
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;
import org.eclipse.ui.views.properties.IPropertySheetPage;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.objectstyle.wolips.eomodeler.Activator;
import org.objectstyle.wolips.eomodeler.EOModelerPerspectiveFactory;
import org.objectstyle.wolips.eomodeler.Messages;
import org.objectstyle.wolips.eomodeler.editors.entities.EOEntitiesTableEditor;
import org.objectstyle.wolips.eomodeler.editors.entity.EOEntityEditor;
import org.objectstyle.wolips.eomodeler.model.EOAttribute;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EOFetchSpecification;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EORelationship;
import org.objectstyle.wolips.eomodeler.model.EORelationshipPath;
import org.objectstyle.wolips.eomodeler.model.EclipseEOModelGroupFactory;
import org.objectstyle.wolips.eomodeler.outline.EOModelContentOutlinePage;
import org.objectstyle.wolips.eomodeler.utils.ComparisonUtils;

public class EOModelEditor extends MultiPageEditorPart implements IResourceChangeListener, ITabbedPropertySheetPageContributor, ISelectionProvider, IEOModelEditor {
  public static final String EOMODEL_EDITOR_ID = "org.objectstyle.wolips.eomodeler.editors.EOModelEditor"; //$NON-NLS-1$

  public static final int EOMODEL_PAGE = 0;
  public static final int EOENTITY_PAGE = 1;
  
  private EOEntitiesTableEditor myEntitiesTableEditor;
  private EOEntityEditor myEntityEditor;
  private EOModelContentOutlinePage myContentOutlinePage;
  
  private ListenerList mySelectionChangedListeners;
  private IStructuredSelection mySelection;
  private PropertyChangeListener myDirtyModelListener;
  private EntitiesChangeRefresher myEntitiesChangeListener;

  private EOEntity mySelectedEntity;
  private EOEntity myOpeningEntity;
  private EOModel myModel;
  private Set myLoadFailures;

  private int mySelectionDepth;

  public EOModelEditor() {
    mySelectionChangedListeners = new ListenerList();
    myDirtyModelListener = new DirtyModelListener();
    myEntitiesChangeListener = new EntitiesChangeRefresher();
    ResourcesPlugin.getWorkspace().addResourceChangeListener(this);
  }

  public EOModelContentOutlinePage getContentOutlinePage() {
    if (myContentOutlinePage == null) {
      myContentOutlinePage = new EOModelContentOutlinePage(myModel);
      myContentOutlinePage.addSelectionChangedListener(new EOModelContentSelectionChangedListener());
    }
    return myContentOutlinePage;
  }

  public EOModel getModel() {
    return myModel;
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
      IContentOutlinePage outlinePage = getContentOutlinePage();
      adapter = outlinePage;
    }
    else {
      adapter = super.getAdapter(_adapterClass);
    }
    return adapter;
  }

  protected void createPages() {
    try {
      myEntitiesTableEditor = new EOEntitiesTableEditor();
      addPage(EOModelEditor.EOMODEL_PAGE, myEntitiesTableEditor, getEditorInput());
      setPageText(EOModelEditor.EOMODEL_PAGE, Messages.getString("EOModelEditor.entitiesTab")); //$NON-NLS-1$

      myEntityEditor = new EOEntityEditor();
      addPage(EOModelEditor.EOENTITY_PAGE, myEntityEditor, getEditorInput());
      setPageText(EOModelEditor.EOENTITY_PAGE, Messages.getString("EOModelEditor.noEntitySelected")); //$NON-NLS-1$

      EOModelSelectionChangedListener modelSelectionChangedListener = new EOModelSelectionChangedListener();
      myEntitiesTableEditor.addSelectionChangedListener(modelSelectionChangedListener);
      myEntitiesTableEditor.setModel(myModel);

      EOEntitySelectionChangedListener entitySelectionChangedListener = new EOEntitySelectionChangedListener();
      myEntityEditor.addSelectionChangedListener(entitySelectionChangedListener);

      if (myOpeningEntity != null) {
        setSelectedEntity(myOpeningEntity);
        setActivePage(EOModelEditor.EOENTITY_PAGE);
      }
    }
    catch (PartInitException e) {
      ErrorDialog.openError(getSite().getShell(), "Error creating editor.", null, e.getStatus());
    }
  }

  public void setSelectedEntity(EOEntity _selectedEntity) {
    if (!ComparisonUtils.equals(mySelectedEntity, _selectedEntity)) {
      mySelectedEntity = _selectedEntity;
      myEntitiesTableEditor.setSelectedEntity(_selectedEntity);
      myEntityEditor.setEntity(_selectedEntity);
      if (_selectedEntity == null) {
        EOModelEditor.this.setPageText(EOModelEditor.EOENTITY_PAGE, Messages.getString("EOModelEditor.noEntitySelected")); //$NON-NLS-1$
      }
      else {
        String entityName = _selectedEntity.getName();
        if (entityName == null) {
          entityName = "?"; //$NON-NLS-1$
        }
        EOModelEditor.this.setPageText(EOModelEditor.EOENTITY_PAGE, entityName);
      }
      updatePartName();
    }
  }

  public void dispose() {
    ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
    super.dispose();
  }

  public void doSave(IProgressMonitor _monitor) {
    try {
      IEditorInput input = getEditorInput();
      if (input != null && myModel != null) {
        Set failures = new HashSet();
        myModel.verify(failures);
        handleModelErrors(failures);

        IFile originalFile = ((IFileEditorInput) input).getFile();
        IContainer originalFolder = originalFile.getParent();
        myModel.saveToFolder(originalFolder.getParent().getLocation().toFile());
        myModel.setDirty(false);
        originalFolder.refreshLocal(IResource.DEPTH_INFINITE, _monitor);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    catch (CoreException e) {
      e.printStackTrace();
    }
    /**/
  }

  public void doSaveAs() {
    doSave(null);
  }

  public void init(IEditorSite _site, IEditorInput _editorInput) throws PartInitException {
    try {
      IWorkbench workbench = Activator.getDefault().getWorkbench();
      IWorkbenchPage workbenchPage = workbench.getActiveWorkbenchWindow().getActivePage();
      if (workbenchPage != null && !EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID.equals(workbenchPage.getPerspective().getId())) {
        boolean switchPerspectives = MessageDialog.openQuestion(Display.getDefault().getActiveShell(), Messages.getString("EOModelEditor.switchPerspectivesTitle"), Messages.getString("EOModelEditor.switchPerspectivesMessage")); //$NON-NLS-1$ //$NON-NLS-2$
        if (switchPerspectives) {
          workbench.showPerspective(EOModelerPerspectiveFactory.EOMODELER_PERSPECTIVE_ID, workbench.getActiveWorkbenchWindow());
        }
      }
    }
    catch (WorkbenchException e) {
      e.printStackTrace();
    }

    try {
      IFileEditorInput fileEditorInput;
      if (_editorInput instanceof IFileEditorInput) {
        fileEditorInput = (IFileEditorInput) _editorInput;
      }
      else {
        throw new PartInitException("Unknown editor input: " + _editorInput + ".");
      }
      if (myModel != null) {
        myModel.removePropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
        myModel.removePropertyChangeListener(EOModel.ENTITIES, myEntitiesChangeListener);
      }

      IFile file = fileEditorInput.getFile();
      String openingEntityName = null;
      if ("plist".equalsIgnoreCase(file.getFileExtension())) { //$NON-NLS-1$
        String name = file.getName();
        openingEntityName = name.substring(0, name.indexOf('.'));
        fileEditorInput = new FileEditorInput(file.getParent().getFile(new Path("index.eomodeld"))); //$NON-NLS-1$
      }

      myLoadFailures = new HashSet();
      myModel = EclipseEOModelGroupFactory.createModel(fileEditorInput.getFile().getParent(), myLoadFailures);
      if (openingEntityName != null) {
        myOpeningEntity = myModel.getEntityNamed(openingEntityName);
      }
      handleModelErrors(myLoadFailures);

      myModel.addPropertyChangeListener(EOModel.DIRTY, myDirtyModelListener);
      myModel.addPropertyChangeListener(EOModel.ENTITIES, myEntitiesChangeListener);
      super.init(_site, fileEditorInput);
      updatePartName();
      _site.setSelectionProvider(this);
    }
    catch (Exception e) {
      throw new PartInitException("Failed to create EOModelEditorInput for " + _editorInput + ".", e);
    }
  }

  protected void handleModelErrors(Set _failures) {
    if (!_failures.isEmpty()) {
      EOModelErrorDialog dialog = new EOModelErrorDialog(Display.getDefault().getActiveShell(), _failures);
      dialog.setBlockOnOpen(true);
      dialog.open();
    }
  }

  protected void updatePartName() {
    String partName;
    if (myModel != null) {
      partName = myModel.getName();
    }
    else {
      partName = Messages.getString("EOModelEditor.partName"); //$NON-NLS-1$
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
      final IFileEditorInput input = (IFileEditorInput) getEditorInput();
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
            setActivePage(EOModelEditor.EOMODEL_PAGE);
          }
          else if (selectedObject instanceof EOEntity) {
            EOEntity selectedEntity = (EOEntity) selectedObject;
            setSelectedEntity(selectedEntity);
            //setActivePage(EOModelEditor.EOENTITY_PAGE);
          }
          else if (selectedObject instanceof EOAttribute) {
            EOAttribute selectedAttribute = (EOAttribute) selectedObject;
            setSelectedEntity(selectedAttribute.getEntity());
            getEntityEditor().setSelection(_selection);
            setActivePage(EOModelEditor.EOENTITY_PAGE);
          }
          else if (selectedObject instanceof EORelationship) {
            EORelationship selectedRelationship = (EORelationship) selectedObject;
            setSelectedEntity(selectedRelationship.getEntity());
            getEntityEditor().setSelection(selection);
            setActivePage(EOModelEditor.EOENTITY_PAGE);
          }
          else if (selectedObject instanceof EOFetchSpecification) {
            EOFetchSpecification selectedFetchSpec = (EOFetchSpecification) selectedObject;
            setSelectedEntity(selectedFetchSpec.getEntity());
            getEntityEditor().setSelection(selection);
            //setActivePage(EOModelEditor.EOENTITY_PAGE);
          }
          else if (selectedObject instanceof EORelationshipPath) {
            EORelationshipPath selectedRelationshipPath = (EORelationshipPath) selectedObject;
            setSelectedEntity(selectedRelationshipPath.getChildRelationship().getEntity());
            getEntityEditor().setSelection(new StructuredSelection(selectedRelationshipPath.getChildRelationship()));
            setActivePage(EOModelEditor.EOENTITY_PAGE);
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

  public void setFocus() {
    super.setFocus();
    // MS: I'm not sure the right way to do this, but without 
    // this call, selecting a relationship in the EOModelEditor
    // before ever activing the outline would not cause the
    // property view to update.
    getSite().setSelectionProvider(this);
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

  protected void doubleClickedObjectInOutline(Object _obj) {
    if (_obj instanceof EOEntity) {
      setActivePage(EOModelEditor.EOENTITY_PAGE);
    }
  }
  protected class EOModelContentSelectionChangedListener implements ISelectionChangedListener {
    private Object mySelectedObject;
    
    public void selectionChanged(SelectionChangedEvent _event) {
      IStructuredSelection selection = (IStructuredSelection) _event.getSelection();
      Object selectedObject = selection.getFirstElement();
      setSelection(selection, false);
      if (mySelectedObject == null) {
        mySelectedObject = selectedObject;
      }
      else if (mySelectedObject == selectedObject) {
        doubleClickedObjectInOutline(selectedObject);
        mySelectedObject = null;
      }
      else {
        mySelectedObject = selectedObject;
      }
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
      String propertyName = _event.getPropertyName();
      if (EOModel.DIRTY.equals(propertyName)) {
        EOModelEditor.this.editorDirtyStateChanged();
      }
    }
  }

  protected class EntitiesChangeRefresher implements PropertyChangeListener {
    public void propertyChange(PropertyChangeEvent _event) {
      List oldEntities = (List) _event.getOldValue();
      List newEntities = (List) _event.getNewValue();
      if (newEntities != null && oldEntities != null && newEntities.size() > oldEntities.size()) {
        newEntities = new LinkedList(newEntities);
        newEntities.removeAll(oldEntities);
        EOModelEditor.this.setSelection(new StructuredSelection(newEntities));
        EOModelEditor.this.setActivePage(EOModelEditor.EOENTITY_PAGE);
      }
    }
  }
}