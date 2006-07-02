package org.objectstyle.wolips.eomodeler.editors.attributes;

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

public class EOAttributesTableEditor extends EditorPart implements IEntityEditor {
  private EOAttributesTableViewer myAttributesTableViewer;
  private EOModelEditorInput myEditorInput;
  private EOEntity myEntity;

  public EOAttributesTableEditor() {
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
    updateAttributesTableViewer();
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
    myAttributesTableViewer = new EOAttributesTableViewer(_parent, SWT.NONE);
    myAttributesTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
    updateAttributesTableViewer();
  }

  public void setFocus() {
  }

  protected void updateAttributesTableViewer() {
    if (myAttributesTableViewer != null) {
      myAttributesTableViewer.setEntity(myEntity);
    }
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myAttributesTableViewer.addSelectionChangedListener(_listener);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myAttributesTableViewer.removeSelectionChangedListener(_listener);
  }
}
