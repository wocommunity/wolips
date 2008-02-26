package tk.eclipse.plugin.jseditor.launch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.objectstyle.wolips.baseforuiplugins.utils.ListContentProvider;

import tk.eclipse.plugin.htmleditor.HTMLPlugin;

/**
 * The table component to edit JavaScript libraries.
 * 
 * @author Naoki Takezoe
 */
public class JavaScriptLibraryTable {
	
	public static final String PREFIX = "entry:";
	
	private TableViewer _tableViewer;
	private List<Object> _tableModel = new ArrayList<Object>();
	
	private Composite _composite;
	private Button _add;
	private Button _addExternal;
	private Button _remove;
	private Button _up;
	private Button _down;
	
	/**
	 * The constructor.
	 * 
	 * @param parent the parent component
	 */
	public JavaScriptLibraryTable(final Composite parent){
		_composite = new Composite(parent, SWT.NULL);
		_composite.setLayout(new GridLayout(2, false));
		_composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		// list
		_tableViewer = new TableViewer(_composite);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 250;
		_tableViewer.getTable().setLayoutData(gd);
		_tableViewer.getTable().addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				updateButtons();
			}
		});
		_tableViewer.setContentProvider(new ListContentProvider());
		_tableViewer.setLabelProvider(new ITableLabelProvider(){
			public Image getColumnImage(Object element, int columnIndex) {
				if(element instanceof File){
					return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_JAR_EXT);
				} else if(element instanceof IFile){
					return HTMLPlugin.getDefault().getImageRegistry().get(HTMLPlugin.ICON_JAR);
				}
				return null;
			}
			public String getColumnText(Object element, int columnIndex) {
				if(element instanceof File){
					return ((File)element).getAbsolutePath();
				} else if(element instanceof IFile){
					return ((IFile)element).getFullPath().toString();
				}
				return element.toString();
			}
			public void addListener(ILabelProviderListener listener) {
			}
			public void dispose() {
			}
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}
			public void removeListener(ILabelProviderListener listener) {
			}
		});
		_tableViewer.setInput(_tableModel);
		
		// buttons
		Composite buttons = new Composite(_composite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		buttons.setLayout(layout);
		buttons.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));
		
		_add = new Button(buttons, SWT.PUSH);
		_add.setText(HTMLPlugin.getResourceString("Button.Add"));
		_add.setLayoutData(createButtonGridData());
		_add.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				IWorkspaceRoot wsroot = ResourcesPlugin.getWorkspace().getRoot();
				ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
						parent.getShell(),
						new WorkbenchLabelProvider(), 
						new WorkbenchContentProvider());
				
				dialog.setTitle(HTMLPlugin.getResourceString("JavaScriptPropertyPage.ChooseJavaScript"));
				dialog.setMessage(HTMLPlugin.getResourceString("JavaScriptPropertyPage.ChooseJavaScript.Description"));
				dialog.setInput(wsroot);
				dialog.setValidator(new ISelectionStatusValidator(){
					private IStatus okStatus = new Status(Status.OK, HTMLPlugin.getDefault().getPluginId(), Status.OK, "", null);
					private IStatus ngStatus = new Status(Status.ERROR, HTMLPlugin.getDefault().getPluginId(), Status.ERROR, "", null);
					
					public IStatus validate(Object[] selection) {
						for(int i=0;i<selection.length;i++){
							if(!(selection[i] instanceof IFile)){
								return ngStatus;
							}
							if(!((IFile)selection[i]).getName().endsWith(".js")){
								return ngStatus;
							}
							if(_tableModel.contains(selection[i])){
								return ngStatus;
							}
						}
						if(selection.length==0){
							return ngStatus;
						}
						return okStatus;
					}
				});
				if (dialog.open() == Dialog.OK) {
					Object[] results = dialog.getResult();
					for(int i=0;i<results.length;i++){
						_tableModel.add(results[i]);
					}
					_tableViewer.refresh();
					modelChanged();
				}
			}
		});
		
		_addExternal = new Button(buttons, SWT.PUSH);
		_addExternal.setText(HTMLPlugin.getResourceString("Button.AddExternal"));
		_addExternal.setLayoutData(createButtonGridData());
		_addExternal.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				FileDialog dialog = new FileDialog(parent.getShell(), SWT.OPEN|SWT.MULTI);
				dialog.setFilterExtensions(new String[]{"*.js"});
				String result = dialog.open();
				if(result!=null){
					String dir = dialog.getFilterPath();
					String[] fileNames = dialog.getFileNames();
					for(int i=0;i<fileNames.length;i++){
						_tableModel.add(new File(dir, fileNames[i]));
					}
					_tableViewer.refresh();
					modelChanged();
				}
			}
		});
		
		_remove = new Button(buttons, SWT.PUSH);
		_remove.setText(HTMLPlugin.getResourceString("Button.Remove"));
		_remove.setLayoutData(createButtonGridData());
		_remove.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				IStructuredSelection sel = (IStructuredSelection)_tableViewer.getSelection();
				_tableModel.removeAll(sel.toList());
				updateButtons();
				_tableViewer.refresh();
				modelChanged();
			}
		});
		
		_up = new Button(buttons, SWT.PUSH);
		_up.setText(HTMLPlugin.getResourceString("Button.Up"));
		_up.setLayoutData(createButtonGridData());
		_up.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				int index = _tableViewer.getTable().getSelectionIndex();
				if(index > 0){
					_tableModel.add(index, _tableModel.remove(index - 1));
					_tableViewer.refresh();
					modelChanged();
					updateButtons();
				}
			}
		});
		
		_down = new Button(buttons, SWT.PUSH);
		_down.setText(HTMLPlugin.getResourceString("Button.Down"));
		_down.setLayoutData(createButtonGridData());
		_down.addSelectionListener(new SelectionAdapter(){
			@Override
      public void widgetSelected(SelectionEvent evt){
				int index = _tableViewer.getTable().getSelectionIndex();
				if(index < _tableModel.size() - 1){
					_tableModel.add(index, _tableModel.remove(index + 1));
					_tableViewer.refresh();
					modelChanged();
					updateButtons();
				}
			}
		});
		
		updateButtons();
	}
	
	/**
	 * Updates button status.
	 */
	protected void updateButtons(){
		_remove.setEnabled(_tableViewer.getTable().getSelectionCount() != 0);
		_up.setEnabled(_tableViewer.getTable().getSelectionCount() == 1 &&
				_tableViewer.getTable().getSelectionIndex() > 0);
		_down.setEnabled(_tableViewer.getTable().getSelectionCount() == 1 &&
				_tableViewer.getTable().getSelectionIndex() < _tableModel.size() - 1);
	}
	
	/**
	 * This method would be invoked when the table model changed.
	 * <p>
	 * Please override this method at the subclass, if you want to
	 * do any processing by the table model modifications.
	 */
	protected void modelChanged(){
	}
	
	/**
	 * Returns the control.
	 * 
	 * @return the control
	 */
	public Control getControl(){
		return _composite;
	}
	
	/**
	 * Returns the table model.
	 * 
	 * @return the table model
	 */
	public List getModel(){
		return _tableModel;
	}
	
	/**
	 * Refreshes the <code>TableViewer</code>.
	 */
	public void refresh(){
		_tableViewer.refresh();
	}
	
	/**
	 * Creates <code>GridData</code> for buttons.
	 * 
	 * @return the <code>GridData</code> which has a widthHint(=100)
	 */
	private static GridData createButtonGridData(){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 100;
		return gd;
	}	
	
}
