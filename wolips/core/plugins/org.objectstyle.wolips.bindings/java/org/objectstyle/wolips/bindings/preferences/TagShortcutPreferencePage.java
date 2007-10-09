package org.objectstyle.wolips.bindings.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.objectstyle.wolips.bindings.Activator;
import org.objectstyle.wolips.bindings.wod.TagShortcut;
import org.objectstyle.wolips.core.CorePlugin;
import org.objectstyle.wolips.preferences.TableViewerSupport;

/**
 * The preference page to add / edit / remove TagShortcuts.
 * 
 * @author Naoki Takezoe
 * @see tk.eclipse.plugin.htmleditor.tasktag.ITagShortcutDetector
 * @see tk.eclipse.plugin.htmleditor.tasktag.TagShortcut
 * @see tk.eclipse.plugin.htmleditor.HTMLProjectBuilder
 */
public class TagShortcutPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {
	private TableViewer _viewer;

	private List<TagShortcut> _model = new ArrayList<TagShortcut>();

	private List<TagShortcut> _oldModel = new ArrayList<TagShortcut>();

	public TagShortcutPreferencePage() {
		setTitle("Tag Shortcuts");
		setDescription("Tag shortcuts are for use with inline bindings. You must have these shortcuts declared at runtime as well. The definitions here are for html validation.\n\nIf you are using WOTagProcessor, you can define shortcuts that map additional keys like 'edit'=>'ERXWOTemplate','templateName=edit'.");
	}

	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return Activator.getDefault().getPreferenceStore();
	}

	@Override
	protected Control createContents(Composite parent) {
		TableViewerSupport support = new TableViewerSupport(_model, parent) {

			@Override
			protected void initTableViewer(TableViewer viewer) {
				Table table = viewer.getTable();

				TableColumn col1 = new TableColumn(table, SWT.LEFT);
				col1.setText("Shortcut");
				col1.setWidth(100);

				TableColumn col2 = new TableColumn(table, SWT.LEFT);
				col2.setText("Actual Tag");
				col2.setWidth(150);

				TableColumn col3 = new TableColumn(table, SWT.LEFT);
				col3.setText("Attributes");
				col3.setWidth(200);
			}

			@Override
			protected Object doAdd() {
				TagShortcutDialog dialog = new TagShortcutDialog(getShell());
				if (dialog.open() == Dialog.OK) {
					return dialog.getTagShortcut();
				}
				return null;
			}

			@Override
			protected void doEdit(Object obj) {
				TagShortcut _tagShortcut = (TagShortcut) obj;
				TagShortcutDialog dialog = new TagShortcutDialog(getShell(), _tagShortcut);
				if (dialog.open() == Dialog.OK) {
					TagShortcut newElement = dialog.getTagShortcut();
					_tagShortcut.setShortcut(newElement.getShortcut());
					_tagShortcut.setActual(newElement.getActual());
					_tagShortcut.setAttributesAsString(newElement.getAttributesAsString());
				}
			}

			@Override
			protected ITableLabelProvider createLabelProvider() {
				return new ITableLabelProvider() {

					public Image getColumnImage(Object tagShortcut, int columnIndex) {
						return null;
					}

					public String getColumnText(Object tagShortcut, int columnIndex) {
						switch (columnIndex) {
						case 0:
							return ((TagShortcut) tagShortcut).getShortcut();
						case 1:
							return ((TagShortcut) tagShortcut).getActual();
						case 2:
							return ((TagShortcut) tagShortcut).getAttributesAsString();
						default:
							return tagShortcut.toString();
						}
					}

					public void addListener(ILabelProviderListener listener) {
						// DO NOTHING
					}

					public void dispose() {
						// DO NOTHING
					}

					public boolean isLabelProperty(Object tagShortcut, String property) {
						return false;
					}

					public void removeListener(ILabelProviderListener listener) {
						// DO NOTHING
					}
				};
			}

		};

		_viewer = support.getTableViewer();
		_model.addAll(TagShortcut.fromPreferenceString(getPreferenceStore().getString(PreferenceConstants.TAG_SHORTCUTS_KEY)));
		syncModels();
		_viewer.refresh();

		return support.getControl();
	}

	@Override
	protected void performDefaults() {
		_model.clear();
		_model.addAll(TagShortcut.fromPreferenceString(getPreferenceStore().getDefaultString(PreferenceConstants.TAG_SHORTCUTS_KEY)));
		_viewer.refresh();
		processChange();
	}

	@Override
	public boolean performOk() {
		getPreferenceStore().setValue(PreferenceConstants.TAG_SHORTCUTS_KEY, TagShortcut.toPreferenceString(_model));
		processChange();
		CorePlugin.getDefault().savePluginPreferences();
		return true;
	}

	private void syncModels() {
		try {
			_oldModel.clear();
			for (int i = 0; i < _model.size(); i++) {
				_oldModel.add(_model.get(i).clone());
			}
		} catch (Exception ex) {
			CorePlugin.getDefault().log(ex);
		}
	}

	public void init(IWorkbench workbench) {
  	  // DO NOTHING
	}

	private void processChange() {
		if (TagShortcut.hasChange(_oldModel, _model)) {
			syncModels();
			// try {
			// IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			// IProject[] projects = root.getProjects();
			// for (int i = 0; i < projects.length; i++) {
			// IProjectAdapter wolipsProjectAdaptor = (IProjectAdapter)
			// projects[i].getAdapter(IProjectAdapter.class);
			// if (wolipsProjectAdaptor != null) {
			// projects[i].build(IncrementalProjectBuilder.CLEAN_BUILD, new
			// NullProgressMonitor());
			// }
			// }
			// }
			// catch (Exception ex) {
			// Activator.getDefault().log(ex);
			// }
		}
	}

	/**
	 * The dialog to add / edit TagShortcuts.
	 */
	private class TagShortcutDialog extends Dialog {
		private Text _shortcutTag;

		private Text _actualTag;

		private Text _attributes;

		private TagShortcut _tagShortcut;

		public TagShortcutDialog(Shell parentShell) {
			super(parentShell);
			setShellStyle(getShellStyle() | SWT.RESIZE);
		}

		public TagShortcutDialog(Shell parentShell, TagShortcut tagShortcut) {
			super(parentShell);
			_tagShortcut = tagShortcut;
		}

		@Override
		protected Point getInitialSize() {
			Point size = super.getInitialSize();
			size.x = 300;
			return size;
		}

		@Override
		protected Control createDialogArea(Composite parent) {
			getShell().setText("Tag Shortcut");

			Composite composite = new Composite(parent, SWT.NULL);
			composite.setLayoutData(new GridData(GridData.FILL_BOTH));
			composite.setLayout(new GridLayout(2, false));

			Label label = new Label(composite, SWT.NULL);
			label.setText("Shortcut");

			_shortcutTag = new Text(composite, SWT.BORDER);
			if (_tagShortcut != null) {
				_shortcutTag.setText(_tagShortcut.getShortcut());
			}
			_shortcutTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			label = new Label(composite, SWT.NULL);
			label.setText("Actual Tag");

			_actualTag = new Text(composite, SWT.BORDER);
			if (_tagShortcut != null) {
				_actualTag.setText(_tagShortcut.getActual());
			}
			_actualTag.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			label = new Label(composite, SWT.NULL);
			label.setText("Attributes");

			_attributes = new Text(composite, SWT.BORDER);
			if (_tagShortcut != null) {
				_attributes.setText(_tagShortcut.getAttributesAsString());
			}
			_attributes.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

			return composite;
		}

		@Override
		protected void okPressed() {
			if (_shortcutTag.getText().length() == 0) {
				MessageDialog.openError(getParentShell(), "Shortcut Required", "You must set a shortcut tag name.");
				return;
			}
			if (_actualTag.getText().length() == 0) {
				MessageDialog.openError(getParentShell(), "Actual Tag Required", "You must set the actual tag name.");
				return;
			}

			String shortcut = _shortcutTag.getText();
			String actual = _actualTag.getText();
			String attributes = _attributes.getText();
			_tagShortcut = new TagShortcut(shortcut, actual, attributes);

			super.okPressed();
		}

		public TagShortcut getTagShortcut() {
			return _tagShortcut;
		}
	}

}
