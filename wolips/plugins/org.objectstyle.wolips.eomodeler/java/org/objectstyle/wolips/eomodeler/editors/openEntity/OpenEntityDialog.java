package org.objectstyle.wolips.eomodeler.editors.openEntity;

import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.util.Util;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenWithMenu;
import org.eclipse.ui.dialogs.FilteredResourcesSelectionDialog;
import org.eclipse.ui.internal.IWorkbenchGraphicConstants;
import org.eclipse.ui.internal.WorkbenchImages;
import org.eclipse.ui.internal.ide.IDEWorkbenchMessages;
import org.eclipse.ui.internal.ide.IIDEHelpContextIds;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.objectstyle.wolips.eomodeler.Activator;

/**
 * Shows a list of resources to the user with a text entry field for a string
 * pattern used to filter the list of resources.
 * 
 * @since 2.1
 */
public class OpenEntityDialog extends FilteredResourcesSelectionDialog {

	private Button openWithButton;

	/**
	 * Creates a new instance of the class.
	 * 
	 * @param parentShell
	 *            the parent shell
	 * @param container
	 *            the container
	 * @param typesMask
	 *            the types mask
	 */
	public OpenEntityDialog(Shell parentShell, IContainer container, boolean showSelectionHistory) {
		super(parentShell, true, container, IResource.FILE | IResource.FOLDER);
		setTitle("Open Model");
		PlatformUI.getWorkbench().getHelpSystem().setHelp(parentShell, IIDEHelpContextIds.OPEN_RESOURCE_DIALOG);

		setListLabelProvider(new EntityLabelProvider());
		setDetailsLabelProvider(new EntityDetailsLabelProvider());
		if (!showSelectionHistory) {
			setSelectionHistory(null);
		}
	}

	@Override
	protected ItemsFilter createFilter() {
		return new EntityFilter(ResourcesPlugin.getWorkspace().getRoot(), false, IResource.FILE | IResource.FOLDER);
	}

	protected class EntityFilter extends ResourceFilter {
		public EntityFilter(IContainer container, boolean showDerived, int typeMask) {
			super(container, showDerived, typeMask);
		}

		@Override
		public boolean matchItem(Object item) {
			boolean matches = super.matchItem(item);
			if (matches) {
				matches = false;
				IResource resource = (IResource) item;
				if ("plist".equals(resource.getFileExtension()) || "storedProcedure".equals(resource.getFileExtension())) {
					if ("eomodeld".equals(resource.getParent().getFileExtension())) {
						matches = true;
					}
				}
				else if ("eomodeld".equals(resource.getFileExtension()) && resource instanceof IContainer) {
					matches = true;
				}
			}
			return matches;
		}
	}

	protected void fillContextMenu(IMenuManager menuManager) {
		super.fillContextMenu(menuManager);

		IStructuredSelection selectedItems = getSelectedItems();
		if (selectedItems.isEmpty()) {
			return;
		}

		IWorkbenchPage activePage = getActivePage();
		if (activePage == null) {
			return;
		}

		// Add 'Open' menu item
		OpenFileAction openFileAction = new OpenFileAction(activePage) {
			@SuppressWarnings("synthetic-access")
			public void run() {
				okPressed();
			}
		};
		openFileAction.selectionChanged(selectedItems);
		if (!openFileAction.isEnabled()) {
			return;
		}
		menuManager.add(new Separator());
		menuManager.add(openFileAction);

		IAdaptable selectedAdaptable = getSelectedAdaptable();
		if (selectedAdaptable == null) {
			return;
		}

		// Add 'Open With...' sub-menu
		MenuManager subMenu = new MenuManager(IDEWorkbenchMessages.OpenResourceDialog_openWithMenu_label);
		OpenWithMenu openWithMenu = new OpenWithMenu(activePage, selectedAdaptable) {
			@SuppressWarnings("synthetic-access")
			protected void openEditor(IEditorDescriptor editorDescriptor, boolean openUsingDescriptor) {
				computeResult();
				setResult(Collections.EMPTY_LIST);
				close();
				super.openEditor(editorDescriptor, openUsingDescriptor);
			}
		};
		subMenu.add(openWithMenu);
		menuManager.add(subMenu);
	}

