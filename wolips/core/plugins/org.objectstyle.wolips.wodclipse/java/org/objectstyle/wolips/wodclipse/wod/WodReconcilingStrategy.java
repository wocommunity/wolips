package org.objectstyle.wolips.wodclipse.wod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import org.objectstyle.wolips.locate.LocateException;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.wod.model.IWodModel;
import org.objectstyle.wolips.wodclipse.wod.model.WodModelUtils;
import org.objectstyle.wolips.wodclipse.wod.model.WodProblem;

public class WodReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	private WodEditor _wodEditor;

	private IProgressMonitor _progressMonitor;

	private IDocument _document;

	public WodReconcilingStrategy(WodEditor wodEditor) {
		_wodEditor = wodEditor;
	}

	public void setDocument(IDocument document) {
		_document = document;
	}

	public void reconcile() {
		IEditorInput input = _wodEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			final IFile wodFile = ((IFileEditorInput) input).getFile();
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor _monitor) throws CoreException {
					try {
						WodReconcilingStrategy.reconcileWodModel(_document, _wodEditor.getComponentsLocateResults(), new HashMap(), new HashMap(), new HashMap());
					} catch (LocateException e) {
						WodclipsePlugin.getDefault().log(e);
					}
				}
			};

			try {
				ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRuleFactory().markerRule(wodFile), IWorkspace.AVOID_UPDATE, null);
			} catch (CoreException e) {
				WodclipsePlugin.getDefault().log(e);
			}
		}
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		reconcile();
	}

	public void reconcile(IRegion partition) {
		reconcile();
	}

	public void initialReconcile() {
		reconcile();
	}

	public void setProgressMonitor(IProgressMonitor monitor) {
		_progressMonitor = monitor;
	}

	private IAnnotationModel getAnnotationModel() {
		IEditorInput input = _wodEditor.getEditorInput();
		IAnnotationModel annotationModel = _wodEditor.getDocumentProvider().getAnnotationModel(input);
		return annotationModel;
	}

	public static synchronized void deleteWodProblems(IFile wodFile) {
		try {
			IMarker[] oldMarkers = wodFile.findMarkers(IMarker.PROBLEM, true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < oldMarkers.length; i++) {
				oldMarkers[i].delete();
			}
			IMarker[] newMarkers = wodFile.findMarkers(WodclipsePlugin.WOD_PROBLEM, true, IResource.DEPTH_INFINITE);
			for (int i = 0; i < newMarkers.length; i++) {
				newMarkers[i].delete();
			}
		} catch (CoreException e) {
			WodclipsePlugin.getDefault().debug(e);
		}
	}

	public static synchronized void reconcileWodModel(IDocument wodDocument, LocalizedComponentsLocateResult locateResult, Map elementNameToTypeCache, Map typeToApiModelWoCache, Map typeContextCache) throws CoreException {
		IFile wodFile = locateResult.getFirstWodFile();
		WodReconcilingStrategy.deleteWodProblems(wodFile);

		IWodModel wodModel = WodModelUtils.createWodModel(wodFile, wodDocument);
		List problems = new LinkedList();
		List syntaxProblems = wodModel.getSyntacticProblems();
		problems.addAll(syntaxProblems);

		try {
			IJavaProject javaProject = JavaCore.create(wodFile.getProject());
			List semanticProblems = WodModelUtils.getSemanticProblems(wodModel, locateResult, javaProject, elementNameToTypeCache, typeToApiModelWoCache, typeContextCache);
			problems.addAll(semanticProblems);
		} catch (CoreException e) {
			WodclipsePlugin.getDefault().log(e);
		}

		Iterator problemsIter = problems.iterator();
		while (problemsIter.hasNext()) {
			WodProblem problem = (WodProblem) problemsIter.next();
			Position problemPosition = problem.getPosition();

			// String type = "org.eclipse.ui.workbench.texteditor.error";
			// String type = "org.eclipse.ui.workbench.texteditor.warning";
			// Annotation problemAnnotation = new Annotation(type, false,
			// problem.getMessage());
			// Position problemPosition = currentPosition.getPosition();
			// annotationModel.addAnnotation(problemAnnotation,
			// problemPosition);

			try {
				IMarker marker = wodFile.createMarker(WodclipsePlugin.WOD_PROBLEM);
				marker.setAttribute(IMarker.MESSAGE, problem.getMessage());
				int severity;
				if (problem.isWarning()) {
					severity = IMarker.SEVERITY_WARNING;
				} else {
					severity = IMarker.SEVERITY_ERROR;
				}
				marker.setAttribute(IMarker.SEVERITY, new Integer(severity));
				if (problemPosition != null) {
					marker.setAttribute(IMarker.LINE_NUMBER, wodDocument.getLineOfOffset(problemPosition.getOffset()));
					marker.setAttribute(IMarker.CHAR_START, problemPosition.getOffset());
					marker.setAttribute(IMarker.CHAR_END, problemPosition.getOffset() + problemPosition.getLength());
				}
				marker.setAttribute(IMarker.TRANSIENT, false);
				String[] relatedToFileNames = problem.getRelatedToFileNames();
				if (relatedToFileNames != null) {
					StringBuffer relatedToFileNamesBuffer = new StringBuffer();
					for (int i = 0; i < relatedToFileNames.length; i++) {
						relatedToFileNamesBuffer.append(relatedToFileNames[i]);
						relatedToFileNamesBuffer.append(", ");
					}
					marker.setAttribute(WodProblem.RELATED_TO_FILE_NAMES, relatedToFileNamesBuffer.toString());
				}
			} catch (CoreException e) {
				WodclipsePlugin.getDefault().log(e);
			} catch (BadLocationException e) {
				WodclipsePlugin.getDefault().log(e);
			}
		}
	}
}
