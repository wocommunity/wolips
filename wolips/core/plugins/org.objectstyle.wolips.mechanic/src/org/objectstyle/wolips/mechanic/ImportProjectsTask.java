package org.objectstyle.wolips.mechanic;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;

import com.google.eclipse.mechanic.CompositeTask;

public class ImportProjectsTask extends CompositeTask {
  private static final Logger log = Logger.getLogger(ImportProjectsTask.class.getName());
  
  private String _id;
  private String _title;
  private String _description;
  private List<IPath> _importPaths;
  private boolean _reconcileProjects;

  private List<IProjectDescription> _projectDescriptions;

  public ImportProjectsTask(String id, String title, String description, List<IPath> importPaths, boolean reconcileProjects) {
    _id = id.replace(':', '_');
    _title = title;
    _description = description;
    _importPaths = importPaths;
    _reconcileProjects = reconcileProjects;
  }

  @Override
  public String getId() {
    return "ImportProjectsTask-" + _id;
  }

  public String getDescription() {
    return _description;
  }

  public String getTitle() {
    return _title;
  }

  protected List<IPath> findProjectFolderPaths() {
    List<IPath> projectFolderPaths = new LinkedList<IPath>();
    
    for (IPath importPath : _importPaths) {
      File importFolder;
      if (importPath.isAbsolute()) {
        importFolder = importPath.toFile();
      }
      else {
        importFolder = ResourcesPlugin.getWorkspace().getRoot().getRawLocation().append(importPath).toFile(); 
      }
      
      // Support the root path being a project
      File importProjectFilePath = new File(importFolder, ".project");
      if (importProjectFilePath.exists()) {
        projectFolderPaths.add(new Path(importProjectFilePath.getAbsolutePath()));
      }
  
      // ... but also check the child folders
      File[] possibleProjectFolders = importFolder.listFiles();
      if (possibleProjectFolders != null) {
        for (File possibleProjectFolder : possibleProjectFolders) {
          if (possibleProjectFolder.isDirectory()) {
            File projectFilePath = new File(possibleProjectFolder, ".project");
            if (projectFilePath.exists()) {
              projectFolderPaths.add(new Path(projectFilePath.getAbsolutePath()));
            }
          }
        }
      }
    }

    return projectFolderPaths;
  }

  public synchronized boolean evaluate() {
    _projectDescriptions = null;

    boolean allProjectsExist = !_reconcileProjects && Activator.getDefault().getPreferenceStore().getBoolean(getId());

    if (!allProjectsExist) {
      allProjectsExist = true;

      _projectDescriptions = new LinkedList<IProjectDescription>();
      IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
      for (IPath projectFolderPath : findProjectFolderPaths()) {
        try {
          IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().loadProjectDescription(projectFolderPath);
          if (!workspaceRoot.getProject(projectDescription.getName()).exists()) {
            _projectDescriptions.add(projectDescription);
            allProjectsExist = false;
          }
        }
        catch (Throwable e) {
          log.log(Level.SEVERE, "Failed to import " + projectFolderPath + ".", e);
        }
      }
    }

    return allProjectsExist;
  }

  public synchronized void run() {
    final List<IProjectDescription> projectDescriptions = _projectDescriptions;
    if (projectDescriptions == null) {
      return;
    }

    new WorkspaceJob("Importing") {
      @Override
      public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
        try {
          for (IProjectDescription projectDescription : projectDescriptions) {
            try {
              IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectDescription.getName());
              monitor.beginTask("Importing " + project.getName() + " ...", 100);
              project.create(projectDescription, new SubProgressMonitor(monitor, 30));
              project.open(IResource.BACKGROUND_REFRESH, new SubProgressMonitor(monitor, 70));
            }
            catch (Throwable t) {
              System.out.println("ImportProjectsTask.run: " + t.getMessage());
            }
          }
          if (_reconcileProjects) {
            Activator.getDefault().getPreferenceStore().setValue(getId(), true);
          }
        }
        finally {
          monitor.done();
        }
        return Status.OK_STATUS;
      }
    }.schedule();
  }
}
