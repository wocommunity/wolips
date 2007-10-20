package org.objectstyle.wolips.componenteditor.actions;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.search.SearchPattern;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.objectstyle.wolips.bindings.utils.BindingReflectionUtils;
import org.objectstyle.wolips.componenteditor.ComponenteditorPlugin;
import org.objectstyle.wolips.core.resources.types.TypeNameCollector;

public class WOElementSelectionDialog extends ElementListSelectionDialog {
	private IJavaProject _javaProject;

	private Point _location;

	private Point _size;

	private IRunnableContext _context;

	public WOElementSelectionDialog(Shell parent, IJavaProject javaProject, IRunnableContext context) {
		super(parent, new ComponentLabelProvider());
		_javaProject = javaProject;
		_context = context;
		setIgnoreCase(true);
		setMultipleSelection(false);
		setAllowDuplicates(false);
	}

	public int open() {
		try {
			final TypeNameCollector typeNameCollector = new TypeNameCollector(_javaProject, false);

			IRunnableWithProgress runnable = new IRunnableWithProgress() {
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						BindingReflectionUtils.findMatchingElementClassNames("", SearchPattern.R_PREFIX_MATCH, typeNameCollector, monitor);
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

	public static class ComponentLabelProvider implements ILabelProvider {
		private static Image _componentImage;

		public ComponentLabelProvider() {
			_componentImage = ComponenteditorPlugin.getImageDescriptor("icons/ComponentEditor.png").createImage();
		}

		public Image getImage(Object element) {
			return _componentImage;
		}

		public String getText(Object element) {
			String str = (String) element;
			int dotIndex = str.lastIndexOf('.');
			if (dotIndex != -1) {
				str = str.substring(dotIndex + 1);
			}
			return str;
		}

		public void addListener(ILabelProviderListener listener) {
			// DO NOTHING
		}

		public void dispose() {
			_componentImage.dispose();
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
			// DO NOTHING
		}

	}
}
