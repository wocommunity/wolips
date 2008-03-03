package org.objectstyle.wolips.componenteditor.part;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.IRewriteTarget;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetAdapter;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.graphics.Point;
import org.objectstyle.wolips.bindings.api.Wo;
import org.objectstyle.wolips.componenteditor.actions.ComponentInserter;
import org.objectstyle.wolips.templateeditor.TemplateEditor;

public class ComponentDropTargetAdaptor extends DropTargetAdapter {
	private ComponentEditor _componentEditor;

	private IRegion _selection;

	public ComponentDropTargetAdaptor(ComponentEditor componentEditor) {
		_componentEditor = componentEditor;
	}

	public StyledText getStyledText() {
		return _componentEditor.getTemplateEditor().getSourceEditor().getViewer().getTextWidget();
	}

	public TemplateEditor getTemplateEditor() {
		return _componentEditor.getTemplateEditor();
	}

	@Override
	public void dragEnter(DropTargetEvent event) {
		try {
			getTemplateEditor().getSourceEditor().getParserCache().getHtmlEntry().clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		Point selectionRange = getStyledText().getSelectionRange();
		if (selectionRange == null) {
			_selection = null;
		} else {
			_selection = new Region(selectionRange.x, selectionRange.y);
		}

		if (event.detail == DND.DROP_DEFAULT) {
			event.detail = DND.DROP_MOVE;
		}
	}

	@Override
	public void dragOperationChanged(DropTargetEvent event) {
		if (event.detail == DND.DROP_DEFAULT) {
			event.detail = DND.DROP_MOVE;
		}
	}

	@Override
	public void dragOver(DropTargetEvent event) {
		try {
			event.feedback |= DND.FEEDBACK_SCROLL;

			_selection = getTemplateEditor().getSourceEditor().getSelectionRegionAtPoint(new Point(event.x, event.y), true);
			if (_selection != null) {
				getTemplateEditor().getSourceEditor().getViewer().setSelectedRange(_selection.getOffset(), _selection.getLength());
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
	}

	@Override
	public void drop(DropTargetEvent event) {
		ComponentInserter inserter = null;
		boolean selectRange = false;

		IStructuredSelection structuredSelection = (IStructuredSelection) event.data;
		if (structuredSelection != null && !structuredSelection.isEmpty()) {
			IResource resource = (IResource) structuredSelection.getFirstElement();
			if (resource instanceof IFolder) {
				IFolder folder = (IFolder) resource;
				String name = folder.getName();
				if (name.endsWith(".wo")) {
					String componentName = name.substring(0, name.lastIndexOf('.'));
					boolean inline = true;
					inserter = new ComponentInserter(_componentEditor, componentName, inline);
					Wo wo = inserter.getWo();
					if (wo != null) {
						selectRange = wo.isComponentContent();
					}
				}
			}
		}

		if (inserter == null) {
			event.detail = DND.DROP_NONE;
			return;
		}

		IRewriteTarget target = (IRewriteTarget) _componentEditor.getAdapter(IRewriteTarget.class);
		if (target != null) {
			target.beginCompoundChange();
		}

		if (!getTemplateEditor().getSourceEditor().validateEditorInputState()) {
			event.detail = DND.DROP_NONE;
			return;
		}

		if (selectRange) {
			getTemplateEditor().getSelectionProvider().setSelection(new TextSelection(_selection.getOffset(), _selection.getLength()));
		} else {
			getTemplateEditor().getSelectionProvider().setSelection(new TextSelection(_selection.getOffset(), 0));
		}

		inserter.insert();
	}
}