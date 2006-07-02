package org.objectstyle.wolips.eomodeler.editors.entities;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditorInput;
import org.objectstyle.wolips.eomodeler.model.EOModel;

public class EOEntitiesTableEditor extends EditorPart {
  private EOEntitiesTableViewer myEntitiesTableViewer;
  private EOModelEditorInput myEditorInput;

  public EOEntitiesTableEditor() {
  }

  public void doSave(IProgressMonitor _monitor) {
  }

  public void doSaveAs() {
  }

  public void init(IEditorSite _site, IEditorInput _input) {
    setSite(_site);
    myEditorInput = (EOModelEditorInput) _input;
    updateEntitiesTableViewer();
  }

  public boolean isDirty() {
    return false;
  }

  public boolean isSaveAsAllowed() {
    return false;
  }

  public void createPartControl(Composite _parent) {
    myEntitiesTableViewer = new EOEntitiesTableViewer(_parent, SWT.NONE);
    myEntitiesTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
    updateEntitiesTableViewer();
  }

  public void setFocus() {
  }

  protected void updateEntitiesTableViewer() {
    if (myEntitiesTableViewer != null) {
      EOModel model = (myEditorInput != null) ? myEditorInput.getModel() : null;
      myEntitiesTableViewer.setModel(model);
    }
  }
  
  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myEntitiesTableViewer.addSelectionChangedListener(_listener);
  }
  
  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myEntitiesTableViewer.removeSelectionChangedListener(_listener);
  }
}
