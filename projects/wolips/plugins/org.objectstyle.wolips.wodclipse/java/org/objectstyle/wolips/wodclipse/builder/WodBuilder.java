package org.objectstyle.wolips.wodclipse.builder;

import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.builder.AbstractDeltaCleanBuilder;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;
import org.objectstyle.wolips.preferences.Preferences;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.wod.WodReconcilingStrategy;

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
          }
          if (validateWodFile) {
            if (_monitor != null) {
              _monitor.subTask("Locating components for " + file.getName() + " ...");
            }

            ComponentLocateScope componentLocateScope = ComponentLocateScope.createLocateScope(file);
            Locate locate = new Locate(componentLocateScope, localizedComponentsLocateResult);
            locate.locate();

            IFile wodFile = localizedComponentsLocateResult.getFirstWodFile();
            if (wodFile != null) {
              if (_monitor != null) {
                _monitor.subTask("Building WOD file " + wodFile.getName() + " ...");
              }

              FileEditorInput input = new FileEditorInput(wodFile);
              WodFileDocumentProvider provider = new WodFileDocumentProvider();
              provider.connect(input);
              try {
                IDocument document = provider.getDocument(input);
                WodReconcilingStrategy.reconcileWodModel(document, localizedComponentsLocateResult, _buildCache, _buildCache);
              }
              finally {
                provider.disconnect(input);
              }
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