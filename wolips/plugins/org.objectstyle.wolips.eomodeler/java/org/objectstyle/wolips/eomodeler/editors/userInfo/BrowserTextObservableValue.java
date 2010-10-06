package org.objectstyle.wolips.eomodeler.editors.userInfo;

import org.eclipse.core.databinding.observable.Diffs;
import org.eclipse.core.databinding.observable.value.AbstractObservableValue;
import org.eclipse.swt.browser.Browser;

public class BrowserTextObservableValue extends AbstractObservableValue {
	private final Browser _browser;

	private String _text;
	private String _defaultStyle;

	public BrowserTextObservableValue(Browser browser, String defaultStyle) {
		_browser = browser;
		_defaultStyle = defaultStyle;
	}

	public void doSetValue(final Object value) {
		String oldValue = _text;
		String newValue = value == null ? "" : value.toString();
		if (_defaultStyle != null) {
			newValue += "<style>" + _defaultStyle + "</style>";
		}
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
