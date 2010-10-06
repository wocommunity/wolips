package org.objectstyle.wolips.wodclipse.action;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;
import org.objectstyle.wolips.baseforuiplugins.utils.WorkbenchUtilities;
import org.objectstyle.wolips.locate.Locate;
import org.objectstyle.wolips.locate.result.LocalizedComponentsLocateResult;
import org.objectstyle.wolips.locate.scope.ComponentLocateScope;
import org.objectstyle.wolips.wodclipse.core.parser.ElementTypeRule;
import org.objectstyle.wolips.wodclipse.core.parser.RulePosition;
import org.objectstyle.wolips.wodclipse.core.parser.WodScanner;

public class OpenDeclarationAction extends Action implements IEditorActionDelegate {
	private IEditorPart _activeEditor;

	private ISelection _selection;

	public void run() {
		if (_selection instanceof TextSelection) {
			RulePosition rulePosition;
			try {
				TextSelection selection = (TextSelection) _selection;
				ITextEditor textEditor = (ITextEditor) _activeEditor;
				IEditorInput editorInput = textEditor.getEditorInput();
				IDocument document = textEditor.getDocumentProvider().getDocument(editorInput);

				WodScanner scanner = WodScanner.wodScannerForDocument(document);
				rulePosition = scanner.getRulePositionAtOffset(selection.getOffset());

				IProject project = null;
				if (editorInput instanceof FileEditorInput) {
					IFile file = ((FileEditorInput) editorInput).getFile();
					project = file.getProject();
				}

				IRule rule = rulePosition.getRule();
				if (rule instanceof ElementTypeRule) {
					String elementTypeName = rulePosition.getText();
					ComponentLocateScope componentLocateScope = new ComponentLocateScope(project, elementTypeName, true);
					LocalizedComponentsLocateResult localizedComponentsLocateResult = new LocalizedComponentsLocateResult();
					Locate locate = new Locate(componentLocateScope, localizedComponentsLocateResult);
					locate.locate();

					IFile wodFile = localizedComponentsLocateResult.getFirstWodFile();
					if (wodFile != null) {
						WorkbenchUtilities.open(wodFile, "org.objectstyle.wolips.componenteditor.ComponentEditor");
					}
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}

	public void runWithEvent(Event event) {
		run();
	}

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		_activeEditor = targetEditor;
	}

	public void run(IAction action) {
		run();
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_selection = selection;
	}
}