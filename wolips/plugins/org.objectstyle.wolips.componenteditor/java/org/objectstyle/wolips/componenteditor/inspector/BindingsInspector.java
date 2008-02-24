package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.templateeditor.TemplateEditor;
import org.objectstyle.wolips.wodclipse.core.util.ICursorPositionListener;
import org.objectstyle.wolips.wodclipse.editor.WodEditor;

public class BindingsInspector extends PageBookView implements ICursorPositionListener {
	public BindingsInspector() {
		super();
	}

	protected IPage createDefaultPage(PageBook book) {
		BindingsInspectorPage page = new BindingsInspectorPage();
		initPage(page);
		page.createControl(book);
		return page;
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		// getSite().getPage().getWorkbenchWindow().getWorkbench().getHelpSystem().setHelp(getPageBook(),
		// IPropertiesHelpContextIds.PROPERTY_SHEET_VIEW);
	}

	public void dispose() {
		super.dispose();
		// getSite().getPage().removeSelectionListener(this);
	}

	protected PageRec doCreatePage(IWorkbenchPart part) {
		BindingsInspectorPage page = new BindingsInspectorPage();
		initPage(page);
		page.createControl(getPageBook());
		return new PageRec(part, page);
	}

	protected void doDestroyPage(IWorkbenchPart part, PageRec rec) {
		BindingsInspectorPage page = (BindingsInspectorPage) rec.page;
		page.dispose();
		rec.dispose();
	}

	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			// bootstrapSelection = page.getSelection();
			return page.getActivePart();
		}
		return null;
	}

	public void init(IViewSite site) throws PartInitException {
		// site.getPage().addSelectionListener(this);
		super.init(site);
	}

	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof ComponentEditor;
	}
	
	@Override
	public void partDeactivated(IWorkbenchPart part) {
		super.partDeactivated(part);
		if (part instanceof ComponentEditor) {
			ComponentEditor componentEditor = (ComponentEditor)part;
			TemplateEditor templateEditor = componentEditor.getTemplateEditor();
			templateEditor.getSourceEditor().removeCursorPositionListener(this);
			WodEditor wodEditor = componentEditor.getWodEditor();
			wodEditor.removeCursorPositionListener(this);
		}
	}

	public void partActivated(IWorkbenchPart part) {
		super.partActivated(part);
		if (part instanceof ComponentEditor) {
			ComponentEditor componentEditor = (ComponentEditor)part;
			TemplateEditor templateEditor = componentEditor.getTemplateEditor();
			templateEditor.getSourceEditor().addCursorPositionListener(this);
			WodEditor wodEditor = componentEditor.getWodEditor();
			wodEditor.addCursorPositionListener(this);
		}

		// When the view is first opened, pass the selection to the page
		// if (bootstrapSelection != null) {
		// BindingsInspectorPage page = (BindingsInspectorPage)
		// getCurrentPage();
		// if (page != null) {
		// page.selectionChanged(part, bootstrapSelection);
		// }
		// bootstrapSelection = null;
		// }
	}

	public void cursorPositionChanged(TextEditor editor, Point selectionRange) {
		// pass the selection to the page
		BindingsInspectorPage page = (BindingsInspectorPage) getCurrentPage();
		if (page != null) {
			page.cursorPositionChanged(editor, selectionRange);
		}
	}

	protected Object getViewAdapter(Class key) {
		if (ISaveablePart.class.equals(key)) {
			return getSaveablePart();
		}
		return super.getViewAdapter(key);
	}

	protected ISaveablePart getSaveablePart() {
		IWorkbenchPart part = getCurrentContributingPart();
		if (part instanceof ISaveablePart) {
			return (ISaveablePart) part;
		}
		return null;
	}
}
