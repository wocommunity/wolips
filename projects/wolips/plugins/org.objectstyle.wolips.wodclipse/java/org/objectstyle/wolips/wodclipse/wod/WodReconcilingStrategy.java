package org.objectstyle.wolips.wodclipse.wod;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.model.IWodModel;
import org.objectstyle.wolips.wodclipse.wod.model.WodModelUtils;
import org.objectstyle.wolips.wodclipse.wod.model.WodProblem;

public class WodReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
  private ITextEditor myTextEditor;
  private IProgressMonitor myProgressMonitor;
  private IDocument myDocument;

  public WodReconcilingStrategy(ITextEditor _textEditor) {
    myTextEditor = _textEditor;
  }

  public void setDocument(IDocument _document) {
    myDocument = _document;
  }

  public void reconcile() {
    IFile documentFile = null;

    IEditorInput input = myTextEditor.getEditorInput();
    if (input instanceof IFileEditorInput) {
      documentFile = ((IFileEditorInput) input).getFile();
    }

    /*
     IAnnotationModel annotationModel = getAnnotationModel();
     Iterator annotationIter = annotationModel.getAnnotationIterator();
     while (annotationIter.hasNext()) {
     Annotation annotation = (Annotation) annotationIter.next();
     if (!annotation.isPersistent()) {
     annotationModel.removeAnnotation(annotation);
     }
     }
     */

    final IFile finalDocumentFile = documentFile;
    IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
      public void run(IProgressMonitor _monitor) throws CoreException {
        try {
          IMarker[] markers = finalDocumentFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
          for (int i = 0; i < markers.length; i++) {
            markers[i].delete();
          }
        }
        catch (CoreException e) {
          WodclipsePlugin.getDefault().debug(e);
        }

        IWodModel wodModel = WodModelUtils.createWodModel(finalDocumentFile, myDocument);
        List problems = new LinkedList();
        List syntaxProblems = wodModel.getSyntacticProblems();
        problems.addAll(syntaxProblems);

        try {
          IJavaProject javaProject = JavaCore.create(finalDocumentFile.getProject());
          List semanticProblems = WodModelUtils.getSemanticProblems(javaProject, wodModel);
          problems.addAll(semanticProblems);
        }
        catch (CoreException e) {
          WodclipsePlugin.getDefault().log(e);
        }
        catch (IOException e) {
          WodclipsePlugin.getDefault().log(e);
        }

        Iterator problemsIter = problems.iterator();
        while (problemsIter.hasNext()) {
          WodProblem problem = (WodProblem) problemsIter.next();
          Position problemPosition = problem.getPosition();

          //String type = "org.eclipse.ui.workbench.texteditor.error";
          // String type = "org.eclipse.ui.workbench.texteditor.warning";
          //Annotation problemAnnotation = new Annotation(type, false, problem.getMessage());
          //Position problemPosition = currentPosition.getPosition();
          //annotationModel.addAnnotation(problemAnnotation, problemPosition);

          try {
            IMarker marker = finalDocumentFile.createMarker(IMarker.PROBLEM);
            marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
            marker.setAttribute(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
            if (problemPosition != null) {
              marker.setAttribute(IMarker.LINE_NUMBER, myDocument.getLineOfOffset(problemPosition.getOffset()));
              marker.setAttribute(IMarker.CHAR_START, problemPosition.getOffset());
              marker.setAttribute(IMarker.CHAR_END, problemPosition.getOffset() + problemPosition.getLength());
            }
            marker.setAttribute(IMarker.TRANSIENT, false);
          }
          catch (CoreException e) {
            WodclipsePlugin.getDefault().log(e);
          }
          catch (BadLocationException e) {
            WodclipsePlugin.getDefault().log(e);
          }
        }
      }
    };

    try {
      ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRuleFactory().markerRule(documentFile), IWorkspace.AVOID_UPDATE, null);
    }
    catch (CoreException e) {
      WodclipsePlugin.getDefault().log(e);
    }
  }

  public void reconcile(DirtyRegion _dirtyRegion, IRegion _subRegion) {
    reconcile();
  }

  public void reconcile(IRegion _partition) {
    reconcile();
  }

  public void initialReconcile() {
    reconcile();
  }

  public void setProgressMonitor(IProgressMonitor _monitor) {
    myProgressMonitor = _monitor;
  }

  private IAnnotationModel getAnnotationModel() {
    IEditorInput input = myTextEditor.getEditorInput();
    IAnnotationModel annotationModel = myTextEditor.getDocumentProvider().getAnnotationModel(input);
    return annotationModel;
  }
}
