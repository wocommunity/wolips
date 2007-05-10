package org.objectstyle.wolips.componenteditor.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.objectstyle.wolips.wodclipse.core.completion.WodParserCache;
import org.objectstyle.wolips.wodclipse.core.util.TypeNameCollector;

public class WOElementSelectionDialog extends ElementListSelectionDialog {
	private WodParserCache _cache;

	private Point _location;

	private Point _size;

	private IRunnableContext _context;

	public WOElementSelectionDialog(Shell parent, WodParserCache cache, IRunnableContext context) {
		super(parent, new LabelProvider());
		_cache = cache;
		_context = context;
		setIgnoreCase(true);
		setMultipleSelection(false);
	}

	public int open() {
		try {
			final TypeNameCollector typeNameCollector = new TypeNameCollector(JavaCore.create(_cache.getProject()), false);

			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
					    SearchEngine searchEngine = new SearchEngine();
					    IJavaSearchScope searchScope = SearchEngine.createWorkspaceScope();
					    searchEngine.searchAllTypeNames(null, null, SearchPattern.R_PREFIX_MATCH, IJavaSearchConstants.CLASS, searchScope, typeNameCollector, IJavaSearchConstants.WAIT_UNTIL_READY_TO_SEARCH, monitor);
						System.out.println(".run: " + typeNameCollector.getTypeNames());
					} catch (Exception e) {
						e.printStackTrace();
						throw new InvocationTargetException(e);
					}
					if (monitor.isCanceled()) {
						throw new InterruptedException();
					}
				}
			};

			_context.run(true, true, runnable);

			if (typeNameCollector.isEmpty()) {
				String title = "None";
				String message = "None";
				MessageDialog.openInformation(getShell(), title, message);
				return CANCEL;
			}

			setElements(typeNameCollector.getTypeNames().toArray());
		} catch (Exception e) {
			return CANCEL;
		}

		return super.open();
	}

	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		// PlatformUI.getWorkbench().getHelpSystem().setHelp(newShell,
		// IJavaHelpContextIds.OPEN_PACKAGE_DIALOG);
	}

	public boolean close() {
		return super.close();
	}

	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		return control;
	}

	protected Point getInitialSize() {
		Point result = super.getInitialSize();
		if (_size != null) {
			result.x = Math.max(result.x, _size.x);
			result.y = Math.max(result.y, _size.y);
			Rectangle display = getShell().getDisplay().getClientArea();
			result.x = Math.min(result.x, display.width);
			result.y = Math.min(result.y, display.height);
		}
		return result;
	}

	protected Point getInitialLocation(Point initialSize) {
		Point result = super.getInitialLocation(initialSize);
		if (_location != null) {
			result.x = _location.x;
			result.y = _location.y;
			Rectangle display = getShell().getDisplay().getClientArea();
			int xe = result.x + initialSize.x;
			if (xe > display.width) {
				result.x -= xe - display.width;
			}
			int ye = result.y + initialSize.y;
			if (ye > display.height) {
				result.y -= ye - display.height;
			}
		}
		return result;
	}
}
