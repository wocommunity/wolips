package org.objectstyle.wolips.eomodeler.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;

public class FormUtils {
	public static Composite createForm(TabbedPropertySheetWidgetFactory widgetFactory, Composite parent) {
		return FormUtils.createForm(widgetFactory, parent, 2);
	}
	
	public static Composite createForm(TabbedPropertySheetWidgetFactory widgetFactory, Composite parent, int numColumns) {
		Composite topForm = widgetFactory.createPlainComposite(parent, SWT.NONE);
		FormData topFormData = new FormData();
		topFormData.top = new FormAttachment(0, 7);
		topFormData.left = new FormAttachment(0, 13);
		topFormData.right = new FormAttachment(100, -13);
		topFormData.bottom = new FormAttachment(100, -13);
		topForm.setLayoutData(topFormData);

		GridLayout topFormLayout = new GridLayout();
		topFormLayout.numColumns = numColumns;
		topFormLayout.verticalSpacing = 10;
		topForm.setLayout(topFormLayout);
		
		return topForm;
	}
}
