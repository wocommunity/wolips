package org.objectstyle.wolips.componenteditor.inspector;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableFontProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Control;
import org.objectstyle.wolips.bindings.wod.BindingValueKey;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;

public class WOBrowserColumnLabelProvider extends BaseLabelProvider implements ILabelProvider, ITableLabelProvider, ITableFontProvider {
	private IType _type;
	
	private Control _control;

	private Font _titleFont;

	public WOBrowserColumnLabelProvider(IType type, Control control) {
		_type = type;
		_control = control;
	}

	protected Font getTitleFont() {
		if (_titleFont == null) {
			Font originalFont = _control.getFont();
			FontData[] fontData = _control.getFont().getFontData();
			_titleFont = new Font(originalFont.getDevice(), fontData[0].getName(), fontData[0].getHeight(), SWT.BOLD);
		}
		return _titleFont;
	}

	public Image getImage(Object element) {
		return getColumnImage(element, 0);
	}

	@Override
	public void dispose() {
		if (_titleFont != null) {
			_titleFont.dispose();
			_titleFont = null;
		}
		super.dispose();
	}

	public String getText(Object element) {
		return getColumnText(element, 0);
	}

	public Image getColumnImage(Object element, int columnIndex) {
		Image image = null;
		if (columnIndex == 0) {
			if (element instanceof String) {
				// image = ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.COMPONENT_ICON);
			}
		}
		else if (columnIndex == 1) {
			if (element instanceof BindingValueKey) {
				BindingValueKey bindingValueKey = (BindingValueKey) element;
				if (bindingValueKey != null) {
					try {
						if (!bindingValueKey.isLeaf()) {
							image = ComponenteditorPlugin.getDefault().getImage(ComponenteditorPlugin.TO_ONE_ICON);
						}
					} catch (JavaModelException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return image;
	}

	public String getColumnText(Object element, int columnIndex) {
		String text = null;
		if (columnIndex == 0) {
			if (element instanceof BindingValueKey) {
				BindingValueKey bindingValueKey = (BindingValueKey) element;
				if (bindingValueKey != null) {
					StringBuffer nameBuffer = new StringBuffer();
					IType declaringType = bindingValueKey.getDeclaringType();
					if (declaringType != null && _type != null && !_type.equals(declaringType)) {
						nameBuffer.append("    ");
					}
					nameBuffer.append(bindingValueKey.getBindingName());
					int minWidth = 40;
					if (text != null && text.length() < minWidth) {
						for (int i = text.length(); i < minWidth; i++) {
							nameBuffer.append(' ');
						}
					}
					text = nameBuffer.toString();
				}
			} else if (element instanceof String) {
				text = (String) element;
			}
			if (text == null) {
				text = "<unknown>";
			}
		} else if (columnIndex == 1) {
			text = null;
		}
		return text;
	}

	public Font getFont(Object element, int columnIndex) {
		Font font = null;
		if (element instanceof String) {
			font = getTitleFont();
		}
		return font;
	}
}