package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.objectstyle.wolips.eomodeler.actions.CopyAction;
import org.objectstyle.wolips.eomodeler.actions.CutAction;
import org.objectstyle.wolips.eomodeler.actions.DeleteAction;
import org.objectstyle.wolips.eomodeler.actions.PasteAction;

public class EOModelClipboardHandler implements ISelectionChangedListener {
	private CutAction _cutAction;

	private CopyAction _copyAction;

	private PasteAction _pasteAction;

	private DeleteAction _deleteAction;

	public EOModelClipboardHandler() {
		Clipboard clipboard = new Clipboard(Display.getDefault());
		_cutAction = new CutAction(clipboard);
		_copyAction = new CopyAction(clipboard);
		_pasteAction = new PasteAction(clipboard);
		_deleteAction = new DeleteAction();
	}

	public void attach(IActionBars actionBars, final EOModelEditor editor) {
		if (actionBars != null && editor != null) {
			// actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
			// getAction(editor, ITextEditorActionConstants.UNDO));
			// actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
			// getAction(editor, ITextEditorActionConstants.REDO));

			actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), _deleteAction);
			actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), _cutAction);
			actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), _copyAction);
			actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), _pasteAction);
			actionBars.setGlobalActionHandler(ActionFactory.REVERT.getId(), new Action() {
				public void run() {
					editor.revert();
				}
			});
			actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), new UndoActionHandler(editor.getSite(), editor.getUndoContext()));
			actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), new RedoActionHandler(editor.getSite(), editor.getUndoContext()));
			// actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
			// getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			// actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
			// getAction(editor, ITextEditorActionConstants.FIND));
			// actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(),
			// getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			// actionBars.updateActionBars();
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		_cutAction.selectionChanged(null, selection);
		_copyAction.selectionChanged(null, selection);
		_pasteAction.selectionChanged(null, selection);
		_deleteAction.selectionChanged(null, selection);
	}

}
