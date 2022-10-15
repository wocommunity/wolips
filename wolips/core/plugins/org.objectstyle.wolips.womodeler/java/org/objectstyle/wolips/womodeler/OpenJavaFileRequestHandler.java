package org.objectstyle.wolips.womodeler;

import java.util.Map;
import java.util.Objects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.objectstyle.wolips.womodeler.server.IRequestHandler;
import org.objectstyle.wolips.womodeler.server.Request;
import org.objectstyle.wolips.womodeler.server.Webserver;

/**
 * Opens a Java file in Eclipse at a given line number (intended for use with
 * WOExceptionPage)
 */

public class OpenJavaFileRequestHandler implements IRequestHandler {

	public void init(Webserver server) throws Exception {
	}

	public void handle(Request request) throws Exception {
		final Map<String, String> params = request.getQueryParameters();
		final String appName = params.get("app");
		final String className = params.get("className");
		final String lineNumber = params.get("lineNumber");

		// All these parameters are required
		Objects.requireNonNull(appName);
		Objects.requireNonNull(className);
		Objects.requireNonNull(lineNumber);

		// Let's locate a project
		final IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(appName);

		if (project != null) {
			Display.getDefault().asyncExec(new Runnable() {
				public void run() {
					IJavaProject javaProject = JavaCore.create(project);
					try {
						IType type = javaProject.findType(className);
						if (type != null) {
							IEditorPart editorPart = JavaUI.openInEditor(type, true, true);

							if (editorPart instanceof ITextEditor) {
								ITextEditor editor = (ITextEditor) editorPart;

								IDocumentProvider provider = editor.getDocumentProvider();
								IDocument document = provider.getDocument(editor.getEditorInput());

								try {
									int lineStart = document.getLineOffset(Integer.parseInt(lineNumber) - 1);
									editor.selectAndReveal(lineStart, 0);
								} catch (BadLocationException x) {
									// We're going to a non-existent line. Not likely as is so do nothing.
								}
							}

						}
					} catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
		}
		request.getWriter().println("ok");
	}
}