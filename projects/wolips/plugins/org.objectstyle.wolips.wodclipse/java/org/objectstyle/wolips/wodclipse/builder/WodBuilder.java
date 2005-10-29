package org.objectstyle.wolips.wodclipse.builder;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.builder.AbstractDeltaCleanBuilder;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.wod.WodReconcilingStrategy;
import org.objectstyle.wolips.wodclipse.wod.model.WodProblem;

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
    if (myValidateWOD) {
      try {
        touchRelatedResources(_resource, _progressMonitor, _buildCache);
      }
      catch (CoreException e) {
        WodclipsePlugin.getDefault().log(e);
      }
    }
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
        boolean validateWodFile = false;
        if (_resource instanceof IFile) {
          IFile file = (IFile) _resource;
          String fileExtension = file.getFileExtension();
          LocalizedComponentsLocateResult localizedComponentsLocateResult = new LocalizedComponentsLocateResult();
          if ("wod".equals(fileExtension)) {
            // "prefill" the search results
            localizedComponentsLocateResult.add(file.getParent());
            validateWodFile = true;
          }
          else if ("html".equals(fileExtension) && _resource.getParent().getName().endsWith(".wo")) {
            // "prefill" the search results
            localizedComponentsLocateResult.add(file.getParent());
            validateWodFile = true;
          }
          else if ("api".equals(fileExtension)) {
            // "prefill" the search results
            localizedComponentsLocateResult.add(file);
            //should we really do something with the component when we change the api?
            //shoulnd't we validate all files using the api?
            validateWodFile = false;

            try {
              touchRelatedResources(_resource, _monitor, _buildCache);
            }
            catch (CoreException e) {
              WodclipsePlugin.getDefault().log(e);
            }
          }

          if (validateWodFile) {
            validateWodFile(localizedComponentsLocateResult, file.getProject(), file.getName(), _monitor, _buildCache);
          }
        }
      }
      catch (Throwable e) {
        WodclipsePlugin.getDefault().log(e);
      }
    }
  }

  protected void touchRelatedResources(IResource _resource, IProgressMonitor _progressMonitor, Map _buildCache) throws CoreException {
    //System.out.println("WodBuilder.touchRelatedResources: looking for problems related to " + _resource);
    if (_progressMonitor != null) {
      _progressMonitor.subTask("Touching files related to " + _resource.getName() + " ...");
    }
    
    IMarker[] markers = _resource.getProject().findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
    Set relatedResources = new HashSet();
    String name = _resource.getName();
    for (int markerNum = 0; markerNum < markers.length; markerNum++) {
      //System.out.println("WodBuilder.touchRelatedResources: Checking " + markers[markerNum]);
      String relatedToFileNames = (String)markers[markerNum].getAttribute(WodProblem.RELATED_TO_FILE_NAMES);
      //System.out.println("WodBuilder.touchRelatedResources: problem in " + markers[markerNum].getResource().getName() + " is related to " + relatedToFileNames);
      if (relatedToFileNames != null && relatedToFileNames.indexOf(name) != -1) {
        //System.out.println("WodBuilder.touchRelatedResources:  ... which is this: " + _resource);
        relatedResources.add(markers[markerNum].getResource());
      }
    }
    
    Iterator relatedResourcesIter = relatedResources.iterator();
    while (relatedResourcesIter.hasNext()) {
      IResource relatedResource = (IResource)relatedResourcesIter.next();
      handleWoappResources(relatedResource, _progressMonitor, _buildCache);
      //relatedResource.touch(_progressMonitor);
    }
  }

  protected void validateWodFile(LocalizedComponentsLocateResult _locateResults, IProject _project, String _resourceName, IProgressMonitor _progressMonitor, Map _buildCache) throws CoreException, LocateException {
    if (_progressMonitor != null) {
      _progressMonitor.subTask("Locating components for " + _resourceName + " ...");
    }

    ComponentLocateScope componentLocateScope = ComponentLocateScope.createLocateScope(_project, _resourceName);
    Locate locate = new Locate(componentLocateScope, _locateResults);
    locate.locate();

    IFile wodFile = _locateResults.getFirstWodFile();
    if (wodFile != null) {
      if (_progressMonitor != null) {
        _progressMonitor.subTask("Building WOD file " + wodFile.getName() + " ...");
      }

      FileEditorInput input = new FileEditorInput(wodFile);
      WodFileDocumentProvider provider = new WodFileDocumentProvider();
      provider.connect(input);
      try {
        IDocument document = provider.getDocument(input);
        WodReconcilingStrategy.reconcileWodModel(document, _locateResults, _buildCache, _buildCache);
      }
      finally {
        provider.disconnect(input);
      }
    }
  }
}