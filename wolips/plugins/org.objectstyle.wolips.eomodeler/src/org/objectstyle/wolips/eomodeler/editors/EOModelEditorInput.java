package org.objectstyle.wolips.eomodeler.editors;

import java.io.IOException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPersistableElement;
import org.objectstyle.wolips.eomodeler.model.EOModel;
import org.objectstyle.wolips.eomodeler.model.EclipseEOModelGroupFactory;

public class EOModelEditorInput implements IEditorInput {
  private IFileEditorInput myFileEditorInput;
  private EOModel myModel;

  public EOModelEditorInput(IFileEditorInput _fileEditorInput) throws CoreException, IOException {
    myFileEditorInput = _fileEditorInput;
    IFile file = myFileEditorInput.getFile();
    myModel = EclipseEOModelGroupFactory.createModel(file);
  }

  public EOModel getModel() {
    return myModel;
  }

  public boolean exists() {
    return myFileEditorInput.exists();
  }

  public Object getAdapter(Class _adapter) {
    return myFileEditorInput.getAdapter(_adapter);
  }

  public IFile getFile() {
    return myFileEditorInput.getFile();
  }

  public ImageDescriptor getImageDescriptor() {
    return myFileEditorInput.getImageDescriptor();
  }

  public String getName() {
    return myFileEditorInput.getName();
  }

  public IPersistableElement getPersistable() {
    return myFileEditorInput.getPersistable();
  }

  public IStorage getStorage() throws CoreException {
    return myFileEditorInput.getStorage();
  }

  public String getToolTipText() {
    return myFileEditorInput.getToolTipText();
  }

}
