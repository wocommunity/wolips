package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;

public class WOBrowserPage extends Page {
	private IType _initialType;

	private WOBrowser _browser;

	public WOBrowserPage(IType initialType) {
		_initialType = initialType;
	}

	@Override
	public void createControl(Composite parent) {
		_browser = new WOBrowser(parent, SWT.BORDER);
		_browser.setLayoutData(new GridData(GridData.FILL_BOTH));
		try {
			_browser.setRootType(_initialType);
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
	}
	
	public WOBrowser getBrowser() {
		return _browser;
	}
	
	public void setBrowserDelegate(IWOBrowserDelegate delegate) {
		_browser.setBrowserDelegate(delegate);
	}

	@Override
	public Control getControl() {
		return _browser;
	}

	@Override
	public void setFocus() {
		_browser.setFocus();
	}
}
