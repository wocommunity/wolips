package org.objectstyle.wolips.eomodeler.editors.entity;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.part.EditorPart;
import org.objectstyle.wolips.eomodeler.editors.EOModelEditorInput;
import org.objectstyle.wolips.eomodeler.editors.IEntityEditor;
import org.objectstyle.wolips.eomodeler.editors.attributes.EOAttributesTableViewer;
import org.objectstyle.wolips.eomodeler.editors.relationships.EORelationshipsTableViewer;
import org.objectstyle.wolips.eomodeler.model.EOEntity;

public class EOEntityEditor extends EditorPart implements IEntityEditor {
  private EOAttributesTableViewer myAttributesTableViewer;
  private EORelationshipsTableViewer myRelationshipsTableViewer;
  private EOModelEditorInput myEditorInput;
  private EOEntity myEntity;

  public EOEntityEditor() {
  }

  public void setEntity(EOEntity _entity) {
    myEntity = _entity;
    updateTableViewers();
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
    SashForm sashForm = new SashForm(_parent, SWT.VERTICAL);
    sashForm.setLayoutData(new GridData(GridData.FILL_BOTH));
    myAttributesTableViewer = new EOAttributesTableViewer(sashForm, SWT.NONE);
    myAttributesTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
    myRelationshipsTableViewer = new EORelationshipsTableViewer(sashForm, SWT.NONE);
    myRelationshipsTableViewer.setLayoutData(new GridData(GridData.FILL_BOTH));
    sashForm.setWeights(new int[] { 2, 1 });
    updateTableViewers();
  }

  public void setFocus() {
  }

  protected void updateTableViewers() {
    if (myRelationshipsTableViewer != null) {
      myRelationshipsTableViewer.setEntity(myEntity);
    }
    if (myAttributesTableViewer != null) {
      myAttributesTableViewer.setEntity(myEntity);
    }
  }

  public void addSelectionChangedListener(ISelectionChangedListener _listener) {
    myAttributesTableViewer.addSelectionChangedListener(_listener);
    myRelationshipsTableViewer.addSelectionChangedListener(_listener);
  }

  public void removeSelectionChangedListener(ISelectionChangedListener _listener) {
    myAttributesTableViewer.removeSelectionChangedListener(_listener);
    myRelationshipsTableViewer.removeSelectionChangedListener(_listener);
  }
}
