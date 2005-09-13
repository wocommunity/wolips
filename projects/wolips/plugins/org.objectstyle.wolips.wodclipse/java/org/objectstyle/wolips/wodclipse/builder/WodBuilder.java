package org.objectstyle.wolips.wodclipse.builder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.part.FileEditorInput;
import org.objectstyle.wolips.core.resources.builder.IBuilder;
import org.objectstyle.wolips.wodclipse.wod.WodFileDocumentProvider;
import org.objectstyle.wolips.wodclipse.wod.WodReconcilingStrategy;
import org.objectstyle.wolips.workbenchutilities.WorkbenchUtilitiesPlugin;

public class WodBuilder implements IBuilder {
  public WodBuilder() {
  }

  public void buildStarted(int _kind, Map _args, IProgressMonitor _monitor, IProject _project) {
    // System.out.println("WodBuilder.buildStarted: " + _project + ", " + _kind);
    if (_kind == IncrementalProjectBuilder.FULL_BUILD || _kind == IncrementalProjectBuilder.CLEAN_BUILD) {
      try {
        _project.accept(new WodBuilderResourceVisitor(_monitor));
      }
      catch (CoreException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
  }

  public void visitingDeltasDone(int _kind, Map _args, IProgressMonitor _monitor, IProject _project) {
    // System.out.println("WodBuilder.visitingDeltasDone: " + _project + ", " + _kind);
  }

  public void handleClassesDelta(IResourceDelta _delta) {
    // System.out.println("WodBuilder.handleClassesDelta: " + _delta);
  }

  public void handleWoappResourcesDelta(IResourceDelta _delta) {
    try {
      IResource resource = _delta.getResource();
      WodBuilder.visit(IncrementalProjectBuilder.INCREMENTAL_BUILD, resource, null, new HashMap(), new HashMap());
      // System.out.println("WodBuilder.handleWoappResourcesDelta: " + _delta);
    }
    catch (CoreException e) {
      e.printStackTrace();
    }
  }

  public void handleWebServerResourcesDelta(IResourceDelta _delta) {
    //System.out.println("WodBuilder.handleWebServerResourcesDelta: " + _delta);
  }

  public void handleOtherDelta(IResourceDelta _delta) {
    //System.out.println("WodBuilder.handleOtherDelta: " + _delta);
  }

  public void classpathChanged(IResourceDelta _delta) {
    //System.out.println("WodBuilder.classpathChanged: " + _delta);
  }

  public static boolean visit(int _kind, IResource _resource, IProgressMonitor _progressMonitor, Map _elementNameToTypeCache, Map _typeToApiModelWoCache) throws CoreException {
    boolean visitChildren;
    if (_resource.isDerived() || "dist".equals(_resource.getName())) {
      visitChildren = false;
    }
    else {
      visitChildren = true;
      IFile wodFile = null;
      if (_resource instanceof IFile) {
        IFile file = (IFile) _resource;
        String fileExtension = file.getFileExtension();
        if ("wod".equals(fileExtension)) {
          wodFile = file;
        }
        else if ((_kind == IncrementalProjectBuilder.AUTO_BUILD || _kind == IncrementalProjectBuilder.INCREMENTAL_BUILD) && (("html".equals(fileExtension) && _resource.getParent().getName().endsWith(".wo")) || "api".equals(fileExtension))) {
          String fileName = file.getName();
          fileName = fileName.substring(0, fileName.length() - ("." + fileExtension).length());
          List wodResources = WorkbenchUtilitiesPlugin.findResourcesInProjectByNameAndExtensions(_resource.getProject(), fileName, new String[] { "wod" }, false);
          if (wodResources != null && wodResources.size() > 0) {
            wodFile = (IFile) wodResources.get(0);
          }
        }
        if (wodFile != null) {
          if (_progressMonitor != null) {
            _progressMonitor.subTask("Building WOD file " + wodFile.getName() + " ...");
          }

          WodFileDocumentProvider provider = new WodFileDocumentProvider();
          FileEditorInput input = new FileEditorInput(wodFile);
          provider.connect(input);
          try {
            IDocument document = provider.getDocument(input);
            WodReconcilingStrategy.reconcileWodModel(wodFile, document, _elementNameToTypeCache, _typeToApiModelWoCache);
          }
          finally {
            provider.disconnect(input);
          }
        }
        visitChildren = false;
      }
    }
    return visitChildren;
  }

  public static class WodBuilderResourceVisitor implements IResourceVisitor {
    private IProgressMonitor myProgressMonitor;
    private Map myElementNameToTypeCache;
    private Map myTypeToApiModelWoCache;

    public WodBuilderResourceVisitor(IProgressMonitor _progressMonitor) {
      myProgressMonitor = _progressMonitor;
      myElementNameToTypeCache = new HashMap();
      myTypeToApiModelWoCache = new HashMap();
    }

    public boolean visit(IResource _resource) throws CoreException {
      boolean visitChildren;
      if (myProgressMonitor.isCanceled()) {
        visitChildren = false;
      }
      else {
        visitChildren = WodBuilder.visit(IncrementalProjectBuilder.FULL_BUILD, _resource, myProgressMonitor, myElementNameToTypeCache, myTypeToApiModelWoCache);
      }
      return visitChildren;
    }
  }
}