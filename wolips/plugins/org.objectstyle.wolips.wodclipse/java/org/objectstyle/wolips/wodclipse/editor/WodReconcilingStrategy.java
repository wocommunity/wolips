package org.objectstyle.wolips.wodclipse.editor;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.objectstyle.wolips.wodclipse.WodclipsePlugin;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.WodModelUtils;

public class WodReconcilingStrategy implements IReconcilingStrategy, IReconcilingStrategyExtension {
	private WodEditor _wodEditor;

	private IProgressMonitor _progressMonitor;

	private IDocument _document;

	private WodParserCache _cache;

	public WodReconcilingStrategy(WodEditor wodEditor) {
		_wodEditor = wodEditor;
	}

	public void setDocument(IDocument document) {
		_document = document;
	}

	public void reconcile() {
		IEditorInput input = _wodEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
				public void run(IProgressMonitor _monitor) throws CoreException {
					try {
						WodModelUtils.validateWodDocument(_document, _wodEditor.getComponentsLocateResults(), WodParserCache.getTypeCache(), _cache.getHtmlEntry().getHtmlElementCache());
					} catch (Exception e) {
						WodclipsePlugin.getDefault().log(e);
					}
				}
			};

			try {
				IFile wodFile = ((IFileEditorInput) input).getFile();
				ResourcesPlugin.getWorkspace().run(runnable, ResourcesPlugin.getWorkspace().getRuleFactory().markerRule(wodFile), IWorkspace.AVOID_UPDATE, _progressMonitor);
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
}
