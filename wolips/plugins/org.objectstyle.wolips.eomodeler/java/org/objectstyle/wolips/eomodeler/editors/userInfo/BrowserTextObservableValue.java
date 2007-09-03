package org.objectstyle.wolips.eomodeler.editors.userInfo;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.jface.internal.databinding.provisional.swt.AbstractSWTObservableValue;
import org.eclipse.swt.browser.Browser;

public class BrowserTextObservableValue extends AbstractSWTObservableValue {
	private final Browser _browser;

	private String _text;

	public BrowserTextObservableValue(Browser browser) {
		super(browser);
		_browser = browser;
	}

	public void doSetValue(final Object value) {
		String oldValue = _text;
		String newValue = value == null ? "" : value.toString();
		_browser.setText(newValue); //$NON-NLS-1$
		_text = newValue;
		fireValueChange(Diffs.createValueDiff(oldValue, newValue));
	}

	public Object doGetValue() {
		return _text;
	}

	public Object getValueType() {
		return String.class;
	}

}
