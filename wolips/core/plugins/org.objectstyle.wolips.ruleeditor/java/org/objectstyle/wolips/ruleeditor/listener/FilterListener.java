package org.objectstyle.wolips.ruleeditor.listener;

import org.eclipse.jface.viewers.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.widgets.*;
import org.objectstyle.wolips.ruleeditor.filter.*;

/**
 * @author <a href="mailto:georg@moleque.com.br">Georg von Bülow</a>
 */
public class FilterListener implements ModifyListener {

	TableViewer viewer;

	public FilterListener(final TableViewer viewer) {
		if (viewer == null) {
			throw new IllegalArgumentException("The viewer cannot be null");
		}

		this.viewer = viewer;
	}

	public void modifyText(final ModifyEvent e) {

		for (ViewerFilter filter : viewer.getFilters()) {
			viewer.removeFilter(filter);
		}

		String regex = ((Text) e.widget).getText();

		if (regex != null && !regex.equals("")) {
			viewer.addFilter(new RulesFilter(regex));
		}

	}

}
