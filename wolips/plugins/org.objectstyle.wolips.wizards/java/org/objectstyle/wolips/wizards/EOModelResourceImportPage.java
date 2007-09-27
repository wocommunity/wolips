package org.objectstyle.wolips.wizards;

import java.io.File;
import java.util.HashMap;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.WizardDataTransferPage;
import org.eclipse.ui.model.WorkbenchContentProvider;


public class EOModelResourceImportPage extends WizardDataTransferPage {
	HashMap <String,String> _modelPaths; //key = file name, value = full path to file including file name
	Table _table;
	static Image _eomodelIcon;
	static boolean _requiresModel;

	public EOModelResourceImportPage(String name) {
		this(name, false);
	}

	public EOModelResourceImportPage(String name, boolean requiresModel) {
		super(name);
		_modelPaths = new HashMap <String, String>();
		_table = null;
		ImageDescriptor id = WizardsPlugin.EOMODEL_ICON();
		_eomodelIcon = id.createImage();
		_requiresModel = requiresModel;
	}

	//key = file name, value = full path to file including file name
	public HashMap <String,String> getModelPaths() {
		return _modelPaths;
	}

	protected void createSourceGroup(Composite parent) {

		//Selected models table
		_table = new Table (parent, SWT.VIRTUAL | SWT.FULL_SELECTION | SWT.BORDER | SWT.MULTI);
		_table.setFont(parent.getFont());
		_table.setHeaderVisible(true);
		_table.setLinesVisible(true);

        GridLayout tablelayout = new GridLayout();
        tablelayout.numColumns = 1;
        _table.setLayout(tablelayout);
		GridData data= new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		data.grabExcessHorizontalSpace = true;
		_table.setLayoutData(data);

		//layout for buttons
        Composite containerGroup = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 3;
        containerGroup.setLayout(layout);
        containerGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.GRAB_HORIZONTAL));
        containerGroup.setFont(parent.getFont());

        //Browse button
        Button browseButton = new Button(containerGroup, SWT.PUSH);
        browseButton.setText("Browse...");
        browseButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        browseButton.addSelectionListener(new EOModelSelectionListener());
        browseButton.setFont(parent.getFont());
        setButtonLayoutData(browseButton);

        //Delete button
        Button tableRemoveButton = new Button(containerGroup, SWT.PUSH);
        tableRemoveButton.setText("Delete");
        tableRemoveButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
        tableRemoveButton.addSelectionListener(new TableRemoveSelectionListener());
        tableRemoveButton.setFont(parent.getFont());
        setButtonLayoutData(tableRemoveButton);


	}

	@Override
    protected void createOptionsGroup(Composite parent) {
        //Override default options group and display nothing
    }

    /** (non-Javadoc)
     * Method declared on IDialogPage.
     */
	public void createControl(Composite parent) {

        initializeDialogUnits(parent);

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_FILL
                | GridData.HORIZONTAL_ALIGN_FILL));
        composite.setSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));
        composite.setFont(parent.getFont());

        createSourceGroup(composite);

        createOptionsGroup(composite);

        if (_requiresModel) {
        	setPageComplete(false);
        }

        restoreWidgetValues();
        updateWidgetEnablements();
        setTitle(Messages.getString("EOModelResourceImportPage.title"));
        setMessage(Messages.getString("EOModelResourceImportPage.description"));
        setErrorMessage(null);	// should not initially have error message

        setControl(composite);
    }

	protected ITreeContentProvider getFileProvider() {
		// TODO Auto-generated method stub
		return new WorkbenchContentProvider();
	}

	protected ITreeContentProvider getFolderProvider() {
		// TODO Auto-generated method stub
		return new WorkbenchContentProvider();
	}

	public void updatePageComplete() {
		if (_requiresModel && getModelPaths().size() > 1) {
			setMessage(null);
			setErrorMessage("EOModel not selected");
			setPageComplete(false);
		}

		setPageComplete(true);
		setMessage(Messages.getString("EOModelResourceImportPage.description"));
		setErrorMessage(null);
	}

	//Responder for EOModel browse button
	class EOModelSelectionListener extends SelectionAdapter {
		@SuppressWarnings("synthetic-access")
		public void widgetSelected(SelectionEvent e) {
			FileDialog ddialog = new FileDialog(getShell(), SWT.OPEN | SWT.MULTI);
			ddialog.setFileName("myModel.eomodeld");
			ddialog.setFilterExtensions(new String[]{"*.eomodeld"});
			ddialog.open();
			String[] paths = ddialog.getFileNames();
			String rootPath = ddialog.getFilterPath();
			for (String aPath : paths) {
				if (!aPath.endsWith(".eomodeld")){
					displayErrorDialog(aPath+" is not an EOModel file and will not be copied");
					break;
				}
				String fullPath = rootPath+File.separator+aPath;
				if (!_modelPaths.values().contains(fullPath)) {
					_modelPaths.put(aPath, fullPath);
					updatePageComplete();
					if (_table != null) {
						TableItem item = new TableItem(_table, SWT.NATIVE);
						item.setImage(_eomodelIcon);
						item.setText(fullPath);
					}
				}
			}
		}
	}

	class TableRemoveSelectionListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent e) {
			if (_table != null) {
				int[] selectedIndexes = _table.getSelectionIndices();
				for (int anIndex : selectedIndexes) {
					TableItem anItem = _table.getItem(anIndex);
					//key is file name, so extract from path
					String key = anItem.getText().substring(anItem.getText().lastIndexOf(File.separator)+1);
					_modelPaths.remove(key);
				}
				_table.remove(selectedIndexes);
			}
		}
	}

	@Override
	protected boolean allowNewContainerName() {
		// TODO Auto-generated method stub
		return false;
	}

	public void handleEvent(Event arg0) {
		// TODO Auto-generated method stub

	}

}
