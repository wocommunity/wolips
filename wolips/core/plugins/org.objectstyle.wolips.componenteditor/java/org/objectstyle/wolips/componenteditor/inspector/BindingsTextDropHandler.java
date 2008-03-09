package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.MenuEvent;
import org.eclipse.swt.events.MenuListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Shell;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.document.ITextWOEditor;

public class BindingsTextDropHandler extends AbstractBindingsDropHandler<IWodElement, IRegion, Annotation, StyledText> implements MenuListener {
	private ITextWOEditor _woEditor;

	private BindingsPopUpMenu _bindingsMenu;

	public BindingsTextDropHandler(ITextWOEditor woEditor) {
		super(woEditor.getWOEditorControl());

		_woEditor = woEditor;
		
		Shell shell = new Shell(woEditor.getWOEditorControl().getShell());
		try {
			_bindingsMenu = new BindingsPopUpMenu(shell, woEditor.getParserCache());
			_bindingsMenu.getMenu().addMenuListener(this);
		} catch (Exception e) {
			ComponenteditorPlugin.getDefault().log(e);
		}
	}

	public void menuHidden(MenuEvent event) {
		removeHoverAnnotation();
	}

	public void menuShown(MenuEvent event) {
		// DO NOTHING
	}

	public void dispose() {
		if (_bindingsMenu != null) {
			_bindingsMenu.dispose();
		}
		super.dispose();
	}

	@Override
	protected Annotation _addHoverAnnotation(IRegion selectedItem) {
		Annotation annotation = new Annotation(TemplateEditor.BINDING_HOVER_ANNOTATION, false, null);
		_woEditor.getWOSourceViewer().getAnnotationModel().addAnnotation(annotation, new Position(selectedItem.getOffset(), selectedItem.getLength()));
		return annotation;
	}

	@Override
	protected void _removeHoverAnnotation(Annotation annotation) {
		if (annotation != null) {
			_woEditor.getWOSourceViewer().getAnnotationModel().removeAnnotation(annotation);
		}
	}

	@Override
	protected IAutoscroller createAutoscroller(StyledText editorControl) {
		return new TextAutoscroller(editorControl);
	}

	@Override
	protected IWodElement getSelectedContainerAtPoint(Point point, boolean forDrop) throws Exception {
		IWodElement wodElement = _woEditor.getWodElementAtPoint(point, forDrop, true);
		return wodElement;
	}

	@Override
	protected IRegion getSelectedItemAtPoint(IWodElement container, Point point) {
		IRegion selectionRegion = new Region(container.getStartOffset(), container.getFullEndOffset() - container.getStartOffset());
		return selectionRegion;
	}

	@Override
	protected Rectangle getSelectionRectangle(IRegion item) {
		Rectangle selectionRect = getEditorControl().getTextBounds(item.getOffset(), item.getOffset() + item.getLength() - 1);
		return selectionRect;
	}

	@Override
	protected boolean isSelectedItemChanged(IRegion oldItem, IRegion newItem) {
		return oldItem == null || oldItem.getOffset() != newItem.getOffset();
	}

	@Override
	protected void dropFromColumnAtPoint(WOBrowserColumn column, Point dropPoint) throws Exception {
		Point controlDropPoint = getEditorControl().toControl(dropPoint);
		IWodElement wodElement = _woEditor.getWodElementAtPoint(controlDropPoint, true, true);
		if (wodElement == null) {
			removeHoverAnnotation();
		} else {
			String droppedKeyPath = column.getSelectedKeyPath();
			if (_bindingsMenu != null) {
				boolean menuShown = _bindingsMenu.showMenuAtLocation(wodElement, droppedKeyPath, dropPoint);
				if (!menuShown) {
					removeHoverAnnotation();
				}
			}
		}
	}
}
