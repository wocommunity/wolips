package org.objectstyle.wolips.refactoring;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.internal.core.SourceType;
import org.eclipse.jdt.internal.corext.refactoring.changes.CopyResourceChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.DeleteFileChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.DeleteFolderChange;
import org.eclipse.jdt.internal.corext.refactoring.changes.RenameResourceChange;
import org.eclipse.ltk.core.refactoring.Change;
import org.eclipse.ltk.core.refactoring.CompositeChange;
import org.eclipse.ltk.core.refactoring.RefactoringStatus;
import org.eclipse.ltk.core.refactoring.participants.CheckConditionsContext;
import org.eclipse.ltk.core.refactoring.participants.RenameArguments;
import org.eclipse.ltk.core.refactoring.participants.RenameParticipant;

/**
 * Plugs into the refactoring process and renames a WOComponent by 
 * moving the .api, creating a new .wo, copying the .html, .wod, .woo over and deleting the old .wo
 * @author Mike Schrag original version
 * @author ak wolips integration
 */

public class WOComponentRenameParticipant extends RenameParticipant {
    private SourceType mySourceType;
    
    public WOComponentRenameParticipant() {
    }
    
    protected boolean initialize(Object _element) {
        boolean initialized = false;
        try {
            if (_element instanceof SourceType) {
                mySourceType = (SourceType) _element;
                initialized = PluginUtils.isOfType(mySourceType, "com.webobjects.appserver.WOComponent");
            }
        }
        catch (Throwable e) {
            e.printStackTrace();
            initialized = false;
        }
        return initialized;
    }
    
    public String getName() {
        return "Rename WOComponent Files";
    }
    
    public RefactoringStatus checkConditions(IProgressMonitor _pm, CheckConditionsContext _context) throws OperationCanceledException {
        RefactoringStatus refactoringStatus = new RefactoringStatus();
        return refactoringStatus;
    }
    
    public Change createChange(IProgressMonitor _pm) throws CoreException, OperationCanceledException {
        RenameArguments arguments = getArguments();
        String oldName = mySourceType.getElementName();
        String newName = arguments.getNewName();
        IProject project = mySourceType.getJavaProject().getProject();
        IFolder oldWoFolder = (IFolder) PluginUtils.findResource(project, oldName + ".wo");
        IFile oldApiFile = (IFile) PluginUtils.findResource(project, oldName + ".api");
        CompositeChange compositeChange;
        if (oldWoFolder != null || oldApiFile != null) {
            compositeChange = new CompositeChange("Rename WOComponent Files");
            if (oldApiFile != null) {
                // compositeChange.add(new RenameResourceChange(oldApiFile, newName + ".api"));
                CompositeChange renameApiFileChange = new CompositeChange("Rename " + oldApiFile.getName() + ".");
                renameApiFileChange.add(new CopyResourceChange(oldApiFile, oldApiFile.getParent(), new FixedNewNameQuery(newName + ".api")));
                renameApiFileChange.add(new DeleteFileChange(oldApiFile));
                compositeChange.add(renameApiFileChange);
            }
            if (oldWoFolder != null) {
                IFolder newWoFolder = oldWoFolder.getParent().getFolder(new Path(newName + ".wo"));
                CompositeChange renameWoFolderChange = new CompositeChange("Rename " + oldWoFolder.getName() + ".");

                //compositeChange.add(createRenameChange(woFolder, newName + ".wo"));
                renameWoFolderChange.add(new CreateFolderChange(newWoFolder));
                String[] renameExtensions = { ".html", ".wod", ".woo" };
                for (int i = 0; i < renameExtensions.length; i++) {
                    IFile woFile = oldWoFolder.getFile(oldName + renameExtensions[i]);
                    if (woFile.exists()) {
                        //compositeChange.add(new RenameResourceChange(woFile, newName + renameExtensions[i]));
                        CompositeChange renameWoFileChange = new CompositeChange("Rename " + woFile.getName() + ".");
                        renameWoFileChange.add(new CopyResourceChange(woFile, newWoFolder, new FixedNewNameQuery(newName + renameExtensions[i])));
                        renameWoFileChange.add(new DeleteFileChange(woFile));
                        renameWoFolderChange.add(renameWoFileChange);
                    }
                }
                renameWoFolderChange.add(new DeleteFolderChange(oldWoFolder));
                compositeChange.add(renameWoFolderChange);
            }
        }
        else {
            compositeChange = null;
        }
        return compositeChange;
    }
}
