package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.IType;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.objectstyle.wolips.componenteditor.part.ComponentEditor;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;

public class WOBrowserPageBookView extends PageBookView {
	private ComponentEditor _componentEditor;

	public WOBrowserPageBookView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		WOBrowserPage page = new WOBrowserPage(null);
		initPage(page);
		page.createControl(book);
		return page;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		PageRec pageRec = null;
		if (part instanceof ComponentEditor) {
			_componentEditor = (ComponentEditor) part;
			try {
				WodParserCache cache = _componentEditor.getParserCache();
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
			_componentEditor = null;
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
