package org.objectstyle.wolips.refactoring;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jdt.internal.corext.refactoring.changes.DeleteFolderChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;


public class CreateFolderChange extends Change {
  private IFolder myFolder;
  
  public CreateFolderChange(IFolder _folder) {
    myFolder = _folder;
  }

  public String getName() {
    return "Create folder " + myFolder.getName() + ".";
  }

  public void initializeValidationData(IProgressMonitor _pm) {
  }

  public RefactoringStatus isValid(IProgressMonitor _pm) throws CoreException, OperationCanceledException {
    RefactoringStatus status = myFolder.exists() ? RefactoringStatus.createErrorStatus(myFolder.getName() + " already exists.") : new RefactoringStatus();
    return status;
  }

  public Change perform(IProgressMonitor _pm) throws CoreException {
    myFolder.create(false, true, _pm);
    DeleteFolderChange undoChange = new DeleteFolderChange(myFolder);
    return undoChange;
  }

  public Object getModifiedElement() {
    return myFolder;
  }

}