	protected void createButtonsForButtonBar(final Composite parent) {
		// increment the number of columns in the button bar
		GridLayout parentLayout = (GridLayout) parent.getLayout();
		parentLayout.numColumns++;
		parentLayout.makeColumnsEqualWidth = false;

		final Composite openComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = 0;
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		openComposite.setLayout(layout);

		Button okButton = createButton(openComposite, IDialogConstants.OK_ID, IDEWorkbenchMessages.OpenResourceDialog_openButton_text, true);

		// Arrow down button for Open With menu
		((GridLayout) openComposite.getLayout()).numColumns++;
		openWithButton = new Button(openComposite, SWT.PUSH);
		openWithButton.setToolTipText(IDEWorkbenchMessages.OpenResourceDialog_openWithButton_toolTip);
		openWithButton.setImage(WorkbenchImages.getImage(IWorkbenchGraphicConstants.IMG_LCL_BUTTON_MENU));

		GridData data = new GridData(SWT.CENTER, SWT.FILL, false, true);
		openWithButton.setLayoutData(data);

		openWithButton.addMouseListener(new MouseAdapter() {
			@SuppressWarnings("synthetic-access")
			public void mouseDown(MouseEvent e) {
				showOpenWithMenu(openComposite);
			}
		});
		openWithButton.addSelectionListener(new SelectionAdapter() {
			@SuppressWarnings("synthetic-access")
			public void widgetSelected(SelectionEvent e) {
				showOpenWithMenu(openComposite);
			}
		});

		Button cancelButton = createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);

		GridData cancelLayoutData = (GridData) cancelButton.getLayoutData();
		GridData okLayoutData = (GridData) okButton.getLayoutData();
		int buttonWidth = Math.max(cancelLayoutData.widthHint, okLayoutData.widthHint);
		cancelLayoutData.widthHint = buttonWidth;
		okLayoutData.widthHint = buttonWidth;

