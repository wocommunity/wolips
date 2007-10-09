package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;

public class BindingsLabelProvider implements ITableLabelProvider, ITableColorProvider {
	private IWodElement _wodElement;

	public void setWodElement(IWodElement wodElement) {
		_wodElement = wodElement;
	}

	public IWodElement getWodElement() {
		return _wodElement;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		IApiBinding apiBinding = (IApiBinding) element;
		String text = null;
		if (columnIndex == 0) {
			text = apiBinding.getName();
		} else if (columnIndex == 1) {
			IWodBinding wodBinding = _wodElement.getBindingNamed(apiBinding.getName());
			if (wodBinding != null) {
				text = wodBinding.getValue();
			}
		}
		return text;
	}

	public void addListener(ILabelProviderListener listener) {
		// DO NOTHING
	}

	public void dispose() {
		// DO NOTHING
	}

	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	public void removeListener(ILabelProviderListener listener) {
		// DO NOTHING
	}

	public Color getBackground(Object element, int columnIndex) {
		return null;
	}

	public Color getForeground(Object element, int columnIndex) {
		Color color;
		IApiBinding apiBinding = (IApiBinding) element;
		if (apiBinding.isRequired()) {
			IWodBinding wodBinding = _wodElement.getBindingNamed(apiBinding.getName());
			if (wodBinding == null) {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
			else {
				color = null;
			}
		}
		else {
			color = null;
		}
		// IWodBinding wodBinding = (IWodBinding) element;
		// _wodElement.fillInProblems(javaProject, javaFileType, true, problems,
		// cache)
		return color;
	}
}
