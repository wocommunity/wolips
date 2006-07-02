package org.objectstyle.wolips.eomodeler.editors.relationships;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditorInput;
import org.objectstyle.wolips.eomodeler.editors.IEntityEditor;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EORelationshipsTableEditor extends EditorPart implements IEntityEditor {
  private EORelationshipsTableViewer myRelationshipsTableViewer;
  private EOModelEditorInput myEditorInput;
  private EOEntity myEntity;

  public EORelationshipsTableEditor() {
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
    updateRelationshipsTableViewer();
  }
  
  public EOEntity getEntity() {
    return myEntity;
  }

  public void doSave(IProgressMonitor _monitor) {
  }

  public void doSaveAs() {
  }

  public void init(IEditorSite _site, IEditorInput _input) {
    setSite(_site);
    myEditorInput = (EOModelEditorInput) _input;
    setEntity(null);
  }

  public boolean isDirty() {
    return false;
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void createPartControl(Composite _parent) {
    myRelationshipsTableViewer = new EORelationshipsTableViewer(_parent, SWT.NONE);
    myRelationshipsTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
    updateRelationshipsTableViewer();
  }

  public void setFocus() {
  }

  protected void updateRelationshipsTableViewer() {
    if (myRelationshipsTableViewer != null) {
      myRelationshipsTableViewer.setEntity(myEntity);
    }
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myRelationshipsTableViewer.addSelectionChangedListener(_listener);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myRelationshipsTableViewer.removeSelectionChangedListener(_listener);
  }
}
