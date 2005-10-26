package org.objectstyle.wolips.wodclipse.builder;

import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.builder.AbstractDeltaCleanBuilder;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.wod.WodReconcilingStrategy;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class WodBuilder extends AbstractDeltaCleanBuilder {
  private boolean myValidateWOD;

  public WodBuilder() {
	  super();
  }

  public boolean buildStarted(int _kind, Map _args, IProgressMonitor _monitor, IProject _project, Map _buildCache) {
    myValidateWOD = Preferences.getPREF_VALIDATE_WOD_ON_BUILD();
    return false;
  }

  public boolean buildPreparationDone(int _kind, Map _args, IProgressMonitor _monitor, IProject _project, Map _buildCache) {
    return false;
  }

  public void handleClasses(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
  }

  public void handleSource(IResource _resource, IProgressMonitor _progressMonitor, Map _buildCache) {
  }
  
  public void handleClasspath(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
  }

  public void handleOther(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
  }

  public void handleWebServerResources(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
  }

  public void handleWoappResources(IResource _resource, IProgressMonitor _monitor, Map _buildCache) {
    if (myValidateWOD) {
      try {
        IFile wodFile = null;
        if (_resource instanceof IFile) {
          IFile file = (IFile) _resource;
          String fileExtension = file.getFileExtension();
          if ("wod".equals(fileExtension)) {
            wodFile = file;
          }
          else if ("html".equals(fileExtension) && _resource.getParent().getName().endsWith(".wo")) {
        	  String fileName = file.getName();
              fileName = fileName.substring(0, fileName.length() - ("." + fileExtension).length());
              IFolder folder = (IFolder)_resource.getParent();
              wodFile = (IFile)folder.findMember(fileName + "wod");
          }
          else if ("api".equals(fileExtension)) {
        	  //should we really do something with the component when we change the api?
        	  //shoulnd't we validate all files using the api?
        	  if(5==5) {
        		  return;
        	  }
            String fileName = file.getName();
            fileName = fileName.substring(0, fileName.length() - ("." + fileExtension).length());
            List wodResources = WorkbenchUtilitiesPlugin.findResourcesInProjectByNameAndExtensions(_resource.getProject(), fileName, new String[] { "wod" }, false);
            if (wodResources != null && wodResources.size() > 0) {
              wodFile = (IFile) wodResources.get(0);
            }
          }
          if (wodFile != null) {
            if (_monitor != null) {
              _monitor.subTask("Building WOD file " + wodFile.getName() + " ...");
            }

            WodFileDocumentProvider provider = new WodFileDocumentProvider();
            FileEditorInput input = new FileEditorInput(wodFile);
            provider.connect(input);
            try {
              IDocument document = provider.getDocument(input);
              WodReconcilingStrategy.reconcileWodModel(wodFile, document, _buildCache, _buildCache);
            }
            finally {
              provider.disconnect(input);
            }
          }
        }
      }
      catch (Throwable e) {
        WodclipsePlugin.getDefault().log(e);
      }
    }
  }
}