package org.objectstyle.wolips.componenteditor.inspector;

import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableColorProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.objectstyle.wolips.baseforplugins.util.ComparisonUtils;
import org.objectstyle.wolips.bindings.api.IApiBinding;
import org.objectstyle.wolips.bindings.wod.ApiBindingValidationProblem;
import org.objectstyle.wolips.bindings.wod.ApiElementValidationProblem;
import org.objectstyle.wolips.bindings.wod.IWodBinding;
import org.objectstyle.wolips.bindings.wod.IWodElement;
import org.objectstyle.wolips.bindings.wod.WodBindingProblem;
import org.objectstyle.wolips.bindings.wod.WodProblem;

public class BindingsLabelProvider extends ColumnLabelProvider implements ITableLabelProvider, ITableColorProvider, ITableFontProvider {
	private IWodElement _wodElement;

	private List<WodProblem> _problems;

	public void setContext(IWodElement wodElement, List<WodProblem> problems) {
		_wodElement = wodElement;
		_problems = problems;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getText(Object element) {
		return getColumnText(element, 1);
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

	@Override
	public Color getForeground(Object element) {
		return getForeground(element, 1);
	}

	public Color getForeground(Object element, int columnIndex) {
		Color color = null;
		IApiBinding apiBinding = (IApiBinding) element;
		if (_problems != null) {
			String bindingName = apiBinding.getName();
			boolean hasValidationProblem = false;
			for (WodProblem problem : _problems) {
				if (problem instanceof ApiBindingValidationProblem) {
					ApiBindingValidationProblem validationProblem = (ApiBindingValidationProblem) problem;
					hasValidationProblem = validationProblem.getBindingName().equals(bindingName);
				} else if (problem instanceof ApiElementValidationProblem) {
					ApiElementValidationProblem validationProblem = (ApiElementValidationProblem) problem;
					hasValidationProblem = validationProblem.getValidation().isAffectedByBindingNamed(bindingName);
				} else if (problem instanceof WodBindingProblem) {
					WodBindingProblem validationProblem = (WodBindingProblem) problem;
					hasValidationProblem = ComparisonUtils.equals(bindingName, validationProblem.getBindingName());
				}
				if (hasValidationProblem) {
					break;
				}
			}
			if (hasValidationProblem) {
				color = Display.getCurrent().getSystemColor(SWT.COLOR_RED);
			}
		}
		return color;
	}

	@Override
	public Font getFont(Object element) {
		return getFont(element, 1);
	}

	public Font getFont(Object element, int columnIndex) {
		Font font = null;
		if (columnIndex == 0) {
			if (element instanceof IWodBinding) {
				font = JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT);
			}
		}
		return font;
	}
}
