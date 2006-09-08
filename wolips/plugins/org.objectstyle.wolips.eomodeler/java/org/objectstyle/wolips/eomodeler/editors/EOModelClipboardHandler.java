package org.objectstyle.wolips.eomodeler.editors;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.actions.ActionFactory;
import org.objectstyle.wolips.eomodeler.actions.CopyAction;
import org.objectstyle.wolips.eomodeler.actions.CutAction;
import org.objectstyle.wolips.eomodeler.actions.DeleteAction;
import org.objectstyle.wolips.eomodeler.actions.PasteAction;

public class EOModelClipboardHandler implements ISelectionChangedListener {
	private CutAction myCutAction;

	private CopyAction myCopyAction;

	private PasteAction myPasteAction;

	private DeleteAction myDeleteAction;

	public EOModelClipboardHandler() {
		Clipboard clipboard = new Clipboard(Display.getDefault());
		myCutAction = new CutAction(clipboard);
		myCopyAction = new CopyAction(clipboard);
		myPasteAction = new PasteAction(clipboard);
		myDeleteAction = new DeleteAction();
	}

	public void attach(IActionBars _actionBars, final EOModelEditor _editor) {
		if (_actionBars != null && _editor != null) {
			// actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(),
			// getAction(editor, ITextEditorActionConstants.UNDO));
			// actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(),
			// getAction(editor, ITextEditorActionConstants.REDO));

			_actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(), myDeleteAction);
			_actionBars.setGlobalActionHandler(ActionFactory.CUT.getId(), myCutAction);
			_actionBars.setGlobalActionHandler(ActionFactory.COPY.getId(), myCopyAction);
			_actionBars.setGlobalActionHandler(ActionFactory.PASTE.getId(), myPasteAction);
			_actionBars.setGlobalActionHandler(ActionFactory.REVERT.getId(), new Action() {
				public void run() {
					_editor.revert();
				}
			});
			// actionBars.setGlobalActionHandler(ActionFactory.SELECT_ALL.getId(),
			// getAction(editor, ITextEditorActionConstants.SELECT_ALL));
			// actionBars.setGlobalActionHandler(ActionFactory.FIND.getId(),
			// getAction(editor, ITextEditorActionConstants.FIND));
			// actionBars.setGlobalActionHandler(IDEActionFactory.BOOKMARK.getId(),
			// getAction(editor, IDEActionFactory.BOOKMARK.getId()));
			// actionBars.updateActionBars();
		}
	}

	public void selectionChanged(SelectionChangedEvent _event) {
		ISelection selection = _event.getSelection();
		myCutAction.selectionChanged(null, selection);
		myCopyAction.selectionChanged(null, selection);
		myPasteAction.selectionChanged(null, selection);
		myDeleteAction.selectionChanged(null, selection);
	}

}