		if (openComposite.getDisplay().getDismissalAlignment() == SWT.RIGHT) {
			// Make the default button the right-most button.
			// See also special code in
			// org.eclipse.jface.dialogs.Dialog#initializeBounds()
			openComposite.moveBelow(null);
			if (Util.isCarbon()) {
				okLayoutData.horizontalIndent = -10;
			}
		}
	}

	protected void initializeBounds() {
		super.initializeBounds();
		if (openWithButton.getDisplay().getDismissalAlignment() == SWT.RIGHT) {
			// Move the menu button back to the right of the default button.
			if (!Util.isMac()) {
				// On the Mac, the round buttons and the big padding would
				// destroy the visual coherence of the split button.
				openWithButton.moveBelow(null);
				openWithButton.getParent().layout();
			}
		}
	}

	protected void updateButtonsEnableState(IStatus status) {
		super.updateButtonsEnableState(status);
		if (openWithButton != null && !openWithButton.isDisposed()) {
			openWithButton.setEnabled(!status.matches(IStatus.ERROR) && getSelectedItems().size() == 1);
		}
	}

	private IAdaptable getSelectedAdaptable() {
		IStructuredSelection s = getSelectedItems();
		if (s.size() != 1) {
			return null;
		}
		Object selectedElement = s.getFirstElement();
		if (selectedElement instanceof IAdaptable) {
			return (IAdaptable) selectedElement;
		}
		return null;
	}

	private IWorkbenchPage getActivePage() {
		IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		if (activeWorkbenchWindow == null) {
			return null;
		}
		return activeWorkbenchWindow.getActivePage();
	}

	private void showOpenWithMenu(final Composite openComposite) {
		IWorkbenchPage activePage = getActivePage();
		if (activePage == null) {
			return;
		}
		IAdaptable selectedAdaptable = getSelectedAdaptable();
		if (selectedAdaptable == null) {
			return;
		}

		OpenWithMenu openWithMenu = new OpenWithMenu(activePage, selectedAdaptable) {
			@SuppressWarnings("synthetic-access")
			protected void openEditor(IEditorDescriptor editorDescriptor, boolean openUsingDescriptor) {
				computeResult();
				setResult(Collections.EMPTY_LIST);
				close();
				super.openEditor(editorDescriptor, openUsingDescriptor);
			}
		};

		Menu menu = new Menu(openComposite.getParent());
		Control c = openComposite;
		Point p = c.getLocation();
		p.y = p.y + c.getSize().y;
		p = c.getParent().toDisplay(p);

		menu.setLocation(p);
		openWithMenu.fill(menu, -1);
		menu.setVisible(true);
	}

	/**
	 * A label provider for ResourceDecorator objects. It creates labels with a
	 * resource full path for duplicates. It uses the Platform UI label
	 * decorator for providing extra resource info.
	 */
	protected class EntityLabelProvider extends LabelProvider implements ILabelProviderListener, IStyledLabelProvider {

		// Need to keep our own list of listeners
		private ListenerList listeners = new ListenerList();

		private WorkbenchLabelProvider _provider = new WorkbenchLabelProvider();

		/**
		 * Creates a new instance of the class
		 */
		public EntityLabelProvider() {
			super();
			_provider.addListener(this);
		}
		
		protected String removeExtension(String str) {
			String finalStr = str;
			int dotIndex = finalStr.lastIndexOf('.');
			if (dotIndex != -1) {
				finalStr = finalStr.substring(0, dotIndex);
			}
			return finalStr;
		}

		public Image getImage(Object element) {
			if (!(element instanceof IResource)) {
				return super.getImage(element);
			}

			Image image;
			IResource res = (IResource) element;
			String extension = res.getFileExtension();
			if ("eomodeld".equals(extension)) {
				image = Activator.getDefault().getImageRegistry().get(Activator.EOMODEL_ICON);
			} else if ("plist".equals(extension)) {
				image = Activator.getDefault().getImageRegistry().get(Activator.EOENTITY_ICON);
			} else if ("fspec".equals(extension)) {
				image = Activator.getDefault().getImageRegistry().get(Activator.EOFETCHSPEC_ICON);
			} else if ("storedProcedure".equals(extension)) {
				image = Activator.getDefault().getImageRegistry().get(Activator.EOSTOREDPROCEDURE_ICON);
			} else {
				image = _provider.getImage(element);
			}
			return image;
		}

		public String getText(Object element) {
			if (!(element instanceof IResource)) {
				return super.getText(element);
			}

			IResource res = (IResource) element;

			String str = res.getName();
			str = removeExtension(str);

			// extra info for duplicates
			if (isDuplicateElement(element)) {
				str = str + " - " + removeExtension(res.getParent().getFullPath().makeRelative().toString()); //$NON-NLS-1$
			}

			return str;
		}

		public StyledString getStyledText(Object element) {
			if (!(element instanceof IResource)) {
				return new StyledString(super.getText(element));
			}

			IResource res = (IResource) element;

			StyledString str = new StyledString(removeExtension(res.getName()));

			// extra info for duplicates
			if (isDuplicateElement(element)) {
				str.append(" - ", StyledString.QUALIFIER_STYLER); //$NON-NLS-1$
				str.append(removeExtension(res.getParent().getFullPath().makeRelative().toString()), StyledString.QUALIFIER_STYLER);
			}

			// Debugging:
			// int pathDistance = pathDistance(res.getParent());
			// if (pathDistance != Integer.MAX_VALUE / 2) {
			// if (pathDistance > Integer.MAX_VALUE / 4)
			// str.append(" (" + (pathDistance - Integer.MAX_VALUE / 4) +
			// " folders up from current selection)",
			// StyledString.QUALIFIER_STYLER);
			// else
			// str.append(" (" + pathDistance +
			// " folders down from current selection)",
			// StyledString.QUALIFIER_STYLER);
			// }

			return str;
		}

		public void dispose() {
			_provider.removeListener(this);
			_provider.dispose();

			super.dispose();
		}

		public void addListener(ILabelProviderListener listener) {
			listeners.add(listener);
		}

		public void removeListener(ILabelProviderListener listener) {
			listeners.remove(listener);
		}

		public void labelProviderChanged(LabelProviderChangedEvent event) {
			Object[] l = listeners.getListeners();
			for (int i = 0; i < listeners.size(); i++) {
				((ILabelProviderListener) l[i]).labelProviderChanged(event);
			}
		}

	}

	/**
	 * A label provider for details of ResourceItem objects.
	 */
	protected class EntityDetailsLabelProvider extends EntityLabelProvider {
		public Image getImage(Object element) {
			if (!(element instanceof IResource)) {
				return super.getImage(element);
			}

			IResource parent = ((IResource) element).getParent();
			return super.getImage(parent);
		}

		public String getText(Object element) {
			if (!(element instanceof IResource)) {
				return super.getText(element);
			}

			IResource parent = ((IResource) element).getParent();

			if (parent.getType() == IResource.ROOT) {
				// Get readable name for workspace root ("Workspace"), without
				// duplicating language-specific string here.
				return null;
			}
			
			return removeExtension(parent.getFullPath().makeRelative().toString());
		}

		public void labelProviderChanged(LabelProviderChangedEvent event) {
			Object[] l = super.listeners.getListeners();
			for (int i = 0; i < super.listeners.size(); i++) {
				((ILabelProviderListener) l[i]).labelProviderChanged(event);
			}
		}
	}

}
