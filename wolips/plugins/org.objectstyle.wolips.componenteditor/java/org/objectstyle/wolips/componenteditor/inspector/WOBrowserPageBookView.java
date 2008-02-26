package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WOBrowserPageBookView extends PageBookView {
	public WOBrowserPageBookView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		System.out.println("WOBrowserPageBookView.createDefaultPage: " + book);
		WOBrowserPage page = new WOBrowserPage(null);
		initPage(page);
		page.createControl(book);
		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		System.out.println("WOBrowserPageBookView.doCreatePage: " + part);
		PageRec pageRec = null;
		if (part instanceof ComponentEditor) {
			ComponentEditor editor = (ComponentEditor) part;
			try {
				WodParserCache cache = editor.getParserCache();
				IType componentType = cache.getComponentType();
				WOBrowserPage page = new WOBrowserPage(componentType);
				initPage(page);
				page.createControl(getPageBook());
				pageRec = new PageRec(part, page);
			} catch (Exception e) {
				e.printStackTrace();
				pageRec = null;
			}
		} else {
			pageRec = null;
		}
		return pageRec;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		System.out.println("WOBrowserPageBookView.doDestroyPage: " + part);
		if (pageRecord != null) {
			WOBrowserPage page = (WOBrowserPage) pageRecord.page;
			page.dispose();
			pageRecord.dispose();
		}
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		System.out.println("WOBrowserPageBookView.isImportant: " + part);
		return part instanceof ComponentEditor;
	}
}
