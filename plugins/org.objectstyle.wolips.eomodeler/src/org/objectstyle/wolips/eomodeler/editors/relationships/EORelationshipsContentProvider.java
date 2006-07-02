package org.objectstyle.wolips.eomodeler.editors.relationships;

import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.objectstyle.wolips.eomodeler.model.EOEntity;
import org.objectstyle.wolips.eomodeler.model.EORelationship;

public class EORelationshipsContentProvider implements IStructuredContentProvider {
  public Object[] getElements(Object _inputElement) {
    EOEntity entity = (EOEntity) _inputElement;
    List relationshipsList = entity.getRelationships();
    EORelationship[] relationships = (EORelationship[]) relationshipsList.toArray(new EORelationship[relationshipsList.size()]);
    return relationships;
  }

  public void dispose() {
  }

  public void inputChanged(Viewer _viewer, Object _oldInput, Object _newInput) {
  }
}
